package com.example.mhealth;

import java.util.List;
import java.util.Set;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
@SuppressLint("NewApi")
public class MyBroadcastReceiver2 extends BroadcastReceiver{
	 Integer array[]=new Integer[5];
		String a2[]=new String[5];
		int n1=3;
		@Override
		
	public void onReceive(Context context, Intent intent) {
		final String NEWAPP = "newApp";
		SharedPreferences prefs = context.getSharedPreferences(NEWAPP, context.MODE_PRIVATE);
				SharedPreferences.Editor editor = prefs.edit();
	    	int m=0;
	    	Toast.makeText(context, "in cast 2", Toast.LENGTH_SHORT);
	    		 Bundle bundle = intent.getExtras();
	    		 if(bundle!=null)
	    		 { Set<String> keys=bundle.keySet();
	    			 for(String s1:keys){
	    				a2[m]=s1;
	    		 array[m]=bundle.getInt(a2[m]);

	  	    		  
	  	    		   editor.putInt("Value"+m, array[m]);
	  	    		 	editor.commit();	
	    			//	 e2=s.sensor;
	  	    		 //	Toast.makeText(context,"value in broadcast:"+array[m], Toast.LENGTH_SHORT).show(); 		
	    				// e2.setText(array[m]);
	    				 m++;
	    				 				 
	   			 
	    		 }
	    
	 }
	    		 else
	    		    {
	    		    	Toast.makeText(context,"values r null broadcast", Toast.LENGTH_LONG).show();   		    	
	    		    }
	}
	
	
	
	
	
}
