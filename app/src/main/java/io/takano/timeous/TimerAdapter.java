package io.takano.timeous;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import io.takano.timeous.timers.Timer;

public class TimerAdapter extends RecyclerView.Adapter<TimerAdapter.TimerHolder> {
    private List<Timer> timers = new ArrayList<>();

    @NonNull
    @Override
    public TimerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_timer, parent, false);
        return new TimerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TimerHolder holder, int position) {
        Timer currentTimer = timers.get(position);
        holder.textViewSeconds.setText(currentTimer.getSeconds());
        holder.textViewName.setText(currentTimer.getName());
    }

    @Override
    public int getItemCount() {
        return timers.size();
    }

    public void setTimerGroups(List<Timer> timers) {
        // sort by order
        Collections.sort(timers, new Comparator<Timer>(){
            public int compare(Timer o1, Timer o2){
                if(o1.getOrder().equals(o2.getOrder()))
                    return 0;
                return o1.getOrder() < o2.getOrder() ? -1 : 1;
            }
        });
        this.timers = timers;
        notifyDataSetChanged();
    }

    static class TimerHolder extends RecyclerView.ViewHolder {
        private final TextView textViewName;
        private final TextView textViewSeconds;

        public TimerHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewSeconds = itemView.findViewById(R.id.textViewSeconds);
        }
    }
}
