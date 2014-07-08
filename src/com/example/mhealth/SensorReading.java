package com.example.mhealth;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.os.Build;

public class SensorReading extends ListActivity {

	String sensors[];
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sensor_reading);
		final String REGISTERING = "Registering";
		getApplicationContext();
		SharedPreferences pref = getApplicationContext().getSharedPreferences(REGISTERING,Context.MODE_PRIVATE);
	    String list = pref.getString("sensor_pack_map", "");
		Iterator<String> keys ;
		ListView lv= getListView();
		JSONObject jo ;
		try {
			jo= new JSONObject(list);
			sensors = new String[jo.length()];
			int i=0;
			keys = jo.keys();
		    while(keys.hasNext()){
		        String key = keys.next();
		        String val = null;
		        try{
		        	Toast.makeText(this,"Listing  - " + key,Toast.LENGTH_SHORT).show();
		             sensors[i++]=key;
		             
		           }
		        finally
		        {
		        }

			}
		    lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
			lv.setTextFilterEnabled(true);
			setListAdapter ((ListAdapter) new ArrayAdapter<String>(this,android.R.layout.simple_list_item_checked,sensors));
		}
		 catch(Exception e){
	            
	        }
		
		
		
}
	protected void onListItemClick(ListView l, View v, int position, long id)
	{
	// TODO Auto-generated method stub
	 
	//STEP 9:Text to be displayed in toast message, here it is set as name of the item clicked
	Toast.makeText(this,"You selected the sensor - " + sensors[position],Toast.LENGTH_SHORT).show();
	Intent intent1 = new Intent(this,Sensorconcept.class);
	intent1.putExtra("sensor", sensors[position] );
	startActivity(intent1);
	
	}
	
	}
