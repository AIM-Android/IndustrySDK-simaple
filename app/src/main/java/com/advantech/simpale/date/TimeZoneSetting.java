package com.advantech.simpale.date;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.Nullable;


import com.advantech.simpale.R;

import java.util.TimeZone;

/**
 * ClassName:   TimeZoneSetting
 * Description: TODO
 * CreateDate   2021/09/15
 * Author:  Fengchao.dai
 */
public class TimeZoneSetting extends Activity {
    private ListView listView;
    private Intent intent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timezone_setting);
        intent = getIntent();
        String[] timezoneIDs = TimeZone.getAvailableIDs();
        for (int i = 0; i < timezoneIDs.length; i++) {
            Log.e("##DFC##", i + "  " + timezoneIDs[i]);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(TimeZoneSetting.this, android.R.layout.simple_list_item_1, timezoneIDs);
        listView = findViewById(R.id.lv_time_zone);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String timezoneId = listView.getItemAtPosition(position).toString();
                intent.putExtra(DateTimePreferenceFragment.TIME_ZONE_ID, timezoneId);
                TimeZoneSetting.this.setResult(DateTimePreferenceFragment.SET_TIME_ZONE_RESULT_CODE, intent);
                TimeZoneSetting.this.finish();
            }
        });
    }
}
