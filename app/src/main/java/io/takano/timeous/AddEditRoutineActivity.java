package io.takano.timeous;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.takano.timeous.database.Routine;
import io.takano.timeous.database.Timer;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.NumberPicker;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import java.io.Serializable;
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
        timerListAdapter.setOnItemClickListener(new TimerListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(final Timer timer) {
                final View durationPickerView = createNewDurationPickerView();
                preFillDurationPickerView(durationPickerView, timer.getSeconds(), timer.getName());
                new MaterialAlertDialogBuilder(AddEditRoutineActivity.this)
                        .setView(durationPickerView)
                        .setCancelable(false)
                        .setNeutralButton("Cancel", null)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String name = parseDurationPickerName(durationPickerView);
                                Integer totalSeconds = parseDurationPickerValues(durationPickerView);
                                timer.setName(name);
                                timer.setSeconds(totalSeconds);
                                timerListAdapter.notifyDataSetChanged();
                            }
                        })
                        .show();
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

    private View createNewDurationPickerView() {
        View view = View.inflate(this, R.layout.duration_picker_alert, null);
        NumberPicker hoursPicker = view.findViewById(R.id.hoursPicker);
        NumberPicker minutesPicker = view.findViewById(R.id.minutesPicker);
        NumberPicker secondsPicker = view.findViewById(R.id.secondsPicker);
        hoursPicker.setMaxValue(99);
        hoursPicker.setMinValue(0);
        minutesPicker.setMaxValue(59);
        minutesPicker.setMinValue(0);
        secondsPicker.setMaxValue(59);
        secondsPicker.setMinValue(0);
        return view;
    }

    private void preFillDurationPickerView(View view, Integer totalSeconds, String name) {
        int hours = (int) Math.floor(totalSeconds / 3600.0);
        int minutes = (int) Math.floor((totalSeconds - (hours * 3600)) / 60.0);
        int seconds = totalSeconds - (hours * 3600) - (minutes * 60);
        NumberPicker hoursPicker = view.findViewById(R.id.hoursPicker);
        NumberPicker minutesPicker = view.findViewById(R.id.minutesPicker);
        NumberPicker secondsPicker = view.findViewById(R.id.secondsPicker);
        TextInputEditText editText = view.findViewById(R.id.textInputTimerName);
        hoursPicker.setValue(hours);
        minutesPicker.setValue(minutes);
        secondsPicker.setValue(seconds);
        editText.setText(name);
    }

    private String parseDurationPickerName(View view) {
        TextInputEditText editText = view.findViewById(R.id.textInputTimerName);
        return editText.getEditableText().toString();
    }

    private Integer parseDurationPickerValues(View view) {
        NumberPicker hoursPicker = view.findViewById(R.id.hoursPicker);
        NumberPicker minutesPicker = view.findViewById(R.id.minutesPicker);
        NumberPicker secondsPicker = view.findViewById(R.id.secondsPicker);
        return hoursPicker.getValue() * 3600 + minutesPicker.getValue() * 60 + secondsPicker.getValue();
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
        currentTimers.add(new Timer(-1L, currentTimers.size(), "Timer " + (currentTimers.size() + 1), 30));
        editingTimers.setValue(currentTimers);
    }

    private void deleteRoutine() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Delete?")
                .setMessage("You can not undo this action.")
                .setNeutralButton("Cancel", null)
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent();
                        intent.putExtra(EXTRA_ROUTINE, editingRoutine);
                        setResult(RESULT_DELETE, intent);
                        finish();
                    }
                })
                .show();
    }

    private void saveRoutine() {
        String name = editTextName.getEditableText().toString();
        if (name.trim().isEmpty()) {
            new MaterialAlertDialogBuilder(this)
                    .setMessage("Please enter a routine name")
                    .show();
            return;
        }
        if (editingTimers.getValue() == null || editingTimers.getValue().size() == 0) {
            new MaterialAlertDialogBuilder(this)
                    .setMessage("Please add one or more timers")
                    .show();
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
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.saveRoutine:
                saveRoutine();
                return true;
            case R.id.deleteRoutine:
                deleteRoutine();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Are you sure?")
                .setMessage("Any active changes will be lost.")
                .setNegativeButton("No, Stay", null)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        AddEditRoutineActivity.super.onBackPressed();
                    }
                })
                .show();
    }


}