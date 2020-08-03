package io.takano.timeous;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.takano.timeous.database.Routine;
import io.takano.timeous.database.Timer;

public class ActiveRoutineActivity extends AppCompatActivity {

    public static final String EXTRA_ROUTINE = "io.takano.timeous.EXTRA_ROUTINE";
    public static final String EXTRA_TIMERS = "io.takano.timeous.EXTRA_TIMERS";

    private Routine routine;
    private List<Timer> timers;
    private int timerIndex;

    private TextView timerName;
    private TextView remainingTimeText;
    private TextView remainingTimeMilliText;
    private FloatingActionButton resumePauseFAB;
    private UpcomingTimerListAdapter adapter;

    // default value
    private CountDownTimer countDownTimer;
    private long timerInitialTime = 50000;
    private long timerRemainingTime = timerInitialTime;
    private boolean timerRunning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_routine);

        routine = (Routine) getIntent().getSerializableExtra(EXTRA_ROUTINE);
        timers = (List<Timer>) getIntent().getSerializableExtra(EXTRA_TIMERS);

        timerName = findViewById(R.id.timerName);
        remainingTimeText = findViewById(R.id.timerRemainingTime);
        remainingTimeMilliText = findViewById(R.id.timerRemainingTimeMilli);

        resumePauseFAB = findViewById(R.id.resumePauseButton);

        resumePauseFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (timerRunning) {
                    pauseTimer();
                } else {
                    resumeTimer();
                }
            }
        });

        RecyclerView upcomingTimersRecyclerView = findViewById(R.id.upcomingTimerRecyclerView);
        upcomingTimersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        upcomingTimersRecyclerView.setHasFixedSize(true);
        upcomingTimersRecyclerView.addItemDecoration(
                new DividerItemDecoration(upcomingTimersRecyclerView.getContext(),
                        DividerItemDecoration.VERTICAL));

        adapter = new UpcomingTimerListAdapter();
        upcomingTimersRecyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new UpcomingTimerListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(final Timer timer, final int position) {

            }
        });

        setActiveTimer(0);
    }

    private void setActiveTimer(int index) {
        if (index == timers.size()) {
            return;
        }
        timerIndex = index;
        Timer timer = timers.get(index);
        timerName.setText(timer.getName());
        timerInitialTime = timer.getSeconds() * 1000;
        timerRemainingTime = timer.getSeconds() * 1000;
        updateText();

        List<Timer> allButFirst = new ArrayList<>(timers);
        allButFirst = allButFirst.subList(index + 1, allButFirst.size());
        adapter.submitList(allButFirst);
    }

    private void resumeTimer() {
        countDownTimer = new CountDownTimer(timerRemainingTime, 20) {
            @Override
            public void onTick(long millisUntilFinished) {
                timerRemainingTime = millisUntilFinished;
                updateText();
            }

            @Override
            public void onFinish() {
                timerRemainingTime = 0;
                updateText();
                setActiveTimer(timerIndex + 1);
                resumeTimer();
            }
        }.start();

        timerRunning = true;
        resumePauseFAB.setImageResource(R.drawable.ic_round_pause_24);
    }

    private void pauseTimer() {
        timerRunning = false;
        countDownTimer.cancel();
        resumePauseFAB.setImageResource(R.drawable.ic_round_play_arrow_24);
    }

    private void updateText() {
        int hours = (int) (timerRemainingTime / 1000) / (60 * 60);
        int minutes = (int) (timerRemainingTime / 1000) % (60 * 60) / 60;
        int seconds = (int) (timerRemainingTime / 1000) % 60;
        int millis = (int) (timerRemainingTime % 1000);
        remainingTimeText.setText(String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds));
        remainingTimeMilliText.setText(String.format(Locale.getDefault(), ".%03d", millis));
    }
}