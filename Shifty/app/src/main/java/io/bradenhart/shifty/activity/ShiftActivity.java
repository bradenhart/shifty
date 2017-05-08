package io.bradenhart.shifty.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import io.bradenhart.shifty.R;
import io.bradenhart.shifty.database.DatabaseManager;
import io.bradenhart.shifty.domain.Shift;
import io.bradenhart.shifty.domain.ShiftTime;
import io.bradenhart.shifty.ui.TimeScroller;
import io.bradenhart.shifty.util.DateUtil;

import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;

//import io.bradenhart.shifty.domain.ShiftDate;

/**
 * Created by bradenhart on 14/03/17.
 */

public class ShiftActivity extends AppCompatActivity {

    final String TAG = "NewShiftActivity";
    private String title;
    private final String TITLE_NEW = "Create Shift";
    private final String TITLE_EDIT = "Edit Shift";

    // Keys for sending data to this activity in an Intent
    public static final String KEY_SHIFT = "KEY_SHIFT";
    public static final String KEY_EDIT_MODE = "KEY_EDIT_MODE";

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

    private String[] monthsFull = {"January", "February", "March", "April", "May", "June", "July",
            "August", "September", "November", "December"};
    private String[] monthsShort = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep",
            "Nov", "Dec"};
    private String[] daysFull = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday",
            "Sunday"};
    private String[] daysShort = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};

    Date selectedDate;
    Boolean editModeEnabled = false;
    Shift shift;
    String ymdString;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shift);

        ButterKnife.bind(this);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            editModeEnabled = bundle.getBoolean(KEY_EDIT_MODE, false);
            if (bundle.containsKey(KEY_SHIFT)) shift = (Shift) bundle.getSerializable(KEY_SHIFT);
        }
        if (shift != null) {
            selectedDate = shift.getDate();
            dayTextView.setText(DateUtil.getPrettyDateString(DateUtil.getYear(shift.getId()), DateUtil.getMonth(shift.getId()), DateUtil.getDay(shift.getId())));
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
                if (editModeEnabled && shift != null) {
                    ShiftTime startTime = shift.getStartTime();
                    ShiftTime endTime = shift.getEndTime();
                    long endTimeDelay = startTimeScroller.scrollAllTo(500, startTime.getHour(), startTime.getMinute(), startTime.getPeriod());
                    endTimeScroller.scrollAllTo(endTimeDelay, endTime.getHour(), endTime.getMinute(), endTime.getPeriod());
                }
            }
        });

    }

    /* initialisation/setup methods */
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
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // shows logo/icon with caret/arrow if passed true. will not show logo/icon if passed false
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
        // set the navigation drawer icon to the hamburger icon
//        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
    }

    @OnClick(R.id.button_day)
    public void onClickDayButton() {
        final Calendar c = Calendar.getInstance();
        Log.e("Calendar", c.toString());

        if (shift != null) {
            c.set(DateUtil.getYear(shift.getId()), DateUtil.getMonth(shift.getId()), DateUtil.getDay(shift.getId()));
        }
        DatePickerDialog datePickerFragment = new DatePickerDialog(this,
                android.R.style.Theme_DeviceDefault_Light_Dialog,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        dayTextView.setText(DateUtil.getPrettyDateString(year, month, day));

                        if (editModeEnabled) ymdString = DateUtil.getYMDString(year, month, day);

                        Calendar chosen = (Calendar) c.clone();
                        chosen.set(year, month, day);
                        selectedDate = chosen.getTime();
                    }
                },
                c.get(YEAR),
                c.get(MONTH),
                c.get(DAY_OF_MONTH));
        datePickerFragment.show();
    }

    @OnClick(R.id.button_save_shift)
    public void onClickAddShiftButton() {
        if (selectedDate == null) {
            makeToast("Please select a date", Toast.LENGTH_LONG);
            return;
        }

        ShiftTime startTime = startTimeScroller.getTime();
        ShiftTime endTime = endTimeScroller.getTime();

        if (endTime.before(startTime)) {
            makeToast("Start time must be before end time", Toast.LENGTH_LONG);
            return;
        }

        if (editModeEnabled) {
            // edit mode is enabled
            new DatabaseManager(getApplicationContext()).deleteShift(shift.getId());
            // shift id has been changed (date has changed)
            if (ymdString != null)
                shift = new Shift(DateUtil.getDateString(ymdString, startTime), selectedDate, startTime, endTime);
            else shift = new Shift(shift.getId(), selectedDate, startTime, endTime);
            Log.e("EDIT_MODE", "enabled");
            Log.e("SHIFT_ID", shift.getId());
        } else {
            // shift created for first time, use constructor that creates id internally
            shift = new Shift(selectedDate, startTime, endTime);
//            Log.e("EDIT_MODE", "not enabled");
//            Log.e("SHIFT_ID", shift.getId());
        }

        boolean success = new DatabaseManager(getApplicationContext()).insertShift(shift);

        makeToast("Insert " + (success ? "Succeeded" : "Failed"), Toast.LENGTH_SHORT);

        selectedDate = null;
        shift = null;
        dayTextView.setText("---, -- --- --");
        startTimeScroller.resetScroller();
        endTimeScroller.resetScroller();

        if (editModeEnabled) {
            Intent intent = new Intent(ShiftActivity.this, ShiftViewActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ShiftActivity.this, ShiftViewActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void makeToast(String message, int length) {
        if (length != Toast.LENGTH_SHORT && length != Toast.LENGTH_LONG) return;
        Toast.makeText(this, message, length).show();
    }

}
