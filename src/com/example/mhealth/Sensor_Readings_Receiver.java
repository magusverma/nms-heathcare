package com.example.mhealth;

import java.util.Set;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

@
SuppressLint("NewApi")
public class Sensor_Readings_Receiver extends BroadcastReceiver {

    @
    Override
    public void onReceive(Context context, Intent intent) {
        Integer concept_readings[] = new Integer[50];
        String concept_ids[] = new String[50];
        final String READINGS = "Sensor_Readings";
        SharedPreferences prefs = context.getSharedPreferences(READINGS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Integer count = 0;
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            Set < String > keys = bundle.keySet();
            for (String key: keys) {
                concept_ids[count] = key;
                concept_readings[count] = bundle.getInt(concept_ids[count]);
                editor.putString(String.valueOf(count), concept_ids[count]);
                editor.putInt(concept_ids[count], concept_readings[count]);
                count++;

            }
            editor.putInt("array_size", count);
            editor.commit();
        } else {
            Toast.makeText(context, "values r null broadcast", Toast.LENGTH_LONG).show();
        }
    }
}