package com.example.mhealth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Document;
import com.couchbase.lite.Manager;
import com.couchbase.lite.android.AndroidContext;

import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


public class Sensor_Reading_Manually extends ActionBarActivity implements View.OnClickListener {

	EditText sensor,patient;
	Button b1,b2;;
	String query;
	LinearLayout main1;
	String username;
	String password;
	String url;
	TextView S, S1;
	HashMap<String, String> scm_name_to_id;
	String[] sensor_name_hinting_array;
	static couch_api ca; //magus
	
	
	
		
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
		main1=new LinearLayout(this);
       main1.setOrientation(LinearLayout.VERTICAL);
  	 setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
  	 Bundle extras = getIntent().getExtras(); 
  	SharedPreferences sharedPref = getSharedPreferences("mhealth", Context.MODE_PRIVATE);
    username = sharedPref.getString(getString(R.string.username), "");
	password = sharedPref.getString(getString(R.string.password), "");
	url = sharedPref.getString(getString(R.string.url), "");
	  S=new TextView(this);
      S.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));     
      S.setText("Enter sensor Id");
      main1.addView(S);
  	 sensor =new EditText(this);
  	 sensor.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));     
      main1.addView(sensor);
      b1=new Button(this);
      b1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));   
      b1.setText("Get Concepts");
      b1.setOnClickListener(this);
      main1.addView(b1);
      setContentView(main1);
		
	
}
	@Override
	public void onClick(View v) {
		
		if(sensor.getText().toString().length()<1)
		{
			Toast.makeText(getApplicationContext(), "Enter Complete Details", Toast.LENGTH_LONG).show();
		}
		else{
		Intent intent = new Intent(this, Sensor_Reading_Manually2.class);
		intent.putExtra("Sensor", sensor.getText().toString());
		startActivity(intent);
		}
		
	}		
}
