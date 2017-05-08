package io.bradenhart.shifty.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;
import java.util.zip.Inflater;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.bradenhart.shifty.R;
import io.bradenhart.shifty.activity.ShiftActivity;
import io.bradenhart.shifty.database.DatabaseManager;
import io.bradenhart.shifty.domain.Shift;
import io.bradenhart.shifty.util.DateUtil;
import io.bradenhart.shifty.view.ItemViewHolder;
import io.bradenhart.shifty.view.WorkWeekSection;
import io.github.luizgrp.sectionedrecyclerviewadapter.Section;

/**
 * Created by bradenhart on 8/05/17.
 */
// Adapter for displaying Shifts in a WorkWeek
public class ShiftRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<Shift> shifts;
    private int itemHeight, progressWidth;

    public ShiftRecyclerViewAdapter(Context context, List<Shift> shifts) {
        this.context = context;
        this.shifts = shifts;
        itemHeight = context.getResources().getDimensionPixelSize(R.dimen.workweek_item_height);
        progressWidth = context.getResources().getDimensionPixelSize(R.dimen.workweek_shift_progress_width);
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.section_workweek_item, parent, false);
        return new ShiftViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final ShiftViewHolder shiftHolder = (ShiftViewHolder) holder;

        final Shift shift = shifts.get(position);

        shiftHolder.shift = shift;
        shiftHolder.pos = position;
//        shiftHolder.section = adapter.getSection(sectionTag);
        shiftHolder.dayOfMonthTV.setText(DateUtil.getDayOfMonth(shift.getId()));
        shiftHolder.dayOfWeekTV.setText(DateUtil.getWeekday(shift.getId(), DateUtil.FMT_WEEKDAY_FULL));
        shiftHolder.startTimeTV.setText(shift.getStartTime().toString());
        shiftHolder.endTimeTV.setText(shift.getEndTime().toString());

        shiftHolder.shiftLengthTV.setText(String.format(Locale.ENGLISH, "%.2f hrs", shift.getPaidHours()));

        double progress = DateUtil.getShiftProgress(shift);
        int percentHeight = (int) Math.ceil(itemHeight * progress);
        shiftHolder.shiftProgressBar.setLayoutParams(new LinearLayout.LayoutParams(progressWidth, percentHeight));
    }

    @Override
    public int getItemCount() {
        return shifts.size();
    }


    class ShiftViewHolder extends RecyclerView.ViewHolder {

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

        public ShiftViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

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
            // TODO change this to use new solution to Section class

//            new DatabaseManager(context.getApplicationContext()).deleteShift(shift.getId());
////        SectionedRecyclerViewAdapter sectionedAdapter =
//            ((WorkWeekSection) section).removeShift(pos);
////        section.removeShift(getPos());
//            onClickCloseButton();
////        section.notifyItemRemovedFromSection(getSectionTag(), getPos());
//            ((WorkWeekSection) section).notifyDataSetChanged();
//            if (((WorkWeekSection) section).shifts.size() == 0) {
//                ((WorkWeekSection) section).removeFromAdapter();
//            }
        }

    }

}
