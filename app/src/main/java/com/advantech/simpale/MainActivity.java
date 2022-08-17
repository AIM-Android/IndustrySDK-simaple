package com.advantech.simpale;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;


import com.advantech.simpale.date.DateTimePreferenceFragment;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.main_framelayout, new DateTimePreferenceFragment())
                .commit();
    }
}