package com.example.mhealth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
//import org.json.simple.JSONValue;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

public class RegisterBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Bundle bundle = intent.getExtras();
		Toast.makeText(context, "in registerbroadcast", Toast.LENGTH_SHORT).show();
		 Intent i = new Intent();
	     i.setClassName("com.example.mhealth", "com.example.mhealth.Create_Sensor");
	     i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	     int num = bundle.getInt("Concept_num");
	     i.putExtra("Conc_num", num);
	     i.putExtra("Sensor", bundle.getString("Sensor"));
		 i.putExtra("Package_Name",bundle.getString("Package_Name"));
		 for(int count=1;count<num+1;count++)
		 {
			 i.putExtra("Concept_"+count,bundle.getString("Concept_"+count));
		 }
		context.startActivity(i);
				
	}
}
	
	
