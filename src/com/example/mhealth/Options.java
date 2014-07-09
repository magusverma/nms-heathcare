package com.example.mhealth;

import java.io.IOException;

import com.couchbase.lite.Manager;
import com.couchbase.lite.android.AndroidContext;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import android.os.Build;

public class Options extends ActionBarActivity implements View.OnClickListener{

	Button b1;
	Button b2;
	Button b3;
	Button b4,b5;
	Button b_post;
	String password;
	String username;
	String url;
	static couch_api ca; //magus
	
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
		/*Intent intent = new Intent(this, ShowDbData.class);
		intent.putExtra("db", ca.sensors_db_name);
		intent.putExtra("view", ca.sensor_view);
		startActivity(intent);*/
		System.out.println("kya ho gaya!");
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.option_layout);
		init_ca();
		b_post = (Button)findViewById(R.id.button7);
		b1=(Button)findViewById(R.id.button2);
		b2=(Button)findViewById(R.id.button3);
		b3=(Button)findViewById(R.id.button4);
		b4=(Button)findViewById(R.id.button5);
		b5=(Button)findViewById(R.id.button6);
		Bundle extras = getIntent().getExtras();
		
		b_post.setOnClickListener(new Button.OnClickListener() {  
			public void onClick(View v)
            {
//				ShowDbData.class
//				Intent i = new Intent(YourActivity.this, NewPlotActivity.class);
				Intent intent = new Intent(Options.this,SensorReading.class);
				intent.putExtra("db", ca.sensors_db_name);
				intent.putExtra("view", ca.sensor_view);
				System.out.println("kya ho gaya!");
				startActivity(intent);
				
            }
         });
//		username= extras.getString("uname");
//		password = extras.getString("pword");
//		url = extras.getString("url");
		SharedPreferences sharedPref = getSharedPreferences("mhealth", Context.MODE_PRIVATE);
        username = sharedPref.getString(getString(R.string.username), "");
		password = sharedPref.getString(getString(R.string.password), "");
		url = sharedPref.getString(getString(R.string.url), "");
        
		//Toast.makeText(this, username+password, Toast.LENGTH_LONG).show();
		b1.setOnClickListener(this);
		b2.setOnClickListener(this);
		b3.setOnClickListener(this);
		b4.setOnClickListener(this);
		b5.setOnClickListener(this);
		
		
	}
	
	public void buttonClick1()
	{
		//Toast.makeText(this, username+"   "+password, Toast.LENGTH_LONG).show();
		//Toast.makeText(this, "entered", Toast.LENGTH_LONG).show();
		Intent intent = new Intent(this, GetQuery.class);
//		intent.putExtra("uname",username );
//		intent.putExtra("pword",password);
//		intent.putExtra("url", url);
		startActivity(intent);
		
	}
	
	public void buttonClick2()
	{
		Intent intent1 = new Intent(this, PostQuery.class);
//		intent1.putExtra("uname",username );
//		intent1.putExtra("pword",password);
//		intent1.putExtra("url", url);
		startActivity(intent1);
	}
	public void buttonClick3()
	{
		Intent intent1 = new Intent(this, GetInput.class);
	//	intent1.putExtra("uname",username );
		//intent1.putExtra("pword",password);
		//intent1.putExtra("url", url);
		startActivity(intent1);
	}
	public void buttonClick4()
	{
		Intent intent1 = new Intent(this,Sensorconcept.class);
//		intent1.putExtra("uname",username );
//		intent1.putExtra("pword",password);
//		intent1.putExtra("url", url);
		startActivity(intent1);
	}
	public void buttonClick5()
	{
		Intent intent1 = new Intent(this,Create_Activity.class);
//		intent1.putExtra("uname",username );
//		intent1.putExtra("pword",password);
//		intent1.putExtra("url", url);
		startActivity(intent1);
	}
	
	/** Called when the user clicks the Sync button */
	public void startSyncActivity(View view) {
	    System.out.println("Clicked Sync Button");
		Intent intent1 = new Intent(this,SyncActivity.class);
		intent1.putExtra("uname",username );
//		intent1.putExtra("pword",password);
//		intent1.putExtra("url", url);
		startActivity(intent1);
	}
	
	/** Called when the user clicks the Settings button */
	public void SettingsActivity(View view) {
	    System.out.println("Clicked Sync Button");
		Intent intent1 = new Intent(this,Login.class);
//		intent1.putExtra("uname",username );
//		intent1.putExtra("pword",password);
//		intent1.putExtra("url", url);
		startActivity(intent1);
	}
	public void onClick(View v) {
		
			switch (v.getId())
			{
			case R.id.button2:
				//Toast.makeText(this, "get_selected", Toast.LENGTH_LONG).show();
					buttonClick1();
					break;
					
			case R.id.button3:
				//Toast.makeText(this, "post_selected", Toast.LENGTH_LONG).show();
				buttonClick2();
				break;
					
			case R.id.button4:
				buttonClick3();
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
