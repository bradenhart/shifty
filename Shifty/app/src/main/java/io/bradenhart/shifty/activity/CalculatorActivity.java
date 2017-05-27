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
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.bradenhart.shifty.R;

import static io.bradenhart.shifty.util.Utils.makeToast;

public class CalculatorActivity extends AppCompatActivity {

    private final String title = "Calculator";

    @BindView(R.id.appbar_calculator)
    AppBarLayout appBar;
    Toolbar toolbar;
    TextView titleView;
    @BindView(R.id.bottomnavigation_calculator)
    BottomNavigationView navView;


    public static void start(Context context) {
        context.startActivity(new Intent(context, CalculatorActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);

        ButterKnife.bind(this);

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

}
