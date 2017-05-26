package io.bradenhart.shifty.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import io.bradenhart.shifty.R;
import io.bradenhart.shifty.domain.Payslip;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.bradenhart.shifty.util.DateUtil;

/**
 * Created by bradenhart on 20/04/17.
 */

public class PayslipActivity extends AppCompatActivity {

    public static final String KEY_WEEK_START_DATE = "KEY_WEEK_START_DATE";
    public static final String KEY_PAID_HOURS = "KEY_PAID_HOURS";

    private final String appName = "Payslip";
    private String subtitle;

    @BindView(R.id.appbar_payslip)
    AppBarLayout appBar;
    Toolbar toolbar;
    TextView titleView;
    TextView subtitleView;
    @BindView(R.id.textview_payslip_hours)
    TextView hoursTV;
    @BindView(R.id.textview_payslip_base_rate)
    TextView baseRateTV;
    @BindView(R.id.textview_payslip_pay_rate)
    TextView payRateTV;
    @BindView(R.id.textview_payslip_gross)
    TextView grossTV;
    @BindView(R.id.textview_payslip_paye)
    TextView payeTV;
    @BindView(R.id.textview_payslip_kiwisaver)
    TextView kiwisaverTV;
    @BindView(R.id.textview_payslip_loan)
    TextView loanTV;
    @BindView(R.id.textview_payslip_net)
    TextView netTV;

    private Payslip payslip;
    private String weekStartDatetime;
    private Double paidHours;

    public static void start(Context context, String weekStartDatetime, Double paidHours) {
        Intent intent = new Intent(context, PayslipActivity.class);
        intent.putExtra(PayslipActivity.KEY_WEEK_START_DATE, weekStartDatetime);
        intent.putExtra(PayslipActivity.KEY_PAID_HOURS, paidHours);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payslip);

        ButterKnife.bind(this);

        subtitleView = ButterKnife.findById(this, R.id.textview_toolbar_subtitle);

        // set up actionbar
        setUpActionBar();

        Bundle data = getIntent().getExtras();
        if (data != null) {
            if (data.containsKey(KEY_WEEK_START_DATE) && data.containsKey(KEY_PAID_HOURS)) {
                weekStartDatetime = data.getString(KEY_WEEK_START_DATE);
                paidHours = data.getDouble(KEY_PAID_HOURS);
                payslip = new Payslip(paidHours);
                String weekEndDatetime = DateUtil.getWeekEnd(weekStartDatetime, DateUtil.FMT_ISO_8601_DATETIME);
                SimpleDateFormat fromFmt = new SimpleDateFormat(DateUtil.FMT_ISO_8601_DATETIME, Locale.ENGLISH);
                SimpleDateFormat toFmt = new SimpleDateFormat("dd MMM", Locale.ENGLISH);
                try {
                    subtitle = toFmt.format(fromFmt.parse(weekStartDatetime)) + " - " + toFmt.format(fromFmt.parse(weekEndDatetime));
                    subtitleView.setText(subtitle);
                } catch (ParseException ex) {
                    ex.printStackTrace();
                }
                displayPayslip(payslip);
            }

        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    /* initialisation/setup methods */
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

    private void displayPayslip(Payslip payslip) {
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
