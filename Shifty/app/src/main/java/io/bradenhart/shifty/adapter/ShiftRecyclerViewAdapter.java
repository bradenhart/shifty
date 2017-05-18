package io.bradenhart.shifty.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.bradenhart.shifty.R;
import io.bradenhart.shifty.activity.ShiftActivity;
import io.bradenhart.shifty.data.DatabaseManager;
import io.bradenhart.shifty.data.ShiftyContract;
import io.bradenhart.shifty.domain.Shift;
import io.bradenhart.shifty.util.DateUtil;
import io.bradenhart.shifty.util.Utils;

/**
 * Created by bradenhart on 8/05/17.
 */
// Adapter for displaying Shifts in a WorkWeek
public class ShiftRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<Shift> shifts;
    private int itemHeight, progressWidth;
    private WorkWeekRecyclerViewAdapter parentAdapter;
    private int parentPos;
    private Cursor cursor;

    public ShiftRecyclerViewAdapter(Context context, List<Shift> shifts) {
        this.context = context;
        this.shifts = shifts;
        this.itemHeight = context.getResources().getDimensionPixelSize(R.dimen.workweek_item_height);
        this.progressWidth = context.getResources().getDimensionPixelSize(R.dimen.workweek_shift_progress_width);
    }

    public ShiftRecyclerViewAdapter(Context context, String datetime) {
        this.context = context;
        this.cursor = getShiftsInWeek(datetime);
        this.itemHeight = context.getResources().getDimensionPixelSize(R.dimen.workweek_item_height);
        this.progressWidth = context.getResources().getDimensionPixelSize(R.dimen.workweek_shift_progress_width);
    }

    private Cursor getShiftsInWeek(String weekDate) {
        // get the ISO8601 formatted string for Monday 00:00 of the current week
        String selection = ShiftyContract.Shift.COLUMN_WEEK_START_DATETIME + " = ?";
        String[] selectionArgs = new String[] { weekDate };

        return context.getContentResolver().query(
                ShiftyContract.Shift.CONTENT_URI, // query Shift table (/shift)
                null, // get all columns
                selection, // get all shifts from this week onwards
                selectionArgs,
                ShiftyContract.Shift.COLUMN_SHIFT_START_DATETIME + " asc" // order by shift start time, earliest to latest
        );
    }

    public void removeShift(int pos) {
        shifts.remove(pos);
    }

    public void setParentPos(int pos) {
        this.parentPos = pos;
    }

    public void setParentAdapter(WorkWeekRecyclerViewAdapter parentAdapter) {
        this.parentAdapter = parentAdapter;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.section_workweek_item, parent, false);
        return new ShiftViewHolder(context, view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final ShiftViewHolder shiftHolder = (ShiftViewHolder) holder;

        cursor.moveToPosition(position);
        // get column index values
        int idCol = cursor.getColumnIndex(ShiftyContract.Shift._ID);
        int shiftStartCol = cursor.getColumnIndex(ShiftyContract.Shift.COLUMN_SHIFT_START_DATETIME);
        int shiftEndCol = cursor.getColumnIndex(ShiftyContract.Shift.COLUMN_SHIFT_END_DATETIME);

        // get values from cursor
        String shiftStart = cursor.getString(shiftStartCol);
        String shiftEnd = cursor.getString(shiftEndCol);

        String dayOfMonth = DateUtil.getDayOfMonth(shiftStart, DateUtil.FMT_ISO_8601_DATETIME);
        String dayOfWeek = DateUtil.getWeekday(shiftStart, DateUtil.FMT_ISO_8601_DATETIME, DateUtil.FMT_WEEKDAY_FULL);
        String startTime = DateUtil.getTime(shiftStart, DateUtil.FMT_ISO_8601_DATETIME, DateUtil.FMT_TIME_SHORT);
        String endTime = DateUtil.getTime(shiftEnd, DateUtil.FMT_ISO_8601_DATETIME, DateUtil.FMT_TIME_SHORT);

        shiftHolder.view.setTag(cursor.getLong(idCol));
        shiftHolder.dayOfMonthTV.setText(dayOfMonth);
        shiftHolder.dayOfWeekTV.setText(dayOfWeek);
        shiftHolder.startTimeTV.setText(startTime);
        shiftHolder.endTimeTV.setText(endTime);
//
        double shiftLength = DateUtil.getHoursBetween(shiftStart, shiftEnd, DateUtil.FMT_ISO_8601_DATETIME);
        double paidHours = shiftLength <= 5.0 ? shiftLength : shiftLength - 0.5;
        
        shiftHolder.paidHoursTV.setText(String.format(Locale.ENGLISH, "%.2f hrs", paidHours));

        double progress = DateUtil.getShiftProgress(shiftStart, shiftEnd, DateUtil.FMT_ISO_8601_DATETIME);
        Log.e("progress", startTime + "   "  + progress);
        int percentHeight = (int) Math.ceil(itemHeight * progress);
        shiftHolder.shiftProgressBar.setLayoutParams(new LinearLayout.LayoutParams(progressWidth, percentHeight));
    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }


    class ShiftViewHolder extends RecyclerView.ViewHolder {

        private Context context;
        Shift shift;
        View view;
        @BindView(R.id.layout_shift_item)
        LinearLayout itemLayout;
        @BindView(R.id.textview_day_of_month)
        TextView dayOfMonthTV;
        @BindView(R.id.textview_day_of_week)
        TextView dayOfWeekTV;
        @BindView(R.id.textview_shift_start_time)
        TextView startTimeTV;
        @BindView(R.id.textview_shift_end_time)
        TextView endTimeTV;
        @BindView(R.id.view_shift_progress)
        View shiftProgressBar;

        @BindView(R.id.layout_shift_item_options)
        LinearLayout optionsLayout;
        @BindView(R.id.button_edit_shift)
        ImageButton editButton;
        @BindView(R.id.textview_paid_hours)
        TextView paidHoursTV;
        @BindView(R.id.button_delete_shift)
        ImageButton deleteButton;
        @BindView(R.id.button_close_options)
        ImageButton closeButton;

        public ShiftViewHolder(Context context, View itemView) {
            super(itemView);
            this.context = context;
            this.view = itemView;
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.layout_shift_item)
        void onClickItemLayout() {
            Log.e("shift _id: ", view.getTag() + "");
            if (optionsLayout.getVisibility() == View.GONE) {
                optionsLayout.setVisibility(View.VISIBLE);
            }
        }

        @OnClick(R.id.button_close_options)
        void onClickCloseButton() {
            if (optionsLayout.getVisibility() == View.VISIBLE) {
                optionsLayout.setVisibility(View.GONE);
            }
        }

        @OnClick(R.id.button_edit_shift)
        void onClickEditButton() {
            ShiftActivity.start(context, ShiftActivity.Mode.EDIT, shift);
        }

        @OnClick(R.id.button_delete_shift)
        void onClickDeleteButton() {
            new AlertDialog.Builder(context)
                    .setTitle("Are you sure?")
                    .setMessage("This will delete the shift for good!")
                    .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            new DatabaseManager(context.getApplicationContext()).deleteShift(shift.getId());
                            removeShift(getAdapterPosition());
                            onClickCloseButton();
                            notifyDataSetChanged();
                            Utils.makeToast(context, "Shift deleted", Toast.LENGTH_LONG);
                            if (shifts.size() == 0) {
                                parentAdapter.removeWorkWeek(parentPos);
                                parentAdapter.notifyDataSetChanged();
                                Utils.makeToast(context, "Workweek deleted", Toast.LENGTH_LONG);
                            }
                        }
                    })
                    .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            onClickCloseButton();
                        }
                    })
                    .show();

        }

    }

}
