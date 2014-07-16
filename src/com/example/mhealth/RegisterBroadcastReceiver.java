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
		//Toast.makeText(context, "in registerbroadcast", Toast.LENGTH_SHORT).show();
		 Intent i = new Intent();
	     i.setClassName("com.example.mhealth", "com.example.mhealth.Create_Sensor");
	     i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	     i.putExtra("Sensor", bundle.getString("Sensor"));
	     System.out.println("Sensor "+ bundle.getString("Sensor"));
		 i.putExtra("Package_Name",bundle.getString("Package_Name"));
	     System.out.println("Package_Name "+bundle.getString("Package_Name"));
	     i.putExtra("Concepts", bundle.getStringArray("Concepts"));//concept string array
	     System.out.println("length of @@******"+bundle.getStringArray("Concepts").length);
	     String [] arr = bundle.getStringArray("Concepts");
		 for(int count=0;count<bundle.getStringArray("Concepts").length;count++)
		 {
			 
			 System.out.println("*********Concept_"+count+"  "+arr[count]);
		 }
		context.startActivity(i);
				
	}
}
	
	
