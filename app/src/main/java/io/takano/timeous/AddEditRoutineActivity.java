package io.takano.timeous;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.takano.timeous.routines.Routine;
import io.takano.timeous.timers.Timer;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class AddEditRoutineActivity extends AppCompatActivity {
    public static final String EXTRA_ROUTINE = "io.takano.timeous.EXTRA_ROUTINE";
    public static final String EXTRA_TIMERS = "io.takano.timeous.EXTRA_TIMERS";
    public static final int RESULT_DELETE = RESULT_FIRST_USER + 1;

    public static Boolean showDelete = false;
    private TextInputEditText editTextName;
    private Routine editingRoutine;
    private final MutableLiveData<List<Timer>> editingTimers = new MutableLiveData<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_routine);

        editTextName = findViewById(R.id.textInputName);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_close_24);
        }

        initializeViews();

        // Initialise the RecyclerView for timers
        RecyclerView recyclerView = findViewById(R.id.timersRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(false);

        final TimerListAdapter timerListAdapter = new TimerListAdapter();
        recyclerView.setAdapter(timerListAdapter);
        timerListAdapter.setOnAddClickListener(new TimerListAdapter.OnAddClickListener() {
            @Override
            public void onAddClick() {
                insertTimer();
            }
        });
        editingTimers.observe(this, new Observer<List<Timer>>() {
            @Override
            public void onChanged(List<Timer> timers) {
                timerListAdapter.setTimers(timers);
            }
        });
        //noinspection unchecked
        editingTimers.setValue((List<Timer>) getIntent().getSerializableExtra(EXTRA_TIMERS));

    }

    private void initializeViews() {
        if (getIntent().hasExtra(EXTRA_ROUTINE)) {
            setTitle("Edit Routine");
            showDelete = true;
            editingRoutine = (Routine) getIntent().getSerializableExtra(EXTRA_ROUTINE);
            if (editingRoutine == null) {
                setResult(RESULT_CANCELED);
                finish();
            }
            editTextName.setText(editingRoutine.getName());
        } else {
            setTitle("Create new routine");
            showDelete = false;
            editingRoutine = new Routine(null);
            if (editTextName.requestFocus()) {
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            }
        }
    }

    private void insertTimer() {
        List<Timer> currentTimers = editingTimers.getValue();
        if (currentTimers == null) {
            currentTimers = new ArrayList<>();
        }
        currentTimers.add(new Timer(-1L, 0, "test", 30));
        editingTimers.setValue(currentTimers);
    }

    private void deleteRoutine() {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_ROUTINE, editingRoutine);
        setResult(RESULT_DELETE, intent);
        finish();
    }

    private void saveRoutine() {
        String name = editTextName.getEditableText().toString();
        if (name.trim().isEmpty()) {
            Toast.makeText(this, "Please enter a name", Toast.LENGTH_SHORT).show();
            return;
        }
        editingRoutine.setName(name);

        Intent intent = new Intent();
        intent.putExtra(EXTRA_ROUTINE, editingRoutine);
        intent.putExtra(EXTRA_TIMERS, (Serializable) editingTimers.getValue());

        setResult(RESULT_OK, intent);
        finish(); // close activity
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_edit_routine_menu, menu);
        // show or hide delete button
        menu.getItem(0).setVisible(showDelete);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.saveTimer:
                saveRoutine();
                return true;
            case R.id.deleteTimer:
                deleteRoutine();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}