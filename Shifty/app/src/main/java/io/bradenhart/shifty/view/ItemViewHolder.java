package io.bradenhart.shifty.view;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.bradenhart.shifty.R;
import io.bradenhart.shifty.activity.ShiftActivity;
import io.bradenhart.shifty.database.DatabaseManager;
import io.bradenhart.shifty.domain.Shift;
import io.github.luizgrp.sectionedrecyclerviewadapter.Section;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;

/**
 * Created by bradenhart on 2/05/17.
 */

public class ItemViewHolder extends RecyclerView.ViewHolder {

    private Context context;
    Integer pos;
    Section section;
    Shift shift;
    @BindView(R.id.layout_shift_item)
    LinearLayout itemLayout;
    @BindView(R.id.textview_day_of_month)
    TextView dayOfMonthTV;
    @BindView(R.id.textview_day_of_week)
    TextView dayOfWeekTV;
    @BindView(R.id.textview_shift_start_time)
    TextView startTimeTV;
    @BindView(R.id.textview_shift_end_time)
    TextView endTimeTV;
    @BindView(R.id.view_shift_progress)
    View shiftProgressBar;

    @BindView(R.id.layout_shift_item_options)
    LinearLayout optionsLayout;
    @BindView(R.id.button_edit_shift)
    ImageButton editButton;
    @BindView(R.id.textview_shift_length)
    TextView shiftLengthTV;
    @BindView(R.id.button_delete_shift)
    ImageButton deleteButton;
    @BindView(R.id.button_close_options)
    ImageButton closeButton;

    ItemViewHolder(Context context, View view) {
        super(view);
        this.context = context;
        ButterKnife.bind(this, view);

    }

//    public void setPos(Integer pos) {
//        this.pos = pos;
//    }

//    public Integer getPos() {
//        return this.pos;
//    }

//    public String getSectionTag() {
//        return sectionTag;
//    }

//    public void setSection(String sectionTag) {
//        this.sectionTag = sectionTag;
//    }

    @OnClick(R.id.layout_shift_item)
    void onClickItemLayout() {
        Log.e("shift id: ", shift.getId());
        if (optionsLayout.getVisibility() == View.GONE) {
            optionsLayout.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.button_close_options)
    void onClickCloseButton() {
        if (optionsLayout.getVisibility() == View.VISIBLE) {
            optionsLayout.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.button_edit_shift)
    void onClickEditButton() {
        Intent intent = new Intent(context, ShiftActivity.class);
        intent.putExtra(ShiftActivity.KEY_EDIT_MODE, true);
        intent.putExtra(ShiftActivity.KEY_SHIFT, shift);
        context.startActivity(intent);
    }

    @OnClick(R.id.button_delete_shift)
    void onClickDeleteButton() {

        new DatabaseManager(context.getApplicationContext()).deleteShift(shift.getId());
//        SectionedRecyclerViewAdapter sectionedAdapter =
        ((WorkWeekSection) section).removeShift(pos);
//        section.removeShift(getPos());
        onClickCloseButton();
//        section.notifyItemRemovedFromSection(getSectionTag(), getPos());
        ((WorkWeekSection) section).notifyDataSetChanged();
        if (((WorkWeekSection) section).shifts.size() == 0) {
            ((WorkWeekSection) section).removeFromAdapter();
        }
    }


}
