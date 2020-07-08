package io.takano.timeous;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.takano.timeous.timerGroups.TimerGroup;
import io.takano.timeous.timers.Timer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TimerViewModel timerViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.mainRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        final TimerGroupAdapter adapter = new TimerGroupAdapter();
        recyclerView.setAdapter(adapter);

        // we want to initialise the viewmodel, but if we do new ViewModel() here then it will get
        // created everytime for each activity
        // instead we use Android's built-in provider, which takes as argument a fragment scope to
        // bind its lifecycle to if it has to create a new one
        timerViewModel = new ViewModelProvider(this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(this.getApplication()))
                .get(TimerViewModel.class);
        timerViewModel.getAllTimerGroups().observe(this, new Observer<List<TimerGroup>>() {
            @Override
            public void onChanged(List<TimerGroup> timerGroups) {
                adapter.setTimerGroups(timerGroups);
            }
        });

//        timerViewModel.getAllTimers().observe(this, new Observer<List<Timer>>() {
//            @Override
//            public void onChanged(List<Timer> timers) {
//                Log.d("abc", timers.toString());
//            }
//        });

        FloatingActionButton addButton = (FloatingActionButton) findViewById(R.id.mainAppBarAdd);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), UpsertTimerActivity.class);
                intent.putExtra("mode", 1);
                startActivity(intent);
            }
        });
    }

}