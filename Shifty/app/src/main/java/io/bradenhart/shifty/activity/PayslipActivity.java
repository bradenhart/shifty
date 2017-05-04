package io.bradenhart.shifty.activity;

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

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by bradenhart on 20/04/17.
 */

public class PayslipActivity extends AppCompatActivity {

    public static final String KEY_PAYSLIP = "KEY_PAYSLIP";
    public static final String KEY_WEEK_INFO = "KEY_WEEK_INFO";
    private Payslip payslip;
    private final String appName = "Payslip";
    private String subtitle;

    @BindView(R.id.toolbar_payslip)
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


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payslip);

        ButterKnife.bind(this);

        // set up actionbar
        setUpActionBar();

        Bundle data = getIntent().getExtras();
        if (data != null) {
            payslip = data.containsKey(KEY_PAYSLIP) ? (Payslip) data.getSerializable(KEY_PAYSLIP) : null;
            subtitle = data.containsKey(KEY_WEEK_INFO) ? data.getString(KEY_WEEK_INFO) : null;
        }

        if (payslip != null) {
            // display information
            displayPayslip(payslip);
//      makeToast(String.format(Locale.ENGLISH, "$%.02f", payslip.getNet()), Toast.LENGTH_SHORT);
        }

        if (subtitle != null) {
            subtitleView = ButterKnife.findById(this, R.id.textview_toolbar_subtitle);
            subtitleView.setText(subtitle);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                Intent homeIntent = new Intent(PayslipActivity.this, ShiftViewActivity.class);
                homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                homeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(homeIntent);
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

    private void makeToast(String message, int length) {
        if (length != Toast.LENGTH_SHORT && length != Toast.LENGTH_LONG) return;
        Toast.makeText(this, message, length).show();
    }
}
