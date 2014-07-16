package com.example.mhealth;

import java.util.Iterator;
import org.json.JSONObject;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class Sensor_Listing extends ListActivity {

	String sensors[];
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);         
		final String REGISTER = "Register_Sensor";
		getApplicationContext();
		SharedPreferences pref = getApplicationContext().getSharedPreferences(REGISTER,Context.MODE_PRIVATE);
	    String list = pref.getString("sensor_pack_map", "");
		Iterator<String> keys ;
		JSONObject jo ;
		try {
			jo= new JSONObject(list);
			sensors = new String[jo.length()];
			int i=0;
			keys = jo.keys();
		    while(keys.hasNext()){
		        String key = keys.next();
		        String value = null;
		        try{
		        	 sensors[i++]=key;
		             
		           }
		        finally
		        {
		        }

			}
		    
		}
		 catch(Exception e){
	            
	        }
		try{
        ListView lv = getListView(); 
        lv.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_checked,sensors));
        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		lv.setTextFilterEnabled(true);
		}
		 catch(NullPointerException e) {
		   Toast.makeText(getApplicationContext(), "No sensors registered", Toast.LENGTH_SHORT).show();
		}
        
 }
        

		
		
		
		

		protected void onListItemClick(ListView l, View v, int position, long id)
		{
		
				Toast.makeText(this,"You selected the sensor - " + sensors[position],Toast.LENGTH_SHORT).show();
				Intent intent1 = new Intent(this,Sensor_Reading_Automatically.class);
				intent1.putExtra("sensor", sensors[position] );
				startActivity(intent1);
		
		}
	
	}
