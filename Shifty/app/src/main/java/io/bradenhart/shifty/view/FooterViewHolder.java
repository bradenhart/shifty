package io.bradenhart.shifty.view;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.bradenhart.shifty.R;
import io.bradenhart.shifty.activity.PayslipActivity;
import io.bradenhart.shifty.domain.Payslip;
import io.bradenhart.shifty.domain.WorkWeek;
import io.bradenhart.shifty.util.DateUtil;

/**
 * Created by bradenhart on 2/05/17.
 */

public class FooterViewHolder extends RecyclerView.ViewHolder {

    private Context context;
    WorkWeek workWeek;
    Payslip payslip;
    @BindView(R.id.workweek_footer_root)
    LinearLayout root;
    @BindView(R.id.textview_net_pay)
    TextView netPayTV;

    public FooterViewHolder(Context context, View view) {
        super(view);
        this.context = context;
        ButterKnife.bind(this, view);
    }

    @OnClick(R.id.workweek_footer_root)
    public void onClickFooter() {
        String startDateTimeString = DateUtil.getWeekStart(workWeek.getShifts().get(0).getId());
        String endDateTimeString = DateUtil.getWeekEnd(workWeek.getShifts().get(0).getId());

        Intent intent = new Intent(context, PayslipActivity.class);
        intent.putExtra(PayslipActivity.KEY_PAYSLIP, payslip);
        intent.putExtra(PayslipActivity.KEY_WEEK_INFO,
                DateUtil.getWorkWeekTitle(startDateTimeString, endDateTimeString));
        context.startActivity(intent);

    }

}
