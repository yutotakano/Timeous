package io.takano.timeous;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.takano.timeous.routines.Routine;
import io.takano.timeous.timers.Timer;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class AddEditRoutineActivity extends AppCompatActivity {
    public static final String EXTRA_ROUTINE = "io.takano.timeous.EXTRA_ROUTINE";
    public static final String EXTRA_TIMERS = "io.takano.timeous.EXTRA_TIMERS";

    private TextInputEditText editTextName;
    private Routine editingTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_routine);

        editTextName = findViewById(R.id.textInputName);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_close_24);

        List<Timer> timers = (List<Timer>) getIntent().getSerializableExtra(EXTRA_TIMERS);

        if (getIntent().hasExtra(EXTRA_ROUTINE)) {
            setTitle("Edit Routine");
            editingTimer = (Routine) getIntent().getSerializableExtra(EXTRA_ROUTINE);
            editTextName.setText(editingTimer.getName());
        } else {
            setTitle("Create new routine");
            editingTimer = new Routine(null);
        }

        // Initialise the RecyclerView for timers
        RecyclerView recyclerView = findViewById(R.id.timersRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(false);

        final TimerListAdapter timerListAdapter = new TimerListAdapter();
        recyclerView.setAdapter(timerListAdapter);
    }

    private void saveTimer() {
        String name = editTextName.getEditableText().toString();
        if (name.trim().isEmpty()) {
            Toast.makeText(this, "Please enter a name", Toast.LENGTH_SHORT).show();
            return;
        }
        editingTimer.setName(name);

        Intent intent = new Intent();
        intent.putExtra(EXTRA_ROUTINE, editingTimer);
        intent.putExtra(EXTRA_TIMERS, new ArrayList<Timer>());

        setResult(RESULT_OK, intent);
        finish(); // close activity
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_edit_timer_menu, menu);
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