package io.takano.timeous;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.takano.timeous.database.Routine;
import io.takano.timeous.database.RoutineWithTimers;
import io.takano.timeous.database.Timer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.Serializable;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DataViewModel dataViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // we want to initialise the ViewModel, but if we do new ViewModel() here then it will get
        // created every time for each activity
        // instead we use Android's built-in provider, which takes as argument a fragment scope to
        // bind its lifecycle to if it has to create a new one
        dataViewModel = new ViewModelProvider(this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(this.getApplication()))
                .get(DataViewModel.class);

        RecyclerView recyclerView = findViewById(R.id.mainRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));

        final RoutineListAdapter adapter = new RoutineListAdapter();
        recyclerView.setAdapter(adapter);

        final ActivityResultLauncher<Intent> addIntentActivityResultLauncher =
                registerForActivityResult(
                        new ActivityResultContracts.StartActivityForResult(),
                        new ActivityResultCallback<ActivityResult>() {
                            @Override
                            public void onActivityResult(ActivityResult result) {
                                if (result.getResultCode() == RESULT_OK)
                                    onRoutineAdded(result);
                            }
                        });

        adapter.setOnItemClickListener(new RoutineListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(final Routine routine, final int position) {
                Intent intent = new Intent(MainActivity.this, ActiveTimerActivity.class);
                startActivity(intent);
            }
        });

        adapter.setOnEditClickListener(new RoutineListAdapter.OnEditClickListener() {
            @Override
            public void onEditClick(final RoutineWithTimers routine, final int position) {
                Intent intent = new Intent(MainActivity.this, AddEditRoutineActivity.class);
                intent.putExtra(AddEditRoutineActivity.EXTRA_ROUTINE, routine.routine);
                intent.putExtra(AddEditRoutineActivity.EXTRA_TIMERS, (Serializable) routine.timers);
                final ActivityResultLauncher<Intent> intentActivityResultLauncher =
                        registerForActivityResult(
                                new ActivityResultContracts.StartActivityForResult(),
                                new ActivityResultCallback<ActivityResult>() {
                                    @Override
                                    public void onActivityResult(ActivityResult result) {
                                        adapter.clearProgress(position);
                                        switch (result.getResultCode()) {
                                            case RESULT_OK:
                                                onRoutineEdited(result);
                                                break;
                                            case AddEditRoutineActivity.RESULT_DELETE:
                                                onRoutineDeleted(result);
                                                break;
                                            default:
                                                break;
                                        }
                                    }
                                });
                intentActivityResultLauncher.launch(intent);
            }
        });

        dataViewModel.getAllRoutines().observe(this, new Observer<List<RoutineWithTimers>>() {
            @Override
            public void onChanged(List<RoutineWithTimers> routines) {
                adapter.submitList(routines);
            }
        });

        FloatingActionButton addButton = findViewById(R.id.mainAppBarAdd);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddEditRoutineActivity.class);
                addIntentActivityResultLauncher.launch(intent);
            }
        });
    }

    private void onRoutineAdded(ActivityResult result) {
        if (result.getData() == null) {
            Toast.makeText(this, "There was an error.", Toast.LENGTH_SHORT).show();
            return;
        }
        Routine routine = (Routine) result.getData().getSerializableExtra(AddEditRoutineActivity.EXTRA_ROUTINE);
        @SuppressWarnings("unchecked") final List<Timer> timers = (List<Timer>)
                result.getData().getSerializableExtra(AddEditRoutineActivity.EXTRA_TIMERS);
        if (routine == null || timers == null) {
            Toast.makeText(this, "There was an error.", Toast.LENGTH_SHORT).show();
            return;
        }

        final LiveData<Long> routineResultId = dataViewModel.insertRoutine(routine);

        // insert child timers when timer group is successfully done
        routineResultId.observe(this, new Observer<Long>() {
            @Override
            public void onChanged(Long aLong) {
                if (aLong != null) {
                    for (int i = 0; i < timers.size(); i++) {
                        Timer timer = timers.get(i);
                        dataViewModel.insertTimer(timer, aLong);
                    }
                    routineResultId.removeObserver(this);
                }
            }
        });
    }

    private void onRoutineEdited(ActivityResult result) {
        if (result.getData() == null) {
            Toast.makeText(this, "There was an error.", Toast.LENGTH_SHORT).show();
            return;
        }
        Routine routine = (Routine) result.getData().getSerializableExtra(AddEditRoutineActivity.EXTRA_ROUTINE);
        @SuppressWarnings("unchecked")
        List<Timer> timers = (List<Timer>) result.getData().getSerializableExtra(AddEditRoutineActivity.EXTRA_TIMERS);
        if (routine == null || timers == null) {
            Toast.makeText(this, "There was an error.", Toast.LENGTH_SHORT).show();
            return;
        }

        dataViewModel.updateRoutine(routine);

        for (int i = 0; i < timers.size(); i++) {
            Timer timer = timers.get(i);
            if (timer.getRoutineId() == -1) {
                dataViewModel.insertTimer(timer, routine.getId());
            } else {
                dataViewModel.updateTimer(timer);
            }
        }
        Toast.makeText(this, "Updated routine and " + timers.size() + " timers", Toast.LENGTH_SHORT).show();
    }

    private void onRoutineDeleted(ActivityResult result) {
        if (result.getData() == null) {
            Toast.makeText(this, "There was an error.", Toast.LENGTH_SHORT).show();
            return;
        }
        Routine routine = (Routine) result.getData().getSerializableExtra(AddEditRoutineActivity.EXTRA_ROUTINE);
        if (routine == null) {
            Toast.makeText(this, "There was an error.", Toast.LENGTH_SHORT).show();
            return;
        }
        dataViewModel.deleteRoutine(routine);
        Toast.makeText(this, "Routine and its timers deleted permanently", Toast.LENGTH_SHORT).show();
    }

}