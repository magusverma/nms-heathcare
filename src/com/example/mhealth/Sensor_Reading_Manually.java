package com.example.mhealth;

import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Sensor_Reading_Manually extends ActionBarActivity implements View.OnClickListener {

    EditText sensor, patient;
    Button b1, b2;;
    String query;
    LinearLayout LLayout1;
    String username;
    String password;
    String url;
    TextView enter_sensor_id;
    HashMap < String, String > scm_name_to_id;
    String[] sensor_name_hinting_array;
    static couch_api ca;

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LLayout1 = new LinearLayout(this);
        LLayout1.setOrientation(LinearLayout.VERTICAL);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        SharedPreferences sharedPref = getSharedPreferences("mhealth", Context.MODE_PRIVATE);
        username = sharedPref.getString(getString(R.string.username), "");
        password = sharedPref.getString(getString(R.string.password), "");
        url = sharedPref.getString(getString(R.string.url), "");
        enter_sensor_id = new TextView(this);
        enter_sensor_id.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        enter_sensor_id.setText("Enter sensor Id");
        LLayout1.addView(enter_sensor_id);
        sensor = new EditText(this);
        sensor.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        LLayout1.addView(sensor);
        b1 = new Button(this);
        b1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        b1.setText("Get Concepts");
        b1.setOnClickListener(this);
        LLayout1.addView(b1);
        setContentView(LLayout1);

    }
    @Override
    public void onClick(View v) {

        if (sensor.getText().toString().length() < 1) {
            Toast.makeText(getApplicationContext(), "Enter Complete Details", Toast.LENGTH_LONG).show();
        } else {
            Intent intent = new Intent(this, Sensor_reading_manually_on_click.class);
            intent.putExtra("Sensor", sensor.getText().toString());
            startActivity(intent);
        }

    }
}
