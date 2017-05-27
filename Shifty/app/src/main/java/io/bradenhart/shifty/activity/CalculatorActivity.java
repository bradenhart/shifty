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
import io.bradenhart.shifty.util.Utils;

import static io.bradenhart.shifty.util.Utils.makeToast;

public class CalculatorActivity extends AppCompatActivity {

    private final String TAG = "CalculatorActivity";
    private final String title = "Calculator";
    private static final String KEY_HOURS = "KEY_HOURS";
    private static final String KEY_GROSS = "KEY_GROSS";
    private static final String KEY_CALCULATOR_MODE = "KEY_CALCULATOR_MODE";
    private static final String MODE_HOUR = "MODE_HOUR";
    private static final String MODE_GROSS = "MODE_GROSS";
    private String currentMode;

    @BindView(R.id.appbar_calculator)
    AppBarLayout appBar;
    Toolbar toolbar;
    TextView titleView;
    @BindView(R.id.button_hour_mode)
    Button hourModeButton;
    @BindView(R.id.button_gross_mode)
    Button grossModeButton;
    @BindView(R.id.edittext_calculator_input)
    EditText inputEditText;
    @BindView(R.id.button_add_value)
    ImageButton addValueButton;
    @BindView(R.id.button_clear_calculator)
    ImageButton clearButton;
    @BindView(R.id.textview_calculator_base_rate)
    TextView baseRateTV;
    @BindView(R.id.textview_calculator_pay_rate)
    TextView payRateTV;
    @BindView(R.id.textview_calculator_hours)
    TextView hoursTV;
    @BindView(R.id.textview_calculator_paye)
    TextView payeTV;
    @BindView(R.id.textview_calculator_kiwisaver)
    TextView kiwisaverTV;
    @BindView(R.id.textview_calculator_loan)
    TextView loanTV;
    @BindView(R.id.textview_calculator_gross)
    TextView grossTV;
    @BindView(R.id.textview_calculator_net)
    TextView netTV;
    @BindView(R.id.button_calculate)
    Button calculateButton;
    @BindView(R.id.bottomnavigation_calculator)
    BottomNavigationView navView;

    private Double totalHours = 0.0;
    private Double gross = 0.0;
    private boolean textHasChanged = false;


    public static void start(Context context) {
        context.startActivity(new Intent(context, CalculatorActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);

        ButterKnife.bind(this);

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(KEY_CALCULATOR_MODE)) {
                currentMode = savedInstanceState.getString(KEY_CALCULATOR_MODE, MODE_HOUR);
                if (currentMode.equals(MODE_HOUR)) selectHourMode();
                else selectGrossMode();
            }
            if (savedInstanceState.containsKey(KEY_HOURS)) {
                Double hours = savedInstanceState.getDouble(KEY_HOURS, 0.0);
                inputEditText.setText(String.format(Locale.ENGLISH, "%.2f", hours));
            }
            if (savedInstanceState.containsKey(KEY_GROSS)) {
                Double gross = savedInstanceState.getDouble(KEY_GROSS, 0.0);
                inputEditText.setText(String.format(Locale.ENGLISH, "%.02f", gross));
            }
        }

        selectHourMode();

        setUpActionBar();

        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (navView.getSelectedItemId() == id) return false;

                switch (id) {
                    case R.id.menu_button_shifts:
                        saveDisplayMode(ShiftViewActivity.MODE_CURRENT);
                        finish();
                        break;
                    case R.id.menu_button_recent:
                        saveDisplayMode(ShiftViewActivity.MODE_RECENT);
                        finish();
                        break;
                }

                return true;
            }
        });

        navView.setSelectedItemId(R.id.menu_button_calculator);
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
        // shows logo/icon with caret/arrow if passed true. will not show logo/icon if passed false
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void saveDisplayMode(String mode) {
        SharedPreferences sp = getSharedPreferences(getString(R.string.shared_preferences_name), MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        if (mode.equals(ShiftViewActivity.MODE_CURRENT) || mode.equals(ShiftViewActivity.MODE_RECENT)) {
            editor.putString(ShiftViewActivity.KEY_DISPLAY_MODE, mode).apply();
        }
    }

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
        if (currentMode.equals(MODE_HOUR)) {
            try {
                Double hours = Double.parseDouble(inputEditText.getText().toString());
                totalHours += hours;
                inputEditText.setText("");
                hoursTV.setText(String.format(Locale.ENGLISH, "%.02f", totalHours));
            } catch (NumberFormatException ex) {
                Log.e(TAG, "Bad input. Could not convert to double");
            }
        }
    }

    @OnClick(R.id.button_clear_calculator)
    public void onClickClearButton() {
        clearCalculator();
    }


    @OnClick(R.id.button_calculate)
    public void onClickCalculateButton() {
        if (!textHasChanged) return;

        textHasChanged = false;
        
        Payslip payslip = null;

        if (currentMode.equals(MODE_HOUR)) {
            if (totalHours == 0) {
                try {
                    totalHours = Double.parseDouble(inputEditText.getText().toString());
                } catch (NumberFormatException ex) {
                    Log.e(TAG, "Bad input. Could not convert to double");
                }
            }
            payslip = new Payslip(totalHours, Payslip.Mode.HOUR);
        } else if (currentMode.equals(MODE_GROSS)) {
            payslip = new Payslip(gross, Payslip.Mode.GROSS);
        } else {
            Utils.makeToast(CalculatorActivity.this, "Cannot calculate payslip");
        }

        if (payslip != null) updateCalculator(payslip);
    }

    @OnTextChanged(R.id.edittext_calculator_input)
    public void onInputTextChanged() {
        if (!textHasChanged) textHasChanged = true;
    }

    private void selectHourMode() {
        currentMode = MODE_HOUR;
        grossModeButton.setTextColor(getColor(R.color.colorPrimary));
        grossModeButton.setBackgroundColor(getColor(R.color.text_white));

        hourModeButton.setTextColor(getColor(R.color.text_white));
        hourModeButton.setBackgroundColor(getColor(R.color.colorPrimary));

        addValueButton.setVisibility(View.VISIBLE);
    }

    private void selectGrossMode() {
        currentMode = MODE_GROSS;
        hourModeButton.setTextColor(getColor(R.color.colorPrimary));
        hourModeButton.setBackgroundColor(getColor(R.color.text_white));

        grossModeButton.setTextColor(getColor(R.color.text_white));
        grossModeButton.setBackgroundColor(getColor(R.color.colorPrimary));

        addValueButton.setVisibility(View.GONE);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_CALCULATOR_MODE, currentMode);
        if (currentMode.equals(MODE_HOUR)) {
            try {
                outState.putDouble(KEY_HOURS, Double.parseDouble(inputEditText.getText().toString()));
            } catch (NumberFormatException ex) {
                Log.e(TAG, "Input is not a double, didn't save in outstate bundle");
            }
        } else if (currentMode.equals(MODE_GROSS)) {
            try {
                outState.putDouble(KEY_GROSS, Double.parseDouble(inputEditText.getText().toString()));
            } catch (NumberFormatException ex) {
                Log.e(TAG, "Input is not a double, didn't save in outstate bundle");
            }
        }
    }

    private void updateCalculator(Payslip payslip) {
        inputEditText.setText("");
        totalHours = 0.0;
        gross = 0.0;
        hoursTV.setText(String.format(Locale.ENGLISH, "%.02f hrs", payslip.getHours()));
        baseRateTV.setText(String.format(Locale.ENGLISH, "$%.02f/hr", payslip.getBaseRate()));
        payRateTV.setText(String.format(Locale.ENGLISH, "$%.02f/hr", payslip.getRateFull()));
        grossTV.setText(String.format(Locale.ENGLISH, "$%.02f", payslip.getGross()));
        payeTV.setText(String.format(Locale.ENGLISH, "$%.02f", payslip.getPayeTotal()));
        kiwisaverTV.setText(String.format(Locale.ENGLISH, "$%.02f", payslip.getKiwisavEmployee()));
        loanTV.setText(String.format(Locale.ENGLISH, "$%.02f", payslip.getStudentLoan()));
        netTV.setText(String.format(Locale.ENGLISH, "$%.02f", payslip.getNet()));
    }

    private void clearCalculator() {
        inputEditText.setText("");
        baseRateTV.requestFocus();
        hoursTV.setText("");
        baseRateTV.setText("");
        payRateTV.setText("");
        grossTV.setText("");
        payeTV.setText("");
        kiwisaverTV.setText("");
        loanTV.setText("");
        netTV.setText("");
        totalHours = 0.0;
        gross = 0.0;
    }
}
