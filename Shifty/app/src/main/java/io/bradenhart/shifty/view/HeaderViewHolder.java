package io.bradenhart.shifty.view;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.bradenhart.shifty.R;

/**
 * Created by bradenhart on 2/05/17.
 */

public class HeaderViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.textview_week_header)
    TextView headerTV;

    HeaderViewHolder(View view) {
        super(view);

        ButterKnife.bind(this, view);
//            Log.e("WorkWeekSection", "constructor(HeaderViewHolder)");
    }

}
