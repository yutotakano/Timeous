package io.takano.timeous;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import io.takano.timeous.timers.Timer;

public class TimerListAdapter extends RecyclerView.Adapter<TimerListAdapter.TimerHolder> {
    private List<Timer> timers = new ArrayList<>();
    public static final Integer VIEW_TYPE_CELL = 1;
    public static final Integer VIEW_TYPE_BUTTON = 2;
    private OnAddClickListener listener;

    @NonNull
    @Override
    public TimerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        // show add button for the last cell
        if (viewType == VIEW_TYPE_CELL) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_timer, parent, false);
        } else {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_timer_add, parent, false);
        }
        return new TimerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TimerHolder holder, int position) {
        // Check that position exists, for timers.get() returns OutOfBounds without it for the last item
        if (position != timers.size()) {
            Timer currentTimer = timers.get(position);
            holder.editTextSeconds.setText(String.valueOf(currentTimer.getSeconds()));
            holder.textViewName.setText(currentTimer.getName());
        }
    }

    @Override
    public int getItemViewType(int position) {
        return (position == timers.size()) ? VIEW_TYPE_BUTTON : VIEW_TYPE_CELL;
    }

    @Override
    public int getItemCount() {
        return timers.size() + 1;
    }

    /**
     * Set the timers into internal variable `timers`, after sorting them.
     * Initialise a new ArrayList if timers is null.
     *
     * @param timers List of timers to set
     */
    public void setTimers(List<Timer> timers) {
        if (timers != null) {
            Collections.sort(timers, new Comparator<Timer>() {
                public int compare(Timer o1, Timer o2) {
                    if (o1.getOrder().equals(o2.getOrder()))
                        return 0;
                    return o1.getOrder() < o2.getOrder() ? -1 : 1;
                }
            });
        } else {
            timers = new ArrayList<>();
        }
        this.timers = timers;
        notifyDataSetChanged();
    }

    class TimerHolder extends RecyclerView.ViewHolder {
        private final TextView textViewName;
        private final TextInputEditText editTextSeconds;

        public TimerHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewName);
            editTextSeconds = itemView.findViewById(R.id.editTextSeconds);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final int position = getAdapterPosition();
                    if (position == timers.size() || (timers.size() == 0 && position == -1)) {
                        listener.onAddClick();
                    }
                }
            });
        }
    }

    public interface OnAddClickListener {
        void onAddClick();
    }

    public void setOnAddClickListener(TimerListAdapter.OnAddClickListener listener) {
        this.listener = listener;
    }

}
