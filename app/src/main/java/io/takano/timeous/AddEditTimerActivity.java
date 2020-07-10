package io.takano.timeous;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.takano.timeous.timerGroups.TimerGroup;
import io.takano.timeous.timers.Timer;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class AddEditTimerActivity extends AppCompatActivity {
    public static final String EXTRA_TIMER_GROUP = "io.takano.timeous.EXTRA_TIMER_GROUP";
    public static final String EXTRA_TIMERS = "io.takano.timeous.EXTRA_TIMERS";

    private TextInputEditText editTextName;
    private TimerGroup editingTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_timer);

        editTextName = findViewById(R.id.textInputName);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_close_24);

        List<Timer> timers = (List<Timer>) getIntent().getSerializableExtra(EXTRA_TIMERS);

        if (getIntent().hasExtra(EXTRA_TIMER_GROUP)) {
            setTitle("Modify timer");
            editingTimer = (TimerGroup) getIntent().getSerializableExtra(EXTRA_TIMER_GROUP);
            editTextName.setText(editingTimer.getName());
        } else {
            setTitle("Add new timer");
            editingTimer = new TimerGroup(null);
        }

        // Initialise the RecyclerView for timers
        RecyclerView recyclerView = findViewById(R.id.timersRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(false);

        final TimerAdapter timerAdapter = new TimerAdapter();
        recyclerView.setAdapter(timerAdapter);
    }

    private void saveTimer() {
        String name = editTextName.getEditableText().toString();
        if (name.trim().isEmpty()) {
            Toast.makeText(this, "Please enter a name", Toast.LENGTH_SHORT).show();
            return;
        }
        editingTimer.setName(name);

        Intent intent = new Intent();
        intent.putExtra(EXTRA_TIMER_GROUP, editingTimer);
        intent.putExtra(EXTRA_TIMERS, new ArrayList<Timer>());

        setResult(RESULT_OK, intent);
        finish(); // close activity
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.upsert_timer_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.saveTimer:
                saveTimer();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}