package com.example.mhealth;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class RegisterSensorBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();
		 Intent i = new Intent();
	     i.setClassName("com.example.mhealth", "com.example.mhealth.Create_Sensor_Automatically");
	     i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	     i.putExtra("Sensor", bundle.getString("Sensor"));
	     System.out.println("Sensor "+ bundle.getString("Sensor"));
		 i.putExtra("Package_Name",bundle.getString("Package_Name"));
	     System.out.println("Package_Name "+bundle.getString("Package_Name"));
	     i.putExtra("Concepts", bundle.getStringArray("Concepts"));//concept string array
	     String [] concept = bundle.getStringArray("Concepts");
		 for(int count=0;count<bundle.getStringArray("Concepts").length;count++)
		 {
			 
			 System.out.println("Concept_"+count+"  "+concept[count]);
		 }
		context.startActivity(i);
				
	}
}
	
	
