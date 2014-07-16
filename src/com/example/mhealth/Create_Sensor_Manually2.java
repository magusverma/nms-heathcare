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

	
	LinearLayout main1;
	String username, password,url;
	TextView S,S1;
	EditText E1,E2;
	Button b1,b2,b3;
	JSONObject jo;
	List<EditText> allEds;
	Bundle extras;
	ScrollView sv;
	EditText et;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sv=new ScrollView(this);
		main1=new LinearLayout(this);
	       main1.setOrientation(LinearLayout.VERTICAL);
	  	 setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	  	 sv.addView(main1);
	  	 extras = getIntent().getExtras(); 
	  	SharedPreferences sharedPref = getSharedPreferences("mhealth", Context.MODE_PRIVATE);
        username = sharedPref.getString(getString(R.string.username), "");
		password = sharedPref.getString(getString(R.string.password), "");
		url = sharedPref.getString(getString(R.string.url), "");
		S=new TextView(this);
	      S.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));     
	      S.setText("Creating Sensor "+extras.getString("Sensor"));
	      main1.addView(S);
		EditText ed;
		allEds = new ArrayList<EditText>();
		System.out.println(extras.getInt("CNumber"));
		for(int i=0;i<extras.getInt("CNumber");i++)
		 {
			// Toast.makeText(getApplicationContext(),i+"  ", Toast.LENGTH_LONG).show();
			 ed = new EditText(Create_Sensor_Manually2.this);
		    allEds.add(ed);
	     ed.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
		    main1.addView(ed);	
		 }
		  b2=new Button(Create_Sensor_Manually2.this);
	      b2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));   
	      b2.setText("Create Sensor "+extras.getString("Sensor"));
	      main1.addView(b2);
	      b2.setOnClickListener(this);   	 
	      setContentView(sv);

	}
	
	
	public static String Httppost(String username, String password , String url, String name ) throws ClientProtocolException, IOException, JSONException
	{
		JSONObject obj = new JSONObject();
		obj.put("sensor_id",1);
		obj.put("sensor_name", name);
		HttpClient httpClient = new DefaultHttpClient();
		ResponseHandler<String> resonseHandler = new BasicResponseHandler();
		HttpPost postMethod = new HttpPost(url+"/ws/rest/v1/sensor/sm");    
		postMethod.setEntity(new StringEntity(obj.toString()));
		postMethod.setHeader( "Content-Type", "application/json");
		//String authorizationString = "Basic " + Base64.encodeToString(("admin" + ":" + "Admin123").getBytes(), Base64.DEFAULT); //this line is diffe
		//authorizationString.replace("\n", "");
		//postMethod.setHeader("Authorization", authorizationString);
		postMethod.addHeader(BasicScheme.authenticate(new UsernamePasswordCredentials(username,password),"UTF-8", false));
		String response = httpClient.execute(postMethod,resonseHandler);
		return response;
	}
	
	public static String Httppost2(String username, String password , String uri, String str, String sens) throws ClientProtocolException, IOException, JSONException
	{
		
		JSONObject obj = new JSONObject();
		obj.put("sensor", sens);
		String [] arrayString = str.split("\\*");
		int siz= arrayString.length;
		JSONArray ja = new JSONArray();
		for(int i =0 ; i<siz;i++)
		{
			ja.put(arrayString[i]);
		}
		obj.put("concepts", ja);
		HttpClient httpClient = new DefaultHttpClient();
		ResponseHandler<String> resonseHandler = new BasicResponseHandler();
		HttpPost postMethod = new HttpPost(uri+"/module/sensorreading/scm.form");    
		postMethod.setEntity(new StringEntity(obj.toString()));
		postMethod.setHeader( "Content-Type", "application/json");
		//String authorizationString = "Basic " + Base64.encodeToString(("admin" + ":" + "Admin123").getBytes(), Base64.DEFAULT); //this line is diffe
		//authorizationString.replace("\n", "");
		//postMethod.setHeader("Authorization", authorizationString);
		postMethod.addHeader(BasicScheme.authenticate(new UsernamePasswordCredentials(username,password),"UTF-8", false));
		String response = null;
		try{
			 response = httpClient.execute(postMethod,resonseHandler);
		}catch(Exception e){
			System.out.println("caught exception");
			e.printStackTrace();
		}
		return response;
		
	}
	
	private class MyAsyncTask2 extends AsyncTask<String, String, String>
	{
		String res;
		@Override
		protected String doInBackground(String... params) {
			
			
			try {
				res= Httppost2(params[0],params[1],params[2],params[3],params[4]);
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
			
			
			
			
			return res;
		}
		
		 protected void onPostExecute(String params){
			 Toast.makeText(getApplicationContext(),"Sensor Concept mapping successfully done!!!", Toast.LENGTH_LONG).show();
			 
		 }
		
		
	}
	
	private class MyAsyncTask extends AsyncTask<String, String, String>
	{
		String res;
		@Override
		protected String doInBackground(String... params) {
			
			try {
				return res=Httppost(params[0],params[1],params[2],params[3]);
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
			return res;
			
		}
		
		 protected void onPostExecute(String params){
			 try {
				jo = new JSONObject(params);
				if(jo.get("sensor_name").equals(extras.getString("Sensor")))
				{
					Toast.makeText(getApplicationContext(),"Sensor successfully created with ID"+jo.get("sensor_id"), Toast.LENGTH_LONG).show();
				
				}
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			String[] strings = new String[extras.getInt("CNumber")];
		    String fin = "" ;
		    for(int i=0; i < allEds.size(); i++){
		    	  strings[i] = allEds.get(i).getText().toString();
		    	  fin=fin+(strings[i]+"*");
			}

			 String send = fin.substring(0,fin.length()-1);
		     //Toast.makeText(getApplicationContext(),((String)jo.get("sensor_id")), Toast.LENGTH_LONG).show();
			 String s;
			try {
				s = Integer.toString(jo.getInt("sensor_id"));
				new MyAsyncTask2().execute(username,password,url,send,s);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
    	 
				
		 }
	}
		 
	@Override
	public void onClick(View v) {
		int flag = 0;
		for(int i=0; i < allEds.size(); i++){
	    	  if( allEds.get(i).getText().toString().length()<1)
	    	  {
	    		  flag =1;
	    	  }
	    	  
		}
		if (flag==1)
		{
			Toast.makeText(getApplicationContext(),"Enter all concepts", Toast.LENGTH_LONG).show();
		}
		else
		 new MyAsyncTask().execute(username,password,url,extras.getString("Sensor"));
		
	}
	}
