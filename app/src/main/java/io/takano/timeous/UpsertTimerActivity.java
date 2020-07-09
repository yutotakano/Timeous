package io.takano.timeous;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.takano.timeous.timerGroups.TimerGroup;
import io.takano.timeous.timers.Timer;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

public class UpsertTimerActivity extends AppCompatActivity {
    private TimerViewModel timerViewModel;
    private LiveData<Long> timerGroupId = new LiveData<Long>() {};
    private LiveData<List<Timer>> timers = new LiveData<List<Timer>>() {};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upsert_timer_group);

        // Init ViewModel
        timerViewModel = new ViewModelProvider(this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(this.getApplication()))
                .get(TimerViewModel.class);

        // Get the upsert mode (add or modify?) and set the internal timerGroupId;
        Integer mode = (Integer) getIntent().getSerializableExtra("mode");
        Long id = (Long) getIntent().getSerializableExtra("id");
        if (mode != null && mode == 1) {
            setTitle("Add new timer");
            // create a new timer and use the id
            timerGroupId = timerViewModel.insertTimerGroup(new TimerGroup("Timer"));
        } else {
            setTitle("Modify timer");
            // use a temporary variable since I somehow can't set the value of a LiveData
            MutableLiveData<Long> temporary = new MutableLiveData<>();
            temporary.setValue(id);
            timerGroupId = temporary;
        }

        // Initialise the RecyclerView for timers
        RecyclerView recyclerView = findViewById(R.id.timersRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(false);

        final TimerAdapter timerAdapter = new TimerAdapter();
        recyclerView.setAdapter(timerAdapter);

        // Act when the timerGroup is created/loaded
        timerGroupId.observe(this, new Observer<Long>() {
            @Override
            public void onChanged(Long aLong) {
                if(aLong != null) {
                    Log.d("test", aLong.toString());
                    timers = timerViewModel.getTimersInGroup(aLong);
                    timers.observe(UpsertTimerActivity.this, new Observer<List<Timer>>() {
                        @Override
                        public void onChanged(List<Timer> timers) {
                            Log.d("aaa", "aaaa");
                            Toast.makeText(UpsertTimerActivity.this, "woo", Toast.LENGTH_SHORT).show();
                            timerAdapter.setTimerGroups(timers);
                        }
                    });
                }
            }
        });



    }
}