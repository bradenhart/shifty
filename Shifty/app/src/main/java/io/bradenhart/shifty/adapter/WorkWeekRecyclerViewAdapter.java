package io.bradenhart.shifty.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.bradenhart.shifty.R;
import io.bradenhart.shifty.activity.PayslipActivity;
import io.bradenhart.shifty.data.ShiftyContract;
import io.bradenhart.shifty.domain.Payslip;
import io.bradenhart.shifty.util.DateUtils;
import io.bradenhart.shifty.util.Utils;

import static io.bradenhart.shifty.util.Utils.makeToast;

/**
 * Adapter for displaying workweeks.
 *
 * @author bradenhart
 */
public class WorkWeekRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    // database data to display in recyclerview
    private Cursor cursor;

    public WorkWeekRecyclerViewAdapter(Context context) {
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.section_workweek_base, parent, false);

        return new WorkWeekViewHolder(context, view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        WorkWeekViewHolder weekHolder = (WorkWeekViewHolder) holder;

        // move cursor to this view holder's position in the recyclerview
        cursor.moveToPosition(position);

        // get the id for the workweek
        int idCol = cursor.getColumnIndex(ShiftyContract.Workweek._ID);
        String id = cursor.getString(idCol);

        // set the viewholder's root view tag to the id
        weekHolder.root.setTag(id);

        // get the date for the start of the week
        int weekStartCol = cursor.getColumnIndex(ShiftyContract.Workweek.COLUMN_WEEK_START_DATETIME);
        String weekStart = cursor.getString(weekStartCol);

        // format the date for the header
        SimpleDateFormat iso8601Format = new SimpleDateFormat(DateUtils.FMT_ISO_8601_DATETIME, Locale.ENGLISH);
        SimpleDateFormat headerFormat = new SimpleDateFormat(DateUtils.FMT_DAY_DATE, Locale.ENGLISH);
        try {
            Date date = iso8601Format.parse(weekStart);
            String headerString = headerFormat.format(date);
            weekHolder.header.setText(headerString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        /* set up the adapter and recyclerview contained inside this view holder */
        ShiftRecyclerViewAdapter adapter = new ShiftRecyclerViewAdapter(context);
        // load the shifts for the current workweek
        new ShiftAsyncTask(context, adapter, weekHolder).execute(weekStart);

        weekHolder.recyclerView.setLayoutManager(new LinearLayoutManager(context.getApplicationContext()));
        weekHolder.recyclerView.setAdapter(adapter);
        weekHolder.recyclerView.setHasFixedSize(true);


        // get the total paid hours for the week
        int totalPaidHoursCol = cursor.getColumnIndex(ShiftyContract.Workweek.COLUMN_TOTAL_PAID_HOURS);
        Double totalPaidHours = cursor.getDouble(totalPaidHoursCol);
        Payslip payslip = new Payslip(totalPaidHours);
        weekHolder.weekStartDatetime = weekStart;
        weekHolder.paidHours = totalPaidHours;
        weekHolder.footer.setText(String.format(Locale.ENGLISH, "$%.02f", payslip.getNet()));

    }

    @Override
    public int getItemCount() {
        if (null == cursor) return 0;
        return cursor.getCount();
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

    /**
     * ViewHolder for displaying a workweek in the recyclerview
     */
    class WorkWeekViewHolder extends RecyclerView.ViewHolder {

        private Context context;
        // the start date for this week
        String weekStartDatetime;
        // the total number of paid hours for the week
        Double paidHours;
        // the root view of this viewholder
        View root;
        // the header for the workweek
        @BindView(R.id.textview_workweek_header)
        TextView header;
        // allows the user to delete the workweek and all of its shifts
        @BindView(R.id.button_delete_workweek)
        ImageButton deleteButton;
        // displays all of the shifts in this workweek
        @BindView(R.id.recyclerview_workweek_content)
        RecyclerView recyclerView;
        // the footer for the workweek
        @BindView(R.id.textview_workweek_footer)
        TextView footer;
        // indicates when the view and any data for the view is being loaded
        @BindView(R.id.progressbar_workweek_base)
        ProgressBar progressBar;

        public WorkWeekViewHolder(Context context, View itemView) {
            super(itemView);
            this.context = context;
            this.root = itemView;
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.button_delete_workweek)
        public void onClickDeleteButton() {
            // confirm with the user that they are deleting a workweek
            // delete the workweek it they accept
            // close the options if they cancel this action
            new AlertDialog.Builder(context)
                    .setTitle("Are you sure?")
                    .setMessage("This will delete the entire workweek for good!")
                    .setCancelable(true)
                    .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String id = String.valueOf(root.getTag());
                            Uri uri = Uri.withAppendedPath(ShiftyContract.Workweek.CONTENT_URI, id);
                            int deleted = context.getContentResolver().delete(uri, null, null);

                            if (deleted > 0) {
                                Utils.makeToast(context, "Workweek deleted", Toast.LENGTH_LONG);
                            } else {
                                Utils.makeToast(context, "Failed to delete workweek", Toast.LENGTH_LONG);
                            }
                        }
                    })
                    .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    })
                    .show();

        }

        @OnClick(R.id.textview_workweek_footer)
        public void onClickFooter() {
            // takes the user to the PayslipActivity to display the payslip for the workweek
            PayslipActivity.start(context, weekStartDatetime, paidHours);
        }

        /**
         * Shows a loading animation in the foreground.
         */
        private void showLoading() {
            recyclerView.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        }

        /**
         * Displays the recyclerview in the foreground.
         */
        private void showRecyclerView() {
            progressBar.setVisibility(View.INVISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
        }

        /**
         * Hides the loading animation.
         */
        private void hideLoading() {
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * AsyncTask for loading the shifts belonging to the current workweek.
     */
    public class ShiftAsyncTask extends AsyncTask<String, Void, Cursor> {

        private Context context;
        // the adapter for displaying the loaded data
        private ShiftRecyclerViewAdapter adapter;
        // the viewholder that is loading this data
        private WorkWeekViewHolder holder;

        public ShiftAsyncTask(Context context, ShiftRecyclerViewAdapter adapter, WorkWeekViewHolder holder) {
            this.context = context;
            this.adapter = adapter;
            this.holder = holder;
        }

        @Override
        protected void onPreExecute() {
            // indicate that something is being loaded
            holder.showLoading();
        }

        @Override
        protected Cursor doInBackground(String... strings) {
            String weekDate = strings[0];

            // get the ISO8601 formatted string for Monday 00:00 of the current week
            String selection = ShiftyContract.Shift.COLUMN_WORKWEEK_ID + " = ?";
            String[] selectionArgs = new String[] { weekDate };

            return context.getContentResolver().query(
                    ShiftyContract.Shift.CONTENT_URI, // query Shift table (/shift)
                    null, // get all columns
                    selection, // get all shifts from this week onwards
                    selectionArgs,
                    ShiftyContract.Shift.COLUMN_SHIFT_START_DATETIME + " asc" // order by shift start time, earliest to latest
            );
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            // swap the adapter's existing cursor with the newly loaded cursor
            adapter.swapCursor(cursor);

            // hide the loading animation if no data is retrieved
            // or show the recylerview if there data was retrieved
            if (cursor.getCount() == 0) {
                holder.hideLoading();
            } else {
                holder.showRecyclerView();
            }
        }

        @Override
        protected void onCancelled() {
            // if loading is cancelled, get rid of the adapter's cursor
            // to indicate no data was loaded
            adapter.swapCursor(null);
        }

    }
}
