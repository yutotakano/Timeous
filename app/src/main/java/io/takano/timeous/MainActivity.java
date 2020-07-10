package io.takano.timeous;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.takano.timeous.timerGroups.TimerGroup;
import io.takano.timeous.timers.Timer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final int UPSERT_NOTE_REQUEST = 1;

    private TimerViewModel timerViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // we want to initialise the viewmodel, but if we do new ViewModel() here then it will get
        // created everytime for each activity
        // instead we use Android's built-in provider, which takes as argument a fragment scope to
        // bind its lifecycle to if it has to create a new one
        timerViewModel = new ViewModelProvider(this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(this.getApplication()))
                .get(TimerViewModel.class);

        RecyclerView recyclerView = findViewById(R.id.mainRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        final TimerGroupAdapter adapter = new TimerGroupAdapter();
        recyclerView.setAdapter(adapter);

        timerViewModel.getAllTimerGroups().observe(this, new Observer<List<TimerGroup>>() {
            @Override
            public void onChanged(List<TimerGroup> timerGroups) {
                adapter.setTimerGroups(timerGroups);
            }
        });

        FloatingActionButton addButton = (FloatingActionButton) findViewById(R.id.mainAppBarAdd);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), UpsertTimerActivity.class);
                intent.putExtra("mode", 1);
                startActivityForResult(intent, UPSERT_NOTE_REQUEST);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @NonNull Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == UPSERT_NOTE_REQUEST && resultCode == RESULT_OK) {
            String name = data.getStringExtra(UpsertTimerActivity.EXTRA_NAME);
            final List<Timer> timers = (List<Timer>)data.getSerializableExtra(UpsertTimerActivity.EXTRA_TIMERS);

            TimerGroup timerGroup = new TimerGroup(name);
            final LiveData<Long> timerGroupId = timerViewModel.insertTimerGroup(timerGroup);

            Toast.makeText(this, "timer group saved", Toast.LENGTH_SHORT).show();
            // insert child timers when timer group is successfully done
            timerGroupId.observe(this, new Observer<Long>() {
                @Override
                public void onChanged(Long aLong) {
                    if (aLong != null) {
                        for (int i = 0; i < timers.size(); i++) {
                            Timer timer = timers.get(i);
                            timerViewModel.insertTimer(timer, aLong);
                            Toast.makeText(MainActivity.this, "inserted timer " + String.valueOf(i) , Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

        } else {
            Toast.makeText(this, "There was an error adding the timer.", Toast.LENGTH_SHORT).show();
        }
    }
}