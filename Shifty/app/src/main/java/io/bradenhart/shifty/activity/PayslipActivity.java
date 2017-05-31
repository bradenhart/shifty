package io.bradenhart.shifty.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import io.bradenhart.shifty.R;
import io.bradenhart.shifty.domain.Payslip;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.bradenhart.shifty.util.DateUtils;

/**
 * Displays the payslip information for a given workweek.
 * @author bradenhart
 */
public class PayslipActivity extends AppCompatActivity {

    // logtag
    private String TAG = PayslipActivity.class.getSimpleName();

    /* key value constants */
    // key for sending the payslip's week start date to this activity in an Intent
    public static final String KEY_WEEK_START_DATE = "KEY_WEEK_START_DATE";
    // key for sending the number of paid hours in the payslip to this activity in an Intent
    public static final String KEY_PAID_HOURS = "KEY_PAID_HOURS";

    // title to be displayed in the toolbar
    private final String appName = "Payslip";
    // the formatted date range for the payslip
    private String subtitle;

    private Context context;

    /* components for the Activity's actionbar */
    @BindView(R.id.appbar_payslip)
    AppBarLayout appBar;
    Toolbar toolbar;
    TextView titleView;

    // displays the date range for the payslip
    @BindView(R.id.textview_toolbar_subtitle)
    TextView subtitleView;
    // displays the hours for the payslip
    @BindView(R.id.textview_payslip_hours)
    TextView hoursTV;
    // displays the base rate for the payslip
    @BindView(R.id.textview_payslip_base_rate)
    TextView baseRateTV;
    // displays the pay rate for the payslip
    @BindView(R.id.textview_payslip_pay_rate)
    TextView payRateTV;
    // displays the gross amount for the payslip
    @BindView(R.id.textview_payslip_gross)
    TextView grossTV;
    // displays the P.A.Y.E deduction for the payslip
    @BindView(R.id.textview_payslip_paye)
    TextView payeTV;
    // displays the kiwisaver deduction for the payslip
    @BindView(R.id.textview_payslip_kiwisaver)
    TextView kiwisaverTV;
    // displays the student loan repayment for the payslip
    @BindView(R.id.textview_payslip_loan)
    TextView loanTV;
    // displays the net pay for the payslip
    @BindView(R.id.textview_payslip_net)
    TextView netTV;

    private String weekStartDatetime;
    private Double paidHours;

    /**
     * Used for starting this Activity. Ensures that the Activity is started with the required
     * extras.
     * @param context The context of the Activity that calls this method
     * @param weekStartDatetime The formatted date string for the payslip
     * @param paidHours The number of paid hours for the payslip
     */
    public static void start(@NonNull Context context, @NonNull String weekStartDatetime, @NonNull Double paidHours) {
        Intent intent = new Intent(context, PayslipActivity.class);
        intent.putExtra(PayslipActivity.KEY_WEEK_START_DATE, weekStartDatetime);
        intent.putExtra(PayslipActivity.KEY_PAID_HOURS, paidHours);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payslip);
        context = PayslipActivity.this;

        ButterKnife.bind(this);

        // set up actionbar
        setUpActionBar();

        Payslip payslip;

        Bundle data = getIntent().getExtras();
        if (data != null) {
            if (data.containsKey(KEY_WEEK_START_DATE) && data.containsKey(KEY_PAID_HOURS)) {
                // get the data from the intent
                weekStartDatetime = data.getString(KEY_WEEK_START_DATE);
                paidHours = data.getDouble(KEY_PAID_HOURS);
                // create a payslip object with the paid hours data
                payslip = new Payslip(paidHours);
                // format the week start datetime data for the subtitle
                String weekEndDatetime = DateUtils.getWeekEnd(weekStartDatetime, DateUtils.FMT_ISO_8601_DATETIME);
                SimpleDateFormat fromFmt = new SimpleDateFormat(DateUtils.FMT_ISO_8601_DATETIME, Locale.ENGLISH);
                SimpleDateFormat toFmt = new SimpleDateFormat("dd MMM", Locale.ENGLISH);
                try {
                    // if the subtitle formats properly, display it in the subtitle view
                    subtitle = toFmt.format(fromFmt.parse(weekStartDatetime)) + " - " + toFmt.format(fromFmt.parse(weekEndDatetime));
                    subtitleView.setText(subtitle);
                } catch (ParseException ex) {
                    ex.printStackTrace();
                }
                // display the payslip information
                displayPayslip(payslip);
            }

        }

    }

    /**
     * Sets up the action bar for this Activity.
     */
    private void setUpActionBar() {
        toolbar = ButterKnife.findById(this, R.id.toolbar);
        titleView = ButterKnife.findById(this, R.id.textview_toolbar_title);

        // replace the default actionbar with our toolbar
        setSupportActionBar(toolbar);
        // disable the title that would appear in the actionbar
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        // show the desired title in the toolbar instead of the actionbar
        titleView.setText(appName);
        // will show the back arrow/caret and make it clickable. will not return home unless parent activity is specified
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // shows logo/icon with caret/arrow if passed true. will not show logo/icon if passed false
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        // set the navigation drawer icon to the hamburger icon
//        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
    }

    /**
     *
     * @param payslip The payslip to display
     */
    private void displayPayslip(@NonNull Payslip payslip) {
        hoursTV.setText(String.format(Locale.ENGLISH, "%.02f hrs", payslip.getHours()));
        baseRateTV.setText(String.format(Locale.ENGLISH, "$%.02f/hr", payslip.getBaseRate()));
        payRateTV.setText(String.format(Locale.ENGLISH, "$%.02f/hr", payslip.getRateFull()));
        grossTV.setText(String.format(Locale.ENGLISH, "$%.02f", payslip.getGross()));
        payeTV.setText(String.format(Locale.ENGLISH, "$%.02f", payslip.getPayeTotal()));
        kiwisaverTV.setText(String.format(Locale.ENGLISH, "$%.02f", payslip.getKiwisavEmployee()));
        loanTV.setText(String.format(Locale.ENGLISH, "$%.02f", payslip.getStudentLoan()));
        netTV.setText(String.format(Locale.ENGLISH, "$%.02f", payslip.getNet()));
    }

}
