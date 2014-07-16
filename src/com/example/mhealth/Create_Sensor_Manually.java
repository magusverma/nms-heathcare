package com.example.mhealth;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
//import android.R;
import android.support.v7.app.ActionBarActivity;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Create_Sensor_Manually extends ActionBarActivity  {

	
	LinearLayout main1;
	String username, password,url;
	TextView S,S1;
	EditText E1,E2;
	Button b1,b2,b3;
	

	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		main1=new LinearLayout(this);
	       main1.setOrientation(LinearLayout.VERTICAL);
	  	 setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	  	SharedPreferences sharedPref = getSharedPreferences("mhealth", Context.MODE_PRIVATE);
        username = sharedPref.getString(getString(R.string.username), "");
		password = sharedPref.getString(getString(R.string.password), "");
		url = sharedPref.getString(getString(R.string.url), "");
		S=new TextView(this);
	      S.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));     
	      S.setText("Enter Sensor Name");
	      main1.addView(S);
	  	 E1 =new EditText(this);
	  	 E1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));     
	      main1.addView(E1); 
	      S1=new TextView(Create_Sensor_Manually.this);
	      S1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));     
	      S1.setText("Enter the number of concepts in sensor");
	      main1.addView(S1);
	  	 E2 =new EditText(Create_Sensor_Manually.this);
	  	 E2.setInputType(InputType.TYPE_CLASS_NUMBER);
	  	 E2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));     
	      main1.addView(E2);
	      b2=new Button(Create_Sensor_Manually.this);
	      b2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));   
	      b2.setText("Write Concepts ");
	      main1.addView(b2);
	      b2.setOnClickListener(new View.OnClickListener() {
	    	    @Override
	    	    public void onClick(View v) {
	    	    	if(E1.getText().toString().length()<1||E2.getText().toString().length()<1)
	    	    	{
	    	    		Toast.makeText(Create_Sensor_Manually.this,"Enter Complete Details", Toast.LENGTH_LONG).show();
	    	    	}
	    	    	else{
	    	    	Intent intent = new Intent(Create_Sensor_Manually.this,Create_Sensor_Manually2.class);
					intent.putExtra("Sensor",E1.getText().toString() );
					intent.putExtra("CNumber",Integer.parseInt(E2.getText().toString()));
					//System.out.println("kya ho gaya!");
					startActivity(intent);
	    	    	}
	    	    }
	      });
	    				
	    
	      
	      
	      setContentView(main1);

	
	
	
	
		}




	}

