package io.bradenhart.shifty.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
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
 * Adapter for displaying shifts in a workWeek.
 *
 * @author bradenhart
 */
public class ShiftRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    // height of the view item
    private int itemHeight;
    // width of progress bar in view item
    private int progressWidth;
    // database data to display in recyclerview
    private Cursor cursor;

    public ShiftRecyclerViewAdapter(Context context) {
        this.context = context;
        // retrieve item height and progress width from dimens
        this.itemHeight = context.getResources().getDimensionPixelSize(R.dimen.workweek_item_height);
        this.progressWidth = context.getResources().getDimensionPixelSize(R.dimen.workweek_shift_progress_width);
    }

    /**
     * Swap the cursor with a new cursor.
     * @param newCursor the new cursor
     */
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

        // move cursor to this view holder's position in the recyclerview
        cursor.moveToPosition(position);
        // get column index values
        int idCol = cursor.getColumnIndex(ShiftyContract.Shift._ID);
        int shiftStartCol = cursor.getColumnIndex(ShiftyContract.Shift.COLUMN_SHIFT_START_DATETIME);
        int shiftEndCol = cursor.getColumnIndex(ShiftyContract.Shift.COLUMN_SHIFT_END_DATETIME);

        // get values from cursor
        String shiftStart = cursor.getString(shiftStartCol);
        String shiftEnd = cursor.getString(shiftEndCol);

        // get formatted datetime strings
        String dayOfMonth = DateUtils.getDayOfMonth(shiftStart, DateUtils.FMT_ISO_8601_DATETIME);
        String dayOfWeek = DateUtils.getWeekday(shiftStart, DateUtils.FMT_ISO_8601_DATETIME, DateUtils.FMT_WEEKDAY_FULL);
        String startTime = DateUtils.getTime(shiftStart, DateUtils.FMT_ISO_8601_DATETIME, DateUtils.FMT_TIME_SHORT);
        String endTime = DateUtils.getTime(shiftEnd, DateUtils.FMT_ISO_8601_DATETIME, DateUtils.FMT_TIME_SHORT);

        // display date and time information for current shift
        shiftHolder.root.setTag(cursor.getLong(idCol));
        shiftHolder.dayOfMonthTV.setText(dayOfMonth);
        shiftHolder.dayOfWeekTV.setText(dayOfWeek);
        shiftHolder.startTimeTV.setText(startTime);
        shiftHolder.endTimeTV.setText(endTime);

        // calculate paid hours and display it
        double shiftLength = DateUtils.getHoursBetween(shiftStart, shiftEnd, DateUtils.FMT_ISO_8601_DATETIME);
        double paidHours = shiftLength <= 5.0 ? shiftLength : shiftLength - 0.5;
        
        shiftHolder.paidHoursTV.setText(String.format(Locale.ENGLISH, "%.2f hrs", paidHours));

        // calculate progress through the current shift and display it
        double progress = DateUtils.getShiftProgress(shiftStart, shiftEnd, DateUtils.FMT_ISO_8601_DATETIME);
        int percentHeight = (int) Math.ceil(itemHeight * progress);
        shiftHolder.shiftProgressBar.setLayoutParams(new LinearLayout.LayoutParams(progressWidth, percentHeight));
    }

    @Override
    public int getItemCount() {
        if (null == cursor) return 0;
        return cursor.getCount();
    }


    /**
     * ViewHolder for displaying a shift in the recyclerview
     */
    class ShiftViewHolder extends RecyclerView.ViewHolder {

        private Context context;
        // the root view of this viewholder
        View root;

        /* shift information */
        // the layout containing the shift item data
        @BindView(R.id.layout_shift_item)
        LinearLayout itemLayout;
        // displays the day of the month
        @BindView(R.id.textview_day_of_month)
        TextView dayOfMonthTV;
        // displays the day of the week
        @BindView(R.id.textview_day_of_week)
        TextView dayOfWeekTV;
        // displays the start time
        @BindView(R.id.textview_shift_start_time)
        TextView startTimeTV;
        // displays the end time
        @BindView(R.id.textview_shift_end_time)
        TextView endTimeTV;
        // displays the progress through the shift
        @BindView(R.id.view_shift_progress)
        View shiftProgressBar;

        /* options */
        // the layout containing options for the shift item e.g. edit, delete
        @BindView(R.id.layout_shift_item_options)
        LinearLayout optionsLayout;
        // allows user to edit the shift
        @BindView(R.id.button_edit_shift)
        ImageButton editButton;
        // displays the number of paid hours
        @BindView(R.id.textview_paid_hours)
        TextView paidHoursTV;
        // allows the user to delete the shift
        @BindView(R.id.button_delete_shift)
        ImageButton deleteButton;
        // hides this layout
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
            // display the item's options when the item is clicked
            if (optionsLayout.getVisibility() == View.GONE) {
                optionsLayout.setVisibility(View.VISIBLE);
            }
        }

        @OnClick(R.id.button_close_options)
        void onClickCloseButton() {
            // hide the item's options when the close button is clicked
            if (optionsLayout.getVisibility() == View.VISIBLE) {
                optionsLayout.setVisibility(View.GONE);
            }
        }

        @OnClick(R.id.button_edit_shift)
        void onClickEditButton() {
            // close the options before going to the ShiftActivity to edit the shift
            onClickCloseButton();
            ShiftActivity.start(context, ShiftActivity.MODE_EDIT, String.valueOf(root.getTag()));
        }

        @OnClick(R.id.button_delete_shift)
        void onClickDeleteButton() {
            // confirm with the user that they are deleting a shift
            // delete the shift it they accept
            // close the options if they cancel this action
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
