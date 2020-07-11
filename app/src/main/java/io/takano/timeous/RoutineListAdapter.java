package io.takano.timeous;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import io.takano.timeous.routines.Routine;

public class RoutineListAdapter extends RecyclerView.Adapter<RoutineListAdapter.TimerGroupHolder> {
    // Using ArrayList instead of List makes it non-null, cutting needs to check for null
    private List<Routine> routines = new ArrayList<>();
    private OnItemClickListener listener;

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

        public TimerGroupHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewName);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    // make sure we don't click something that doesn't exist, like during a
                    // deletion animation
                    if (listener != null && position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(routines.get(position));
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Routine routine);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
