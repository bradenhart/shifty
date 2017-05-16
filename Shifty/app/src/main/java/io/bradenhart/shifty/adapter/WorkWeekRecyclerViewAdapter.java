package io.bradenhart.shifty.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.bradenhart.shifty.R;
import io.bradenhart.shifty.activity.PayslipActivity;
import io.bradenhart.shifty.database.DatabaseManager;
import io.bradenhart.shifty.domain.Shift;
import io.bradenhart.shifty.domain.WorkWeek;
import io.bradenhart.shifty.util.DateUtil;
import io.bradenhart.shifty.util.Utils;

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

    public void removeWorkWeek(int pos) {
        this.workweeks.remove(pos);
    }

    public void removeWorkWeek(WorkWeek workWeek) {
        this.workweeks.remove(workWeek);
    }

    public void clear() {
        this.workweeks.clear();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.section_workweek_base, parent, false);

        return new WorkWeekViewHolder(context, view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        WorkWeekViewHolder weekHolder = (WorkWeekViewHolder) holder;

        WorkWeek workWeek = workweeks.get(position);
        weekHolder.pos = position;
        ShiftRecyclerViewAdapter adapter = new ShiftRecyclerViewAdapter(context, workWeek.getShifts());
        adapter.setParentPos(position);
        adapter.setParentAdapter(this);
        weekHolder.header.setText(workWeek.getTitle());
        weekHolder.recyclerView.setLayoutManager(new LinearLayoutManager(context.getApplicationContext()));
        weekHolder.recyclerView.setAdapter(adapter);
        weekHolder.recyclerView.setHasFixedSize(true);
        weekHolder.footer.setText(String.format(Locale.ENGLISH, "$%.02f", workWeek.getPayslip().getNet()));
    }

    @Override
    public int getItemCount() {
        return workweeks.size();
    }

    class WorkWeekViewHolder extends RecyclerView.ViewHolder {

        private Context context;
        Integer pos;
        @BindView(R.id.textview_workweek_header)
        TextView header;
        @BindView(R.id.button_delete_workweek)
        ImageButton deleteButton;
        @BindView(R.id.recyclerview_workweek_content)
        RecyclerView recyclerView;
        @BindView(R.id.textview_workweek_footer)
        TextView footer;

        public WorkWeekViewHolder(Context context, View itemView) {
            super(itemView);
            this.context = context;
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.button_delete_workweek)
        public void onClickDeleteButton() {
            new AlertDialog.Builder(context)
                    .setTitle("Are you sure?")
                    .setMessage("This will delete the entire workweek for good!")
                    .setCancelable(true)
                    .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            List<Shift> shifts = workweeks.get(getAdapterPosition()).getShifts();
                            String[] ids = new String[shifts.size()];

                            int index = 0;
                            for (Shift s : shifts) {
                                ids[index] = s.getId();
                                index++;
                            }

                            new DatabaseManager(context).deleteAllShifts(ids);

                            removeWorkWeek(pos);
                            notifyDataSetChanged();
                            Utils.makeToast(context, "Workweek deleted", Toast.LENGTH_LONG);
                        }
                    })
                    .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    })
                    .show();

        }

        @OnClick(R.id.textview_workweek_footer)
        public void onClickFooter() {
            //TODO stop using id as start date - keep start date column, change id,
            WorkWeek workWeek = workweeks.get(getAdapterPosition());

            PayslipActivity.start(context, workWeek.getPayslip());
        }
    }
}
