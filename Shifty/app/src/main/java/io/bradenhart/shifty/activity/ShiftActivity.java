package io.bradenhart.shifty.activity;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import io.bradenhart.shifty.R;
import io.bradenhart.shifty.data.ShiftyContract;
import io.bradenhart.shifty.ui.TimeScroller;
import io.bradenhart.shifty.util.DateUtils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static io.bradenhart.shifty.util.Utils.makeToast;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;

/**
 * Allows the user to create new shifts or edit a shift.
 *
 * @author bradenhart
 */
public class ShiftActivity extends AppCompatActivity {

    // logtag
    private final String TAG = ShiftActivity.class.getSimpleName();
    // title to be displayed in the toolbar
    private String title;
    // title options (New shift and Edit shift)
    private final String TITLE_CREATE = "Create Shift";
    private final String TITLE_EDIT = "Edit Shift";
    // button text options (New shift and Edit shift)
    private final String BUTTON_TEXT_CREATE = "ADD";
    private final String BUTTON_TEXT_EDIT = "UPDATE";

    /* key constants */
    // key for sending a shift's id to this activity in an Intent
    public static final String KEY_SHIFT = "KEY_SHIFT";
    // key for sending the mode to this activity in an Intent
    public static final String KEY_MODE = "KEY_MODE";

    private Context context;

    /* components for the Activity's actionbar */
    @BindView(R.id.appbar_shift)
    AppBarLayout appBar;
    Toolbar toolbar;
    TextView titleView;

    // displays the date selected by the user
    @BindView(R.id.textview_day)
    TextView dayTextView;
    // opens a date picker
    @BindView(R.id.button_day)
    ImageButton dayButton;
    // selects the start time
    @BindView(R.id.timescroller_start)
    TimeScroller startTimeScroller;
    // selects the end time
    @BindView(R.id.timescroller_end)
    TimeScroller endTimeScroller;
    // adds the shift to the database
    @BindView(R.id.button_save_shift)
    AppCompatButton shiftButton;

    // the date for the shift
    Date shiftDate;
    // whether the user is editing a shift
    @Mode
    int mode;
    // the id of the shift being edited
    String shiftID;
    // the formatted start datetime for the shift
    String startDatetime;
    // the formatted end datetime for the shift
    String endDatetime;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({MODE_CREATE, MODE_EDIT})
    public @interface Mode {
    }

    /* Mode constants */
    public static final int MODE_CREATE = 0;
    public static final int MODE_EDIT = 1;

    /**
     * Used for starting this Activity. Ensures that the Activity is started with the required
     * extras.
     *
     * @param context The context of the Activity that calls this method
     * @param mode    the mode to start the Activity in
     * @param shiftID the id of the shift to be edited, or null if a new shift will be created
     */
    public static void start(@NonNull Context context, @Mode int mode, @Nullable String shiftID) {
        Intent intent = new Intent(context, ShiftActivity.class);
        intent.putExtra(ShiftActivity.KEY_MODE, mode);
        intent.putExtra(ShiftActivity.KEY_SHIFT, shiftID);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shift);
        context = ShiftActivity.this;

        ButterKnife.bind(this);

        /* get data from the intent's extras */
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            @Mode int value = bundle.getInt(KEY_MODE);
            mode = value;
            shiftID = bundle.getString(KEY_SHIFT);
        }
        // if activity is being created in edit mode with the correct data, get the
        // shift from the database
        if (mode == MODE_EDIT && shiftID != null) {
            Uri shiftUri = Uri.withAppendedPath(ShiftyContract.Shift.CONTENT_URI, shiftID);
            String selection = ShiftyContract.Shift._ID + " = ?";
            String[] selectionArgs = new String[]{shiftID};
            Cursor cursor = getContentResolver().query(shiftUri,
                    null,
                    selection,
                    selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                // get the start and end time for the shift
                int startCol = cursor.getColumnIndex(ShiftyContract.Shift.COLUMN_SHIFT_START_DATETIME);
                int endCol = cursor.getColumnIndex(ShiftyContract.Shift.COLUMN_SHIFT_END_DATETIME);
                startDatetime = cursor.getString(startCol);
                endDatetime = cursor.getString(endCol);
                cursor.close();

                // display the shift's date if it's valid (not null)
                if (startDatetime != null) {
                    dayTextView.setText(DateUtils.getPrettyDateString(startDatetime, DateUtils.FMT_ISO_8601_DATETIME));
                    try {
                        shiftDate = new SimpleDateFormat(DateUtils.FMT_ISO_8601_DATETIME, Locale.ENGLISH).parse(startDatetime);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }

        }

        // set the appropriate title and button text for the mode
        shiftButton.setText(mode == MODE_EDIT ? BUTTON_TEXT_EDIT : BUTTON_TEXT_CREATE);
        title = mode == MODE_EDIT ? TITLE_EDIT : TITLE_CREATE;

        dayButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int m = motionEvent.getActionMasked();

                if (m == MotionEvent.ACTION_UP)
                    dayButton.setImageResource(R.drawable.ic_date_black_24dp);
                else if (m == MotionEvent.ACTION_DOWN)
                    dayButton.setImageResource(R.drawable.ic_date_green_24dp);
                return false;
            }
        });

        // set up actionbar
        setUpActionBar();

        ViewTreeObserver observer = startTimeScroller.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                startTimeScroller.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                // if the activity is in edit mode and has a valid shift id,
                // scroll the time scrollers to display the shift's start and end time
                if (mode == MODE_EDIT && shiftID != null) {
                    String fmt = DateUtils.FMT_ISO_8601_DATETIME;

                    startTimeScroller.scrollAllAtOnce(500,
                            DateUtils.getHour(startDatetime, fmt),
                            DateUtils.getMinute(startDatetime, fmt),
                            DateUtils.getPeriod(startDatetime, fmt));

                    endTimeScroller.scrollAllAtOnce(500,
                            DateUtils.getHour(endDatetime, fmt),
                            DateUtils.getMinute(endDatetime, fmt),
                            DateUtils.getPeriod(endDatetime, fmt));
                }
            }
        });

    }

    /**
     * Sets up the action bar for this Activity.
     */
    private void setUpActionBar() {
        toolbar = ButterKnife.findById(appBar, R.id.toolbar);
        titleView = ButterKnife.findById(toolbar, R.id.textview_toolbar_title);
        // replace the default actionbar with our toolbar
        setSupportActionBar(toolbar);
        // disable the title that would appear in the actionbar
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        // show the desired title in the toolbar instead of the actionbar
        titleView.setText(title);
        // will show the back arrow/caret and make it clickable. will not return home unless parent activity is specified
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @OnClick(R.id.button_day)
    public void onClickDayButton() {
        final Calendar c = Calendar.getInstance();

        // if shiftDate is set, display that date in the Date Picker
        // the means the current date of a shift being edited will be selected
        // when the user opens the dialog, and if they select a date more than once
        // the most recent date will be selected each time the dialog is opened.
        if (shiftDate != null) {
            c.setTime(shiftDate);
        }

        DatePickerDialog datePickerFragment = new DatePickerDialog(this,
                android.R.style.Theme_DeviceDefault_Light_Dialog,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        // display the selected date
                        dayTextView.setText(DateUtils.getPrettyDateString(year, month, day));

                        // get the selected date and update shiftDate
                        Calendar chosen = (Calendar) c.clone();
                        chosen.set(year, month, day);
                        shiftDate = chosen.getTime();
                    }
                },
                c.get(YEAR),
                c.get(MONTH),
                c.get(DAY_OF_MONTH));
        datePickerFragment.show();
    }

    @OnClick(R.id.button_save_shift)
    public void onClickSaveShiftButton() {
        // if shiftDate isn't set, a shift can't be added
        if (shiftDate == null) {
            makeToast(context, "Please select a date", Toast.LENGTH_LONG);
            return;
        }

        try {
            // compare the timescroller times
            int timeComparison = startTimeScroller.compareTo(endTimeScroller);
            if (timeComparison == 0) {
                // start time is the same as the end time
                makeToast(context, "Start time and end time should be different", Toast.LENGTH_LONG);
                return;
            } else if (timeComparison > 0) {
                // start time is after the end time
                makeToast(context, "Start time must be before end time", Toast.LENGTH_LONG);
                return;
            }
        } catch (ParseException e) {
            // if the compareTo method throws a parse exception, the shift can't be added
            e.printStackTrace();
            makeToast(context, "There was an error saving the shift...", Toast.LENGTH_SHORT);
            return;
        }

        // get the date (yyyy-MM-dd) from the shift date
        String dateString = DateUtils.getDatestringWithFormat(shiftDate, DateUtils.FMT_ISO_8601_DATE);
        // get the time strings (HH:MM:SS.sss) from the timescrollers
        String startTime = startTimeScroller.getTimeString();
        String endTime = endTimeScroller.getTimeString();

        // build the datetime strings for the start and end of the shift
        String startDatetime = dateString + " " + startTime;
        String endDatetime = dateString + " " + endTime;

        // set up the content values for the database
        ContentValues values = new ContentValues();
        values.put(ShiftyContract.Shift.COLUMN_SHIFT_START_DATETIME, startDatetime);
        values.put(ShiftyContract.Shift.COLUMN_SHIFT_END_DATETIME, endDatetime);

        /* update the shift if in edit mode, or insert if in create mode */
        if (mode == MODE_EDIT) {
            int numRowsUpdated = getContentResolver().update(
                    Uri.withAppendedPath(ShiftyContract.Shift.CONTENT_URI, shiftID),
                    values,
                    ShiftyContract.Shift._ID + " = ?",
                    new String[]{shiftID}
            );

            if (numRowsUpdated == 1) {
                makeToast(ShiftActivity.this, "Shift updated", Toast.LENGTH_SHORT);
            } else {
                makeToast(ShiftActivity.this, "Update failed", Toast.LENGTH_LONG);
            }

            finish(); // go back to the parent activity
        } else {
            Uri newUri = getContentResolver().insert(
                    ShiftyContract.Shift.CONTENT_URI,
                    values
            );

            if (newUri != null) {
                makeToast(ShiftActivity.this, "Shift added successfully", Toast.LENGTH_LONG);
            } else {
                makeToast(ShiftActivity.this, "Failed to add shift", Toast.LENGTH_LONG);
            }

            /* clean up */
            shiftDate = null;
            shiftID = null;
            dayTextView.setText("---, -- --- --");
            startTimeScroller.resetScroller();
            endTimeScroller.resetScroller();
        }

    }

}
