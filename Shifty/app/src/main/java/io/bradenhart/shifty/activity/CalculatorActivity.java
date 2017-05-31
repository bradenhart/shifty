package io.bradenhart.shifty.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import io.bradenhart.shifty.R;
import io.bradenhart.shifty.domain.Payslip;

import static io.bradenhart.shifty.util.Utils.makeToast;

/**
 * A Calculator that allows the user to calculate the net pay
 * given the hours worked/to be worked, or the gross amount earned/
 * to be earned. Two modes are available for the calculator to determine
 * which type of input the user will provide: Hour, Gross.
 * @author bradenhart
 */
public class CalculatorActivity extends AppCompatActivity {

    // logtag
    private final String TAG = CalculatorActivity.class.getSimpleName();
    // title to be displayed in the toolbar
    private final String title = "Calculator";
    /* key value constants */
    // key to be paired with the current input value for the calculator when in Hour mode
    private static final String KEY_HOURS = "KEY_HOURS";
    // key to be paired with the current input value for the calculator when in Gross mode
    private static final String KEY_GROSS = "KEY_GROSS";
    // key to be paired with the mode that the calculator is in
    private static final String KEY_CALCULATOR_MODE = "KEY_CALCULATOR_MODE";
    /* other constants */
    private static final String MODE_HOUR = "MODE_HOUR";
    private static final String MODE_GROSS = "MODE_GROSS";
    // holds the current mode for the calculator
    private CalcMode currentMode = CalcMode.HOUR;

    private Context context;

    /* components for the Activity's actionbar */
    @BindView(R.id.appbar_calculator)
    AppBarLayout appBar;
    Toolbar toolbar;
    TextView titleView;

    // button to set the calculator in Hour mode
    @BindView(R.id.button_hour_mode)
    Button hourModeButton;
    // button to set the calculator in Gross mode
    @BindView(R.id.button_gross_mode)
    Button grossModeButton;
    // user inputs a value for the calculator to use
    @BindView(R.id.edittext_calculator_input)
    EditText inputEditText;
    // in Hour mode, user can click this to add multiple values together for the number of hours
    @BindView(R.id.button_add_value)
    ImageButton addValueButton;
    // clears the calculator and any associated values
    @BindView(R.id.button_clear_calculator)
    ImageButton clearButton;
    // displays the base rate used in the calculation
    @BindView(R.id.textview_calculator_base_rate)
    TextView baseRateTV;
    // displays the full pay rate used in the calculation
    @BindView(R.id.textview_calculator_pay_rate)
    TextView payRateTV;
    // displays the number of hours used in the calculation
    @BindView(R.id.textview_calculator_hours)
    TextView hoursTV;
    // displays the amount of P.A.Y.E deducted from the gross amount
    @BindView(R.id.textview_calculator_paye)
    TextView payeTV;
    // displays the kiwisaver contribution for the calculation
    @BindView(R.id.textview_calculator_kiwisaver)
    TextView kiwisaverTV;
    // displays the student loan repayment for the calculation
    @BindView(R.id.textview_calculator_loan)
    TextView loanTV;
    // displays the gross amount used in the calculation
    @BindView(R.id.textview_calculator_gross)
    TextView grossTV;
    // displays the net amount calculated
    @BindView(R.id.textview_calculator_net)
    TextView netTV;
    // calculates the net pay when button is clicked and input is valid
    @BindView(R.id.button_calculate)
    Button calculateButton;
    // bottom navigation bar to allow the user to go to current or recent shifts (ShiftViewActivity)
    @BindView(R.id.bottomnavigation_calculator)
    BottomNavigationView navView;

    // holds the total number of hours input by the user in Hour mode
    private Double totalHours = 0.0;
    // holds the gross amount input by the user in Gross mode
    private Double gross = 0.0;
    // if the text has not changed since the previous calculation, a new calculation will not be performed
    private boolean textHasChanged = false;

    // set of valid modes for the Calculator to use for calculations
    private enum CalcMode {
        HOUR(MODE_HOUR), GROSS(MODE_GROSS);

        private String value;

        CalcMode(String value) {
            this.value = value;
        }

        public static CalcMode get(String value) {
            switch (value) {
                case MODE_GROSS:
                    return GROSS;
                case MODE_HOUR:
                default:
                    return HOUR;
            }
        }

        public String getValue() {
            return value;
        }
    }


    /**
     * Used for starting this Activity. Ensures that the Activity is started with the required
     * extras.
     *
     * @param context The context of the Activity that calls this method
     */
    public static void start(@NonNull Context context) {
        context.startActivity(new Intent(context, CalculatorActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);

        context = CalculatorActivity.this;

        ButterKnife.bind(this);


        if (savedInstanceState != null) {
            // if the Calculator mode is stored, retrieve the value
            // and assign it to currentMode.
            if (savedInstanceState.containsKey(KEY_CALCULATOR_MODE)) {
                currentMode = CalcMode.get(savedInstanceState.getString(KEY_CALCULATOR_MODE));
            }
            // if the Hour value is stored, retrieve it and display in the EditText
            if (savedInstanceState.containsKey(KEY_HOURS)) {
                Double hours = savedInstanceState.getDouble(KEY_HOURS, 0.0);
                inputEditText.setText(String.format(Locale.ENGLISH, "%.2f", hours));
            }
            // if the Gross value is stored, retrieve it and display it in the EditText
            else if (savedInstanceState.containsKey(KEY_GROSS)) {
                Double gross = savedInstanceState.getDouble(KEY_GROSS, 0.0);
                inputEditText.setText(String.format(Locale.ENGLISH, "%.02f", gross));
            }
        }

        // select the current mode
        if (currentMode == CalcMode.HOUR) {
            selectHourMode();
        } else {
            selectGrossMode();
        }

        setUpActionBar();

        // set up the bottom navigation view with an item selection listener
        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (navView.getSelectedItemId() == id) return false;

                switch (id) {
                    case R.id.menu_button_shifts:
                        saveDisplayMode(ShiftViewActivity.DisplayMode.CURRENT);
                        finish();
                        break;
                    case R.id.menu_button_recent:
                        saveDisplayMode(ShiftViewActivity.DisplayMode.RECENT);
                        finish();
                        break;
                }

                return true;
            }
        });

        // select the calculator item to show the user that they are on the calculator screen
        navView.setSelectedItemId(R.id.menu_button_calculator);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_CALCULATOR_MODE, currentMode.getValue());
        if (currentMode == CalcMode.HOUR) {
            try {
                outState.putDouble(KEY_HOURS, Double.parseDouble(inputEditText.getText().toString()));
            } catch (NumberFormatException ex) {
                Log.e(TAG, "Input is not a double, didn't save in outstate bundle");
            }
        } else if (currentMode == CalcMode.GROSS) {
            try {
                outState.putDouble(KEY_GROSS, Double.parseDouble(inputEditText.getText().toString()));
            } catch (NumberFormatException ex) {
                Log.e(TAG, "Input is not a double, didn't save in outstate bundle");
            }
        }
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
        // shows logo/icon with caret/arrow if passed true. will not show logo/icon if passed false
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    /**
     * Saves the provided mode in SharedPreferences to be retrieved in ShiftViewActivity.
     *
     * @param mode The mode that ShiftViewActivity will use for displaying shifts
     *             (Current or Recent mode)
     */
    private void saveDisplayMode(ShiftViewActivity.DisplayMode mode) {
        SharedPreferences sp = getSharedPreferences(getString(R.string.shared_preferences_name), MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        // check if the mode is one of DisplayMode.Current and DisplayMode.RECENT
        if (mode.isValid()) {
            editor.putString(ShiftViewActivity.KEY_DISPLAY_MODE, mode.getValue()).apply();
        }
    }

    /**
     * Changes the calculator's mode to Hour mode, selects the Hour mode
     * button and deselects the Gross mode button.
     */
    private void selectHourMode() {
        currentMode = CalcMode.HOUR;
        grossModeButton.setTextColor(getColor(R.color.colorPrimary));
        grossModeButton.setBackgroundColor(getColor(R.color.text_white));

        hourModeButton.setTextColor(getColor(R.color.text_white));
        hourModeButton.setBackgroundColor(getColor(R.color.colorPrimary));

        addValueButton.setVisibility(View.VISIBLE);
        clearCalculatorResult();
    }

    /**
     * Changes the calculator's mode to Gross mode, selects the Gross mode button
     * and deselects the Hour mode button.
     */
    private void selectGrossMode() {
        currentMode = CalcMode.GROSS;
        hourModeButton.setTextColor(getColor(R.color.colorPrimary));
        hourModeButton.setBackgroundColor(getColor(R.color.text_white));

        grossModeButton.setTextColor(getColor(R.color.text_white));
        grossModeButton.setBackgroundColor(getColor(R.color.colorPrimary));

        addValueButton.setVisibility(View.GONE);
        clearCalculatorResult();
    }

    /**
     * Updates the calculator's result and resets the user input.
     *
     * @param payslip The payslip that will be displayed
     */
    private void updateCalculatorResult(Payslip payslip) {
        resetInput();
        hoursTV.setText(String.format(Locale.ENGLISH, "%.02f hrs", payslip.getHours()));
        baseRateTV.setText(String.format(Locale.ENGLISH, "$%.02f/hr", payslip.getBaseRate()));
        payRateTV.setText(String.format(Locale.ENGLISH, "$%.02f/hr", payslip.getRateFull()));
        grossTV.setText(String.format(Locale.ENGLISH, "$%.02f", payslip.getGross()));
        payeTV.setText(String.format(Locale.ENGLISH, "$%.02f", payslip.getPayeTotal()));
        kiwisaverTV.setText(String.format(Locale.ENGLISH, "$%.02f", payslip.getKiwisavEmployee()));
        loanTV.setText(String.format(Locale.ENGLISH, "$%.02f", payslip.getStudentLoan()));
        netTV.setText(String.format(Locale.ENGLISH, "$%.02f", payslip.getNet()));
    }

    /**
     * Clears the calculator result fields and resets the user input.
     */
    private void clearCalculatorResult() {
        resetInput();
        hoursTV.setText("");
        baseRateTV.setText("");
        payRateTV.setText("");
        grossTV.setText("");
        payeTV.setText("");
        kiwisaverTV.setText("");
        loanTV.setText("");
        netTV.setText("");
    }

    /**
     * Changes the user input back to the default values.
     */
    private void resetInput() {
        inputEditText.setText("");
        totalHours = 0.0;
        gross = 0.0;
    }

    /* listeners */
    @OnClick(R.id.button_hour_mode)
    public void onClickHourModeButton() {
        selectHourMode();
    }

    @OnClick(R.id.button_gross_mode)
    public void onClickGrossModeButton() {
        selectGrossMode();
    }

    @OnClick(R.id.button_add_value)
    public void onClickAddValue() {
        // only do something if the current mode is Hour
        if (currentMode == CalcMode.HOUR) {
            try {
                Double hours = Double.parseDouble(inputEditText.getText().toString());
                totalHours += hours;
                inputEditText.setText("");
                hoursTV.setText(String.format(Locale.ENGLISH, "%.02f hrs", totalHours));
            } catch (NumberFormatException ex) {
                Log.e(TAG, "Bad input. Could not convert to double");
            }
        }
    }

    @OnClick(R.id.button_clear_calculator)
    public void onClickClearButton() {
        clearCalculatorResult();
    }

    @OnClick(R.id.button_calculate)
    public void onClickCalculateButton() {
        if (!textHasChanged) return;

        textHasChanged = false;

        Payslip payslip = null;

        // get the user's input
        String input = inputEditText.getText().toString();
        if (input.isEmpty()) {
            makeToast(context, "No input");
            clearCalculatorResult();
            return;
        }

        double value = 0.0;
        // try and convert the user input to a double
        try {
            value = Double.parseDouble(input);
        } catch (NumberFormatException ex) {
            Log.e(TAG, "Bad input. Could not convert to double");
        }

        if (currentMode == CalcMode.HOUR) {
            /* Handles a calculation in Hour mode */
            totalHours += value;
            payslip = new Payslip(totalHours, Payslip.Mode.HOUR);
        } else if (currentMode == CalcMode.GROSS) {
            /* Handles a calculation in Gross mode */
            if (value != 0.0) {
                gross = value;
                payslip = new Payslip(gross, Payslip.Mode.GROSS);
            } else {
                makeToast(context, "Gross value can't be zero");
            }
        } else {
            makeToast(context, "Cannot calculate payslip");
        }

        if (payslip != null) updateCalculatorResult(payslip);
    }

    @OnTextChanged(R.id.edittext_calculator_input)
    public void onInputTextChanged() {
        if (!textHasChanged) textHasChanged = true;
    }


}
