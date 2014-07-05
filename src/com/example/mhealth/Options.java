package com.example.mhealth;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;

public class Options extends ActionBarActivity implements View.OnClickListener{

	Button b1;
	Button b2;
	Button b3;
	Button b4,b5;
	String password;
	String username;
	String url;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.option_layout);
		b1=(Button)findViewById(R.id.button2);
		b2=(Button)findViewById(R.id.button3);
		b3=(Button)findViewById(R.id.button4);
		b4=(Button)findViewById(R.id.button5);
		b5=(Button)findViewById(R.id.button6);
		Bundle extras = getIntent().getExtras();
		username= extras.getString("uname");
		password = extras.getString("pword");
		url = extras.getString("url");
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
		intent.putExtra("uname",username );
		intent.putExtra("pword",password);
		intent.putExtra("url", url);
		startActivity(intent);
		
	}
	
	public void buttonClick2()
	{
		Intent intent1 = new Intent(this, PostQuery.class);
		intent1.putExtra("uname",username );
		intent1.putExtra("pword",password);
		intent1.putExtra("url", url);
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
		intent1.putExtra("uname",username );
		intent1.putExtra("pword",password);
		intent1.putExtra("url", url);
		startActivity(intent1);
	}
	public void buttonClick5()
	{
		Intent intent1 = new Intent(this,Create_Activity.class);
		intent1.putExtra("uname",username );
		intent1.putExtra("pword",password);
		intent1.putExtra("url", url);
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