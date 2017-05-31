package io.bradenhart.shifty.activity;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.bradenhart.shifty.util.Utils;

import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;

/**
 * Allows the user to create new shifts or edit a shift.
 * @author bradenhart
 */
public class ShiftActivity extends AppCompatActivity {

    // logtag
    private final String TAG = ShiftActivity.class.getSimpleName();
    // title to be displayed in the toolbar
    private String title;
    // title options (New shift and Edit shift)
    private final String TITLE_NEW = "Create Shift";
    private final String TITLE_EDIT = "Edit Shift";

    /* key value constants */
    // key for sending a shift's id to this activity in an Intent
    public static final String KEY_SHIFT = "KEY_SHIFT";
    // key for sending the mode to this activity in an Intent
    public static final String KEY_MODE = "KEY_MODE";

    /* components for the Activity's actionbar */
    @BindView(R.id.appbar_shift)
    AppBarLayout appBar;
    Toolbar toolbar;
    TextView titleView;


    @BindView(R.id.textview_day)
    TextView dayTextView;
    @BindView(R.id.button_day)
    ImageButton dayButton;
    @BindView(R.id.timescroller_start)
    TimeScroller startTimeScroller;
    @BindView(R.id.timescroller_end)
    TimeScroller endTimeScroller;
    @BindView(R.id.button_save_shift)
    AppCompatButton shiftButton;

    Date shiftDate;
    Boolean editModeEnabled = false;
    String shiftID;
    Cursor cursor;
    String startDatetime;
    String endDatetime;

    public enum Mode {
        CREATE, EDIT
    }

    public static void start(Context context, Mode mode, String shiftID) {
        Intent intent = new Intent(context, ShiftActivity.class);
        intent.putExtra(ShiftActivity.KEY_MODE, mode == Mode.EDIT);
        intent.putExtra(ShiftActivity.KEY_SHIFT, shiftID);
        context.startActivity(intent);
    }

    public static void start(Context context, Mode mode) {
        Intent intent = new Intent(context, ShiftActivity.class);
        intent.putExtra(ShiftActivity.KEY_MODE, mode == Mode.EDIT);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shift);

        ButterKnife.bind(this);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            editModeEnabled = bundle.getBoolean(KEY_MODE, false);
            if (bundle.containsKey(KEY_SHIFT)) shiftID = bundle.getString(KEY_SHIFT);
        }
        if (shiftID != null) {
            Uri shiftUri = Uri.withAppendedPath(ShiftyContract.Shift.CONTENT_URI, shiftID);
            String selection = ShiftyContract.Shift._ID + " = ?";
            String[] selectionArgs = new String[] {shiftID};
            cursor = getContentResolver().query(shiftUri,
                    null,
                    selection,
                    selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                int startCol = cursor.getColumnIndex(ShiftyContract.Shift.COLUMN_SHIFT_START_DATETIME);
                int endCol = cursor.getColumnIndex(ShiftyContract.Shift.COLUMN_SHIFT_END_DATETIME);
                startDatetime = cursor.getString(startCol);
                endDatetime = cursor.getString(endCol);
            }
        }

        if (startDatetime != null) {
            dayTextView.setText(DateUtils.getPrettyDateString(startDatetime, DateUtils.FMT_ISO_8601_DATETIME));
            try {
                shiftDate = new SimpleDateFormat(DateUtils.FMT_ISO_8601_DATETIME, Locale.ENGLISH).parse(startDatetime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        shiftButton.setText(editModeEnabled ? "UPDATE" : "ADD");
        title = editModeEnabled ? TITLE_EDIT : TITLE_NEW;

        dayButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int m = motionEvent.getActionMasked();

                if (m == MotionEvent.ACTION_UP)
                    dayButton.setImageResource(R.drawable.ic_date_black_24dp);
                if (m == MotionEvent.ACTION_DOWN)
                    dayButton.setImageResource(R.drawable.ic_date_green_24dp);
                return false;
            }
        });

        // set up actionbar
        setUpActionBar();

        ViewTreeObserver observer1 = startTimeScroller.getViewTreeObserver();
        observer1.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                startTimeScroller.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                if (editModeEnabled && shiftID != null) {
                    String fmt = DateUtils.FMT_ISO_8601_DATETIME;

//                    long endTimeDelay = startTimeScroller.scrollAllTo(500,
//                            DateUtil.getHour(startDatetime, fmt),
//                            DateUtil.getMinute(startDatetime, fmt),
//                            DateUtil.getPeriod(startDatetime, fmt));
//
//                    endTimeScroller.scrollAllTo(endTimeDelay,
//                            DateUtil.getHour(endDatetime, fmt),
//                            DateUtil.getMinute(endDatetime, fmt),
//                            DateUtil.getPeriod(endDatetime, fmt));

                    startTimeScroller.scrollAllAtOnce(500,
                            DateUtils.getHour(startDatetime, fmt),
                            DateUtils.getMinute(startDatetime, fmt),
                            DateUtils.getPeriod(startDatetime, fmt));

                    endTimeScroller.scrollAllAtOnce(500,
                            DateUtils.getHour(endDatetime, fmt),
                            DateUtils.getMinute(endDatetime, fmt),
                            DateUtils.getPeriod(endDatetime, fmt));

//                    Utils.makeToast(ShiftActivity.this, "end hour: " + DateUtil.getHour(endDatetime, fmt));
                }
            }
        });

    }

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
    public void onClickAddShiftButton() {
        if (shiftDate == null) {
            Utils.makeToast(ShiftActivity.this, "Please select a date", Toast.LENGTH_LONG);
            return;
        }

        try {
            int timeComparison = startTimeScroller.compareTo(endTimeScroller);
            if (timeComparison == 0) {
                // start time is the same as the end time
                Utils.makeToast(ShiftActivity.this, "Start time and end time should be different", Toast.LENGTH_LONG);
                return;
            } else if (timeComparison > 0) {
                // start time is after the end time
                Utils.makeToast(ShiftActivity.this, "Start time must be before end time", Toast.LENGTH_LONG);
                return;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String dateString = DateUtils.getDatestringWithFormat(DateUtils.FMT_ISO_8601_DATE, shiftDate);
        String startTime = startTimeScroller.getTimeString();
        String endTime = endTimeScroller.getTimeString();

        String startDatetime = dateString + " " + startTime;
        String endDatetime = dateString + " " + endTime;

        ContentValues values = new ContentValues();
        values.put(ShiftyContract.Shift.COLUMN_SHIFT_START_DATETIME, startDatetime);
        values.put(ShiftyContract.Shift.COLUMN_SHIFT_END_DATETIME, endDatetime);
        if (editModeEnabled) {
            int numRowsUpdated = getContentResolver().update(
                    Uri.withAppendedPath(ShiftyContract.Shift.CONTENT_URI, shiftID),
                    values,
                    ShiftyContract.Shift._ID + " = ?",
                    new String[] {shiftID}
            );

            if (numRowsUpdated == 1) {
                Utils.makeToast(ShiftActivity.this, "Shift updated");
            } else {
                Utils.makeToast(ShiftActivity.this, "Update failed", Toast.LENGTH_LONG);
            }

            finish(); // go back to the parent activity
        } else {
            Uri newUri = getContentResolver().insert(
                    ShiftyContract.Shift.CONTENT_URI,
                    values
            );

            if (newUri != null) {
                Utils.makeToast(ShiftActivity.this, "Shift added successfully", Toast.LENGTH_LONG);
            } else {
                Utils.makeToast(ShiftActivity.this, "Failed to add shift", Toast.LENGTH_LONG);
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
