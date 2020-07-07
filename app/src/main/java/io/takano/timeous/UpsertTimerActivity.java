package io.takano.timeous;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class UpsertTimerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_timer);
        Integer mode = (Integer) getIntent().getSerializableExtra("mode");
        if (mode != null && mode == 1) {
            setTitle("Add a Timer");
        } else {
            setTitle("Modify a Timer");
        }
    }
}