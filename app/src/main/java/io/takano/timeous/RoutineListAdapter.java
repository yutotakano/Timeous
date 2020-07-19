package io.takano.timeous;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.progressindicator.ProgressIndicator;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import io.takano.timeous.routines.Routine;

public class RoutineListAdapter extends ListAdapter<Routine, RoutineListAdapter.TimerGroupHolder> {
    private OnItemClickListener listener;
    private OnEditClickListener onEditClickListener;
    private ProgressIndicator visibleBar;

    private static final DiffUtil.ItemCallback<Routine> DIFF_CALLBACK = new DiffUtil.ItemCallback<Routine>() {
        @Override
        public boolean areItemsTheSame(@NonNull Routine oldItem, @NonNull Routine newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Routine oldItem, @NonNull Routine newItem) {
            return oldItem.getName().equals(newItem.getName());
        }
    };

    public RoutineListAdapter() {
        super(DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public TimerGroupHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_routine, parent, false);
        return new TimerGroupHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TimerGroupHolder holder, int position) {
        Routine currentRoutine = getItem(position);
        holder.textViewName.setText(currentRoutine.getName());
    }

    class TimerGroupHolder extends RecyclerView.ViewHolder {
        private final TextView textViewName;
        private final ImageView editButton;
        private final ProgressIndicator progressIndicator;

        public TimerGroupHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewName);
            editButton = itemView.findViewById(R.id.editRoutineButton);
            progressIndicator = itemView.findViewById(R.id.indeterminateProgress);
            progressIndicator.setVisibility(View.INVISIBLE);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    listener.onItemClick(getItem(position), position);
                }
            });

            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    // make sure we don't click something that doesn't exist, like during a
                    // deletion animation
                    if (listener != null && position != RecyclerView.NO_POSITION) {
                        progressIndicator.setVisibility(View.VISIBLE);
                        visibleBar = progressIndicator;
                        onEditClickListener.onEditClick(getItem(position), position);
                    }
                }
            });

        }
    }

    public interface OnItemClickListener {
        void onItemClick(Routine routine, int position);
    }

    public interface OnEditClickListener {
        void onEditClick(Routine routine, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setOnEditClickListener(OnEditClickListener listener) {
        this.onEditClickListener = listener;
    }
    public void clearProgress(int position) {
        visibleBar.setVisibility(View.INVISIBLE);
        visibleBar = null;
    }

}
