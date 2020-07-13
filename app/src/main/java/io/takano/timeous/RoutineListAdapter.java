package io.takano.timeous;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.progressindicator.ProgressIndicator;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import io.takano.timeous.routines.Routine;

public class RoutineListAdapter extends RecyclerView.Adapter<RoutineListAdapter.TimerGroupHolder> {
    // Using ArrayList instead of List makes it non-null, cutting needs to check for null
    private List<Routine> routines = new ArrayList<>();
    private OnItemClickListener listener;
    private ProgressIndicator visibleBar;

    @NonNull
    @Override
    public TimerGroupHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_routine, parent, false);
        return new TimerGroupHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TimerGroupHolder holder, int position) {
        Routine currentRoutine = routines.get(position);
        holder.textViewName.setText(currentRoutine.getName());
    }

    @Override
    public int getItemCount() {
        return routines.size();
    }

    public void setRoutines(List<Routine> routines) {
        this.routines = routines;
        notifyDataSetChanged();
    }

    class TimerGroupHolder extends RecyclerView.ViewHolder {
        private final TextView textViewName;
        private final ProgressIndicator progressIndicator;

        public TimerGroupHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewName);
            progressIndicator = itemView.findViewById(R.id.indeterminateProgress);
            progressIndicator.setVisibility(View.INVISIBLE);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    // make sure we don't click something that doesn't exist, like during a
                    // deletion animation
                    if (listener != null && position != RecyclerView.NO_POSITION) {
                        progressIndicator.setVisibility(View.VISIBLE);
                        visibleBar = progressIndicator;
                        listener.onItemClick(routines.get(position), position);
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Routine routine, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void clearProgress(int position) {
        visibleBar.setVisibility(View.INVISIBLE);
        visibleBar = null;
    }

}
