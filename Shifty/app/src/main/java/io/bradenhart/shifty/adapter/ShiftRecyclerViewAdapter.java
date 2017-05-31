package io.bradenhart.shifty.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
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

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.bradenhart.shifty.R;
import io.bradenhart.shifty.activity.ShiftActivity;
import io.bradenhart.shifty.data.ShiftyContract;
import io.bradenhart.shifty.util.DateUtils;
import io.bradenhart.shifty.util.Utils;

/**
 * Created by bradenhart on 8/05/17.
 */
// Adapter for displaying Shifts in a WorkWeek
public class ShiftRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private int itemHeight, progressWidth;
    private Cursor cursor;

    public ShiftRecyclerViewAdapter(Context context) {
        this.context = context;
        this.itemHeight = context.getResources().getDimensionPixelSize(R.dimen.workweek_item_height);
        this.progressWidth = context.getResources().getDimensionPixelSize(R.dimen.workweek_shift_progress_width);
    }

//    public ShiftRecyclerViewAdapter(Context context, String datetime) {
//        this.context = context;
//        this.cursor = getShiftsInWeek(datetime);
//        this.itemHeight = context.getResources().getDimensionPixelSize(R.dimen.workweek_item_height);
//        this.progressWidth = context.getResources().getDimensionPixelSize(R.dimen.workweek_shift_progress_width);
//    }

//    private Cursor getShiftsInWeek(String weekDate) {
//        // get the ISO8601 formatted string for Monday 00:00 of the current week
//        String selection = ShiftyContract.Shift.COLUMN_WORKWEEK_ID + " = ?";
//        String[] selectionArgs = new String[] { weekDate };
//
//        return context.getContentResolver().query(
//                ShiftyContract.Shift.CONTENT_URI, // query Shift table (/shift)
//                null, // get all columns
//                selection, // get all shifts from this week onwards
//                selectionArgs,
//                ShiftyContract.Shift.COLUMN_SHIFT_START_DATETIME + " asc" // order by shift start time, earliest to latest
//        );
//    }

//    public void removeShift(int pos) {
//        shifts.remove(pos);
//    }
//
//    public void setParentPos(int pos) {
//        this.parentPos = pos;
//    }
//
//    public void setParentAdapter(WorkWeekRecyclerViewAdapter parentAdapter) {
//        this.parentAdapter = parentAdapter;
//    }

    public void swapCursor(Cursor newCursor) {
        if (cursor != null) cursor.close();
        cursor = newCursor;
        notifyDataSetChanged();
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

        String dayOfMonth = DateUtils.getDayOfMonth(shiftStart, DateUtils.FMT_ISO_8601_DATETIME);
        String dayOfWeek = DateUtils.getWeekday(shiftStart, DateUtils.FMT_ISO_8601_DATETIME, DateUtils.FMT_WEEKDAY_FULL);
        String startTime = DateUtils.getTime(shiftStart, DateUtils.FMT_ISO_8601_DATETIME, DateUtils.FMT_TIME_SHORT);
        String endTime = DateUtils.getTime(shiftEnd, DateUtils.FMT_ISO_8601_DATETIME, DateUtils.FMT_TIME_SHORT);

        shiftHolder.root.setTag(cursor.getLong(idCol));
        shiftHolder.dayOfMonthTV.setText(dayOfMonth);
        shiftHolder.dayOfWeekTV.setText(dayOfWeek);
        shiftHolder.startTimeTV.setText(startTime);
        shiftHolder.endTimeTV.setText(endTime);

        double shiftLength = DateUtils.getHoursBetween(shiftStart, shiftEnd, DateUtils.FMT_ISO_8601_DATETIME);
        double paidHours = shiftLength <= 5.0 ? shiftLength : shiftLength - 0.5;
        
        shiftHolder.paidHoursTV.setText(String.format(Locale.ENGLISH, "%.2f hrs", paidHours));

        double progress = DateUtils.getShiftProgress(shiftStart, shiftEnd, DateUtils.FMT_ISO_8601_DATETIME);
        int percentHeight = (int) Math.ceil(itemHeight * progress);
        shiftHolder.shiftProgressBar.setLayoutParams(new LinearLayout.LayoutParams(progressWidth, percentHeight));
    }

    @Override
    public int getItemCount() {
        if (null == cursor) return 0;
        return cursor.getCount();
    }


    class ShiftViewHolder extends RecyclerView.ViewHolder {

        private Context context;
        View root;
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
            this.root = itemView;
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.layout_shift_item)
        void onClickItemLayout() {
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
            onClickCloseButton();
            ShiftActivity.start(context, ShiftActivity.Mode.EDIT, String.valueOf(root.getTag()));
        }

        @OnClick(R.id.button_delete_shift)
        void onClickDeleteButton() {
            new AlertDialog.Builder(context)
                    .setTitle("Are you sure?")
                    .setMessage("This will delete the shift for good!")
                    .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String id = String.valueOf(root.getTag());
                            Uri uri = Uri.withAppendedPath(ShiftyContract.Shift.CONTENT_URI, id);
                            int deleted = context.getContentResolver().delete(uri, null, null);

                            if (deleted > 0) {
                                Utils.makeToast(context, "Shift deleted", Toast.LENGTH_LONG);
                            } else {
                                Utils.makeToast(context, "Failed to delete shift", Toast.LENGTH_LONG);
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
