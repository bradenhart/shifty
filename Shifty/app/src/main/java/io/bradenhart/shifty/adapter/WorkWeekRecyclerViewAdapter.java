package io.bradenhart.shifty.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.bradenhart.shifty.R;
import io.bradenhart.shifty.domain.WorkWeek;

/**
 * Created by bradenhart on 8/05/17.
 */
// Adapter for displaying WorkWeeks (in Card Views)
public class WorkWeekRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<WorkWeek> workweeks;

    public WorkWeekRecyclerViewAdapter(Context context, List<WorkWeek> workWeeks) {
        this.context = context;
        this.workweeks = workWeeks;
    }

    public WorkWeekRecyclerViewAdapter(Context context) {
        this.context = context;
        this.workweeks = new ArrayList<>();
    }

    public void addWorkWeek(WorkWeek workWeek) {
        this.workweeks.add(workWeek);
    }

    public void removedWorkWeek(int pos) {
        this.workweeks.remove(pos);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.section_workweek_base, parent, false);

        return new WorkWeekViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        WorkWeekViewHolder weekHolder = (WorkWeekViewHolder) holder;

        WorkWeek workWeek = workweeks.get(position);
        ShiftRecyclerViewAdapter adapter = new ShiftRecyclerViewAdapter(context, workWeek.getShifts());
        weekHolder.header.setText(workWeek.getTitle());
        weekHolder.recyclerView.setLayoutManager(new LinearLayoutManager(context.getApplicationContext()));
        weekHolder.recyclerView.setAdapter(adapter);
        weekHolder.footer.setText(String.format(Locale.ENGLISH, "$%.02f", workWeek.getPayslip().getNet()));
    }

    @Override
    public int getItemCount() {
        return workweeks.size();
    }

    class WorkWeekViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.textview_workweek_header)
        TextView header;
        @BindView(R.id.recyclerview_workweek_content)
        RecyclerView recyclerView;
        @BindView(R.id.textview_workweek_footer)
        TextView footer;

        public WorkWeekViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
