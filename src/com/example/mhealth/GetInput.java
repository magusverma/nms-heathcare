package com.example.mhealth;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.view.View.OnClickListener;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.widget.Button;
import android.widget.EditText;
import android.view.View;
import android.content.Intent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class GetInput extends Activity implements OnClickListener{
	Button b1;
	TextView t1;
	TextView t2;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.get_inputlayout);
		b1=(Button)findViewById(R.id.button1);
		t1=(TextView)findViewById(R.id.textView1);
		t2=(TextView)findViewById(R.id.textView2);
		b1.setOnClickListener(this);
		int mInt=2,mInt2=1;
		
		try{
		Bundle b = getIntent().getExtras();
		mInt= b.getInt("Pulse");
		mInt2=b.getInt("Oxygen");
		//Toast.makeText(getApplicationContext(),"mInt Value is "+mInt, Toast.LENGTH_LONG).show();
		t1.setText("Pulse Reading is"+mInt);
	
		t2.setText("Oxygen Reading is"+mInt2);
				

		
		
		
		
		
		}catch(Exception e)
		{}
		
	}
	
	public void onClick(View v)
	
	{  switch (v.getId())
		{
		case R.id.button1:
		
		Intent i1 = new Intent();

    Intent i=null;
     PackageManager manager = getPackageManager();
     try {
         i = manager.getLaunchIntentForPackage("org.opendatakit.sensors.drivers.xpodpulseox");
         if (i == null)
             throw new PackageManager.NameNotFoundException();
         i.addCategory(Intent.CATEGORY_LAUNCHER);
      //   startActivityForResult(i,0);
     } catch (PackageManager.NameNotFoundException e) {

     }

    startActivityForResult(i,0);
	
		
		}
}
	@Override  
    public boolean onCreateOptionsMenu(Menu menu) {  
        // Inflate the menu; this adds items to the action bar if it is present.  
        getMenuInflater().inflate(R.menu.main, menu);  
        return true;  
    }  

	
	

	
	
	
	
	
}