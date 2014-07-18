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
	LinearLayout LLayout1;
	String username, password,url;
	TextView Sensor,Concept_number;
	EditText Sensor_ID,Number_of_concepts;
	Button button;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LLayout1=new LinearLayout(this);
        LLayout1.setOrientation(LinearLayout.VERTICAL);
  	 	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	  	SharedPreferences sharedPref = getSharedPreferences("mhealth", Context.MODE_PRIVATE);
        username = sharedPref.getString(getString(R.string.username), "");
		password = sharedPref.getString(getString(R.string.password), "");
		url = sharedPref.getString(getString(R.string.url), "");
		Sensor=new TextView(this);
        Sensor.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));     
        Sensor.setText("Enter Sensor Name");
	    LLayout1.addView(Sensor);
	  	Sensor_ID =new EditText(this);
	  	Sensor_ID.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));     
	    LLayout1.addView(Sensor_ID); 
	    Concept_number=new TextView(Create_Sensor_Manually.this);
	    Concept_number.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));     
	    Concept_number.setText("Enter the number of concepts in sensor");
	    LLayout1.addView(Concept_number);
	  	Number_of_concepts =new EditText(Create_Sensor_Manually.this);
	  	Number_of_concepts.setInputType(InputType.TYPE_CLASS_NUMBER);
	  	Number_of_concepts.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));     
	    LLayout1.addView(Number_of_concepts);
	    button=new Button(Create_Sensor_Manually.this);
	    button.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));   
	    button.setText("Write Concepts ");
	    LLayout1.addView(button);
	    button.setOnClickListener(new View.OnClickListener() {
	    	    @Override
	    	    public void onClick(View v)
	    	    {
	    	    	if(Sensor_ID.getText().toString().length()<1||Number_of_concepts.getText().toString().length()<1)
	    	    	{
	    	    			Toast.makeText(Create_Sensor_Manually.this,"Enter Complete Details", Toast.LENGTH_LONG).show();
	    	    	}
	    	    	else{
			    	    	Intent intent = new Intent(Create_Sensor_Manually.this,Create_Sensor_Manually2.class);
							intent.putExtra("Sensor",Sensor_ID.getText().toString() );
							intent.putExtra("Concept_Number",Integer.parseInt(Number_of_concepts.getText().toString()));
							startActivity(intent);
	    	    	}
	    	    }
	      });
	     setContentView(LLayout1);
		}
	}