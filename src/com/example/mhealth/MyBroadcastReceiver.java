package com.example.mhealth;

import java.util.*;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;
import android.widget.EditText;
import android.content.*;
import android.content.SharedPreferences.Editor;
import android.graphics.AvoidXfermode.Mode;

@SuppressLint("NewApi")
public class MyBroadcastReceiver extends BroadcastReceiver {

	 Integer array[]=new Integer[10];
	String a2[]=new String[10];
	int n1=3;
	
	@Override
	 public void onReceive(Context context, Intent intent) {
		final String HIGH_SCORES = "Readings";
		//Toast.makeText(context,, Toast.LENGTH_SHORT).show();
		System.out.println("in receier");
		SharedPreferences prefs = context.getSharedPreferences(HIGH_SCORES, context.MODE_PRIVATE);
		
		//prefs.edit().clear().commit();
				SharedPreferences.Editor editor = prefs.edit();
	    	 Sensorconcept s=new Sensorconcept();
	    	//Toast.makeText(context," in broadcast", Toast.LENGTH_SHORT).show();
	    	List<EditText> e;
	    	EditText e2;
	    	 //s.allEds.get(1)
	    		 int j=1;
	    		
	    		
	    		// editor.putInt("array_size",2);
	    		 Integer m=0;
	    		 Bundle bundle = intent.getExtras();
	    		 if(bundle!=null)
	    		 { Set<String> keys=bundle.keySet();
	    			 for(String s1:keys){
	    				a2[m]=s1;
	    				System.out.println("printing ids ^^^"+a2[m]);
	    		 array[m]=bundle.getInt(a2[m]);
	    		 System.out.println("printing readings ^^^"+array[m]);
	  	    		  	editor.putString(String.valueOf(m), a2[m]);
	  	    		   editor.putInt(a2[m], array[m]);
	  	    		 		
	    			//	 e2=s.sensor;
	  	    		 	//Toast.makeText(context,"value in broadcast:"+array[m], Toast.LENGTH_SHORT).show(); 		
	    				// e2.setText(array[m]);
	    				 m++;
	    				 j++;
	    				 			 
	   			 
	    		 }
	    			 editor.putInt("array_size",m);
	    			 editor.commit();
	 }
	    		 else
	    		    {
	    		    	Toast.makeText(context,"values r null broadcast", Toast.LENGTH_LONG).show();   		    	
	    		    }
	}
	}