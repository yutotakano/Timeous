package io.takano.timeous;

import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.progressindicator.ProgressIndicator;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import io.takano.timeous.database.Timer;

public class UpcomingTimerListAdapter extends ListAdapter<Timer, UpcomingTimerListAdapter.UpcomingTimerHolder> {
    private OnItemClickListener listener;
    private ProgressIndicator visibleBar;

    private static final DiffUtil.ItemCallback<Timer> DIFF_CALLBACK = new DiffUtil.ItemCallback<Timer>() {
        @Override
        public boolean areItemsTheSame(@NonNull Timer oldItem, @NonNull Timer newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Timer oldItem, @NonNull Timer newItem) {
            return oldItem.getName().equals(newItem.getName());
        }
    };

    public UpcomingTimerListAdapter() {
        super(DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public UpcomingTimerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_upcoming_timer, parent, false);
        return new UpcomingTimerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull UpcomingTimerHolder holder, int position) {
        Timer currentTimer = getItem(position);
        holder.textViewName.setText(currentTimer.getName());
    }

    class UpcomingTimerHolder extends RecyclerView.ViewHolder {
        private final TextView textViewName;

        public UpcomingTimerHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewName);

            itemView.setOnClickListener(new View.OnClickListener() {
                public static final long MIN_INTERVAL = 1000;
                private long lastClickTime = 0;

                @Override
                public void onClick(View view) {
                    long currentTime = SystemClock.elapsedRealtime();
                    if (currentTime - lastClickTime > MIN_INTERVAL) {
                        lastClickTime = currentTime;
                        int position = getAdapterPosition();
                        listener.onItemClick(getItem(position), position);
                    }
                }
            });

        }
    }

    public interface OnItemClickListener {
        void onItemClick(Timer routine, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

}
