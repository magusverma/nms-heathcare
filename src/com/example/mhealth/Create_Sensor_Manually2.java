package com.example.mhealth;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
//import android.R;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class Create_Sensor_Manually2 extends ActionBarActivity implements View.OnClickListener{

	
	LinearLayout LLayout1;
	String username, password,url;
	TextView create_sensor_message,S1;
	EditText E1,E2;
	Button b1,b2,b3;
	JSONObject jo;
	List<EditText> list_of_concepts;
	Bundle bundle;
	ScrollView sv;
	EditText et;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sv=new ScrollView(this);
		LLayout1=new LinearLayout(this);
	    LLayout1.setOrientation(LinearLayout.VERTICAL);
	  	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	  	sv.addView(LLayout1);
	  	bundle = getIntent().getExtras(); 
	  	SharedPreferences sharedPref = getSharedPreferences("mhealth", Context.MODE_PRIVATE);
        username = sharedPref.getString(getString(R.string.username), "");
		password = sharedPref.getString(getString(R.string.password), "");
		url = sharedPref.getString(getString(R.string.url), "");
		create_sensor_message=new TextView(this);
		create_sensor_message.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));     
		create_sensor_message.setText("Creating Sensor "+bundle.getString("Sensor"));
	    LLayout1.addView(create_sensor_message);
		EditText ed;
		list_of_concepts = new ArrayList<EditText>();
		for(int i=0;i<bundle.getInt("Concept_Number");i++)
		 {
				ed = new EditText(Create_Sensor_Manually2.this);
			    list_of_concepts.add(ed);
			    ed.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
			    LLayout1.addView(ed);	
		 }
		  b2=new Button(Create_Sensor_Manually2.this);
	      b2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));   
	      b2.setText("Create Sensor "+bundle.getString("Sensor"));
	      LLayout1.addView(b2);
	      b2.setOnClickListener(this);   	 
	      setContentView(sv);

	}
	
	
	public static String Sensor_Create_Httppost(String username, String password , String url, String sensor_name ) throws ClientProtocolException, IOException, JSONException
	{
		JSONObject obj = new JSONObject();
		obj.put("sensor_id",1);
		obj.put("sensor_name", sensor_name);
		HttpClient httpClient = new DefaultHttpClient();
		ResponseHandler<String> responseHandler = new BasicResponseHandler();
		HttpPost postMethod = new HttpPost(url+"/ws/rest/v1/sensor/sm");    
		postMethod.setEntity(new StringEntity(obj.toString()));
		postMethod.setHeader( "Content-Type", "application/json");
		postMethod.addHeader(BasicScheme.authenticate(new UsernamePasswordCredentials(username,password),"UTF-8", false));
		String response = httpClient.execute(postMethod,responseHandler);
		return response;
	}
	
	public static String Sensor_Concept_Httppost(String username, String password , String uri, String str, String sensor) throws ClientProtocolException, IOException, JSONException
	{
		
		JSONObject obj = new JSONObject();
		obj.put("sensor", sensor);
		String [] arrayString = str.split("\\*");
		int size= arrayString.length;
		JSONArray ja = new JSONArray();
		for(int i =0 ; i<size;i++)
		{
			ja.put(arrayString[i]);
		}
		obj.put("concepts", ja);
		HttpClient httpClient = new DefaultHttpClient();
		ResponseHandler<String> responseHandler = new BasicResponseHandler();
		HttpPost postMethod = new HttpPost(uri+"/module/sensorreading/scm.form");    
		postMethod.setEntity(new StringEntity(obj.toString()));
		postMethod.setHeader( "Content-Type", "application/json");
		postMethod.addHeader(BasicScheme.authenticate(new UsernamePasswordCredentials(username,password),"UTF-8", false));
		String response = null;
		try{
			 response = httpClient.execute(postMethod,responseHandler);
		}
		catch(Exception e){
			System.out.println("caught exception");
			e.printStackTrace();
		}
		return response;
		
	}
	
	private class Sensor_Concept_AsyncTask extends AsyncTask<String, String, String>
	{
		String result;
		@Override
		protected String doInBackground(String... params) {
			
			
			try {
				result= Sensor_Concept_Httppost(params[0],params[1],params[2],params[3],params[4]);
			} catch (ClientProtocolException e) {
				System.out.println("Client Protocol exception caught.");
				e.printStackTrace();
			} catch (IOException e) {
				System.out.println("IO exception caught.");
				e.printStackTrace();
			} catch (JSONException e) {
				System.out.println("JSON exception caught.");
				e.printStackTrace();
			}
			
			
			
			
			return result;
		}
		
		 protected void onPostExecute(String params)
		 {
			 	Toast.makeText(getApplicationContext(),"Sensor Concept mapping successfully done!!!", Toast.LENGTH_LONG).show();
			 
		 }
		
		
	}
	
	private class Sensor_Create_AsyncTask extends AsyncTask<String, String, String>
	{
		String result;
		@Override
		protected String doInBackground(String... params) {
			
			try {
				return result=Sensor_Create_Httppost(params[0],params[1],params[2],params[3]);
			} catch (ClientProtocolException e) {
				System.out.println("Client Protocol exception caught.");
				e.printStackTrace();
			} catch (IOException e) {
				System.out.println("IO exception caught.");
				e.printStackTrace();
			} catch (JSONException e) {
				System.out.println("JSON exception caught.");
				e.printStackTrace();
			}
			return result;
			
		}
		
		 protected void onPostExecute(String params){
			 try {
					jo = new JSONObject(params);
					if(jo.get("sensor_name").equals(bundle.getString("Sensor")))
					{
						Toast.makeText(getApplicationContext(),"Sensor successfully created with ID"+jo.get("sensor_id"), Toast.LENGTH_LONG).show();
					
					}
			} 
			catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			String[] Concepts = new String[bundle.getInt("Concept_Number")];
		    String conceptID_concat = "" ;
		    for(int i=0; i < list_of_concepts.size(); i++)
		    {
		    	  Concepts[i] = list_of_concepts.get(i).getText().toString();
		    	  conceptID_concat=conceptID_concat+(Concepts[i]+"*");
			}

			 String to_send = conceptID_concat.substring(0,conceptID_concat.length()-1);
		     //Toast.makeText(getApplicationContext(),((String)jo.get("sensor_id")), Toast.LENGTH_LONG).show();
			String sensor_id;
			try {
				sensor_id = Integer.toString(jo.getInt("sensor_id"));
				new Sensor_Concept_AsyncTask().execute(username,password,url,to_send,sensor_id);
			} catch (JSONException e) {
				System.out.println("JSON Exception caught");
				e.printStackTrace();
			}
			
    	 
				
		 }
	}
		 
	@Override
	public void onClick(View v) {
		int flag = 0;
		for(int i=0; i < list_of_concepts.size(); i++){
	    	  if( list_of_concepts.get(i).getText().toString().length()<1)
	    	  {
	    		  flag =1;
	    	  }
	    	  
		}
		if (flag==1)
		{
			Toast.makeText(getApplicationContext(),"Enter all concepts", Toast.LENGTH_LONG).show();
		}
		else
		 new Sensor_Create_AsyncTask().execute(username,password,url,bundle.getString("Sensor"));
		
	}
	}
