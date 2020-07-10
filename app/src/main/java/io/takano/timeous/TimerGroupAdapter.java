package io.takano.timeous;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import io.takano.timeous.timerGroups.TimerGroup;

public class TimerGroupAdapter extends RecyclerView.Adapter<TimerGroupAdapter.TimerGroupHolder> {
    // Using ArrayList instead of List makes it non-null, cutting needs to check for null
    private List<TimerGroup> timerGroups = new ArrayList<>();
    private OnItemClickListener listener;

    @NonNull
    @Override
    public TimerGroupHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_timer_group, parent, false);
        return new TimerGroupHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TimerGroupHolder holder, int position) {
        TimerGroup currentTimerGroup = timerGroups.get(position);
        holder.textViewName.setText(currentTimerGroup.getName());
    }

    @Override
    public int getItemCount() {
        return timerGroups.size();
    }

    public void setTimerGroups(List<TimerGroup> timerGroups) {
        this.timerGroups = timerGroups;
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
                        listener.onItemClick(timerGroups.get(position));
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(TimerGroup timerGroup);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
