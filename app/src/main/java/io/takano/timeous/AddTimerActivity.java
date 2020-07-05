package io.takano.timeous;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class AddTimerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_timer);
        setTitle("Add a Timer");
    }
}