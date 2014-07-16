package com.example.mhealth;

import java.io.IOException;

import com.couchbase.lite.Manager;
import com.couchbase.lite.android.AndroidContext;

import android.support.v7.app.ActionBarActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Options extends ActionBarActivity implements View.OnClickListener{

	Button sensor_read_manual,sensor_read_auto,find_patient,create_patient,create_sensor;
	String password;
	String username;
	String url;
	static couch_api ca; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.option_layout);
		init_ca();
		sensor_read_auto = (Button)findViewById(R.id.button7);
		find_patient=(Button)findViewById(R.id.button2);
		create_patient=(Button)findViewById(R.id.button3);
		sensor_read_manual=(Button)findViewById(R.id.button5);
		create_sensor=(Button)findViewById(R.id.button6);
		Bundle extras = getIntent().getExtras();
		SharedPreferences sharedPref = getSharedPreferences("mhealth", Context.MODE_PRIVATE);
        username = sharedPref.getString(getString(R.string.username), "");
		password = sharedPref.getString(getString(R.string.password), "");
		url = sharedPref.getString(getString(R.string.url), "");
		
		sensor_read_auto.setOnClickListener(new Button.OnClickListener() {  
			public void onClick(View v)
            {
				Intent intent = new Intent(Options.this,Sensor_Listing.class);
				//intent.putExtra("db", ca.sensors_db_name);
				//intent.putExtra("view", ca.sensor_view);
				startActivity(intent);
				
            }
		}
		);
       
		sensor_read_manual.setOnClickListener(this);
		find_patient.setOnClickListener(this);
		create_patient.setOnClickListener(this);
		create_sensor.setOnClickListener(this);
		
		
	}
	
	public void init_ca(){
		if(ca==null){
			Manager manager = null;
			try {
				manager = new Manager(new AndroidContext(this), Manager.DEFAULT_OPTIONS);
				System.out.println("Manager Created!");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		    this.ca = new couch_api(manager);
		    if(ca == null){
		    	System.out.println("Failed to Set CouchApi");
		    }      
		    System.out.println("Initialized ca in Sync Activity");
		}
		else{
			System.out.println("ca variable already initialized");
		}
	}
	
	/** Called when the user clicks the Visualize Sensors button */
	public void viewSensors(View view) {
		System.out.println("Viewing sensors");
	}
	
	
	
	public void buttonClick1()
	{
		Intent intent = new Intent(this, Get_Patient_information.class);
		startActivity(intent);
		
	}
	
	public void buttonClick2()
	{
		Intent intent1 = new Intent(this, Create_Patient.class);
		startActivity(intent1);
	}
	
	public void buttonClick4()
	{
		Intent intent1 = new Intent(this,Sensor_Reading_Manually.class);
		startActivity(intent1);
	}
	
	public void buttonClick5()
	{
		Intent intent1 = new Intent(this,Create_Sensor_Manually.class);
    	startActivity(intent1);
	}
	
	/** Called when the user clicks the Sync button */
	public void startSyncActivity(View view) {
	    System.out.println("Clicked Sync Button");
		Intent intent1 = new Intent(this,SyncActivity.class);
		startActivity(intent1);
	}
	
	/** Called when the user clicks the Settings button */
	public void SettingsActivity(View view) 
	{
	    System.out.println("Clicked Sync Button");
		Intent intent1 = new Intent(this,Login.class);
		startActivity(intent1);
	}
	
	public void onClick(View v) {
		
			switch (v.getId())
			{
			case R.id.button2:
				buttonClick1();
				break;			
			case R.id.button3:
				buttonClick2();
				break;			
			case R.id.button5:
				buttonClick4();
				break;
			case R.id.button6:
				buttonClick5();
				break;		
			
			
		
			}
				
			}


	

	
	

}
