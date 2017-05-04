package io.bradenhart.shifty.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import java.util.List;
import java.util.Locale;

import io.bradenhart.shifty.R;
import io.bradenhart.shifty.domain.Shift;
import io.bradenhart.shifty.domain.WorkWeek;
import io.bradenhart.shifty.util.DateUtil;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;
import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection;

/**
 * Created by bradenhart on 2/05/17.
 */

public class WorkWeekSection extends StatelessSection {

    private Context context;
    private String sectionTag;
    private SectionedRecyclerViewAdapter adapter;
    private String headerTitle;
    private WorkWeek workWeek;
    List<Shift> shifts;

    private int itemHeight;
    private int progressWidth;

    private WorkWeekSection(Context context) {
        super(R.layout.section_workweek_header, R.layout.section_workweek_footer, R.layout.section_workweek_item);
        this.context = context;
        itemHeight = context.getResources().getDimensionPixelSize(R.dimen.workweek_item_height);
        progressWidth = context.getResources().getDimensionPixelSize(R.dimen.workweek_shift_progress_width);
    }

    public WorkWeekSection(WorkWeekSection.Builder builder) {
        this(builder.context);
        this.sectionTag = builder.parentSectionTag;
        this.adapter = builder.adapter;
        this.workWeek = builder.workWeek;
        this.headerTitle = builder.workWeek.getTitle();
        this.shifts = builder.workWeek.getShifts();
    }

//    public WorkWeekSection(Context context, WorkWeek workWeek) {
//        this();
//        this.context = context;
//        this.workWeek = workWeek;
//        this.headerTitle = workWeek.getTitle();
//        this.shifts = workWeek.getShifts();
//    }

    public void addShift(Shift shift) {
        shifts.add(shift);
    }

    public void addShiftAtIndex(int index, Shift shift) {
        shifts.add(index, shift);
    }

    public void removeShift(int pos) {
        shifts.remove(pos);
    }

    public String getEndDate() {
        return DateUtil.getWeekEnd(shifts.get(0).getId());
    }

//    public SectionedRecyclerViewAdapter getAdapter() {
//        return adapter;
//    }

    public void notifyDataSetChanged() {
        adapter.notifyDataSetChanged();
    }

    public void removeFromAdapter() {
        adapter.removeSection(sectionTag);
    }

    @Override
    public int getContentItemsTotal() {
//            Log.e("WorkWeekSection", "getContentItemsTotal() -> " + shifts.size());
        return shifts.size();
    }

    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
//            Log.e("WorkWeekSection", "getHeaderViewHolder()");
        return new HeaderViewHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
//            Log.e("WorkWeekSection", "Binding Header View Holder");
        HeaderViewHolder headerHolder = (HeaderViewHolder) holder;

        headerHolder.headerTV.setText(headerTitle);
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
//            Log.e("WorkWeekSection", "getItemViewHolder()");
        return new ItemViewHolder(context, view);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
//            Log.e("WorkWeekSection", "Binding Item View Holder: " + position);
        final ItemViewHolder itemHolder = (ItemViewHolder) holder;

        final Shift shift = shifts.get(position);
//        Log.e("BIND", shift.getId());

        itemHolder.shift = shift;
        itemHolder.pos = position;
        itemHolder.section = adapter.getSection(sectionTag);
        itemHolder.dayOfMonthTV.setText(DateUtil.getDayOfMonth(shift.getId()));
        itemHolder.dayOfWeekTV.setText(DateUtil.getWeekday(shift.getId(), DateUtil.FMT_WEEKDAY_FULL));
        itemHolder.startTimeTV.setText(shift.getStartTime().toString());
        itemHolder.endTimeTV.setText(shift.getEndTime().toString());

        itemHolder.shiftLengthTV.setText(String.format(Locale.ENGLISH, "%.2f hrs", shift.getPaidHours()));

        double progress = DateUtil.getShiftProgress(shift);
//        Log.e("Progress", progress + "");
        int percentHeight = (int) Math.ceil(itemHeight * progress);
        itemHolder.shiftProgressBar.setLayoutParams(new LinearLayout.LayoutParams(progressWidth, percentHeight));
    }

    @Override
    public RecyclerView.ViewHolder getFooterViewHolder(View view) {
        return new FooterViewHolder(context, view);
    }

    @Override
    public void onBindFooterViewHolder(RecyclerView.ViewHolder holder) {
        final FooterViewHolder footerHolder = (FooterViewHolder) holder;

        footerHolder.workWeek = workWeek;
        footerHolder.payslip = workWeek.getPayslip();
        footerHolder.netPayTV.setText(String.format(Locale.ENGLISH, "$%.02f", workWeek.getPayslip().getNet()));
    }


    public static class Builder {

        private Context context;
        private SectionedRecyclerViewAdapter adapter;
        private String parentSectionTag;
        private WorkWeek workWeek;

        public Builder() {}

        public Builder setContext(Context context) {
            this.context = context;
            return this;
        }

        public WorkWeekSection.Builder setAdapter(SectionedRecyclerViewAdapter adapter) {
            this.adapter = adapter;
            return this;
        }

        public Builder setTag(String tag) {
            this.parentSectionTag = tag;
            return this;
        }

        public Builder setWorkWeek(WorkWeek workWeek) {
            this.workWeek = workWeek;
            return this;
        }

        public WorkWeekSection build() {
            return new WorkWeekSection(this);
        }

    }

}
