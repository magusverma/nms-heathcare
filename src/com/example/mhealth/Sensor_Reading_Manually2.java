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
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;


public class Sensor_Reading_Manually2 extends ActionBarActivity {

	EditText sensor,patient;
	Button b1,b2;
	String query;
	LinearLayout LLayout1;
	String username;
	String password;
	String url;
	TextView enter_patient_id,message;
	Bundle extras;
	HashMap<String, String> scm_name_to_id;
	String[] sensor_name_hinting_array;
	ScrollView sv;
	static couch_api ca; //magus
	
	
		
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ScrollView sv = new ScrollView(this);
		LLayout1=new LinearLayout(this);
		LLayout1.setOrientation(LinearLayout.VERTICAL);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		sv.addView(LLayout1);
		extras = getIntent().getExtras(); 
		SharedPreferences sharedPref = getSharedPreferences("mhealth", Context.MODE_PRIVATE);
		username = sharedPref.getString(getString(R.string.username), "");
		password = sharedPref.getString(getString(R.string.password), "");
		url = sharedPref.getString(getString(R.string.url), "");
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
     
		new Sensor_name_AsyncTask().execute(username,password,url);
		enter_patient_id=new TextView(this);
     	enter_patient_id.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));     
     	enter_patient_id.setText("Enter Patient Id");
     	LLayout1.addView(enter_patient_id);
     	patient =new EditText(this);
     	patient.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));     
     	LLayout1.addView(patient);
     	setContentView(sv);
     	new sensor_concept_mapping_AsyncTask().execute(username,password,url,extras.getString("Sensor"));
		
	
}
	
	public String get_sensor_concept_mapping() throws ClientProtocolException, IOException{
		HttpClient httpClient = new DefaultHttpClient();
		ResponseHandler<String> responseHandler = new BasicResponseHandler();
		System.out.println(url+"/ws/rest/v1/sensor/scm"+" "+username+password);
		HttpGet getMethod = new HttpGet(this.url+"/ws/rest/v1/sensor/scm");    
		getMethod.setHeader( "Content-Type", "application/json");
		getMethod.addHeader(BasicScheme.authenticate(new UsernamePasswordCredentials(username,password),"UTF-8", false));
		String response = httpClient.execute(getMethod,responseHandler);
		return response;
	}
	
	
		public void process_sensor_hinting() throws ClientProtocolException, IOException{
			JSONObject query = null;
			try {
				query = new JSONObject(get_sensor_concept_mapping());
			} catch (JSONException e1) {
				
				e1.printStackTrace();
			}
			JSONArray results = null;
			try {
				results = query.getJSONArray("results");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			// Iterates over SCMs JSON Array
			for (int it = 0 ; it < results.length(); it++) {
				JSONObject current_sensor = null;
				try {
					current_sensor = (JSONObject) ((JSONObject)results.get(it)).get("sensor");
					scm_name_to_id.put(current_sensor.get("sensor_name").toString(), current_sensor.get("sensor_id").toString());
				} catch (JSONException e) {
					e.printStackTrace();
				}
	        }
			
			Set<String> hinting_set = scm_name_to_id.keySet();
			sensor_name_hinting_array = hinting_set.toArray(new String[hinting_set.size()]);
		}
	    
		public static String SensorName_Httpget (String username, String password, String url,String query) throws ClientProtocolException, IOException
		{
			HttpClient httpClient = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(url+"/ws/rest/v1/sensor/sm/"+query);
			httpGet.addHeader(BasicScheme.authenticate(new UsernamePasswordCredentials(username, password),"UTF-8", false));
			HttpResponse httpResponse = httpClient.execute(httpGet);
			HttpEntity responseEntity = httpResponse.getEntity();
			String str = inputStreamToString(responseEntity.getContent()).toString();
			httpClient.getConnectionManager().shutdown();
			return str;  
			       
	        
	    	}
		
		public static String SensorName_JsonParse(String str) throws JSONException
		{
			JSONObject jo = new JSONObject(str);
			return jo.getString("sensor_name");
			
		}
		
		private class Sensor_name_AsyncTask extends AsyncTask<String, String, String> 
		{
			String result="";
			@Override
			protected String doInBackground(String... params) {
				
					try {
						result = SensorName_JsonParse(SensorName_Httpget(params[0], params[1], params[2], extras.getString("Sensor")));
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
				 
				 message=new TextView(Sensor_Reading_Manually2.this);
			     message.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));     
			     message.setText("Readings for sensor "+params);
			     LLayout1.addView(message);
				 
			 }
			
			
		}
	public static StringBuilder inputStreamToString (InputStream is) {
		String line = "";
		StringBuilder total = new StringBuilder();
		// Wrap a BufferedReader around the InputStream
		BufferedReader rd = new BufferedReader(new InputStreamReader(is));
		// Read response until the end
		try {
			while ((line = rd.readLine()) != null) { 
				total.append(line); 
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		// Return full string
		return total;
		}
	
	
	public static String sensor_concept_Httpget (String username, String password, String uri,String query) throws ClientProtocolException, IOException
	{
		System.out.println("Looking up for Sensors Online");
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(uri+"/ws/rest/v1/sensor/scm/"+query);
		httpGet.addHeader(BasicScheme.authenticate(new UsernamePasswordCredentials(username, password),"UTF-8", false));
		HttpResponse httpResponse = httpClient.execute(httpGet);
		HttpEntity responseEntity = httpResponse.getEntity();
		String str = inputStreamToString(responseEntity.getContent()).toString();
		httpClient.getConnectionManager().shutdown();
		return str;
	} 
	
	public static String get_sensor_concepts_from_db(String id){
		String str="";
		Document retrievedDocument = ca.getSensor(id);
		if(retrievedDocument != null){
			System.out.println("Using Sensor Found in Database");
			HashMap<String,Object> sensor_concepts = (HashMap<String, Object>) ((Map) retrievedDocument.getProperty("sensor_concepts"));
			for (String key: sensor_concepts.keySet()){
				System.out.println(key);
				str = str + key + "*"; 
			}
		}
		else{
			System.out.println("No Such Sensor Found with ID="+id);
		}
		return str;
	}
	

	public static String Readings_Httppost(String username, String password ,String uri, String id_string, String reading_string, String sens, String pat) throws ClientProtocolException, IOException, JSONException
	{
		JSONObject obj = new JSONObject();
		
		obj.put("sensor", sens);
		obj.put("patient", pat);
		JSONObject id_reading = new JSONObject();
		String id_array[]=id_string.split("\\*");
		String reading_array[] = reading_string.split("\\*");
		int size=id_array.length;
		for(int i=0;i<size;i++)
		{
			id_reading.put(id_array[i], reading_array[i]);
		}
		obj.put("readings", id_reading);
		System.out.println(obj);
		//
			System.out.println("Running CouchBase Code");
			HashMap<String,Object> readings = new HashMap<String,Object>();
			for(int i=0;i<size;i++)
			{
				readings.put(id_array[i], reading_array[i]);
			}
			ca.createDocument("reading", ca.makeReadingMap(pat, sens, readings));
			ca.getAllDocument("reading");
		//
		HttpClient httpClient = new DefaultHttpClient();
		ResponseHandler<String> resonseHandler = new BasicResponseHandler();
		HttpPost postMethod = new HttpPost(uri+"/module/sensorreading/sr.form");    
		postMethod.setEntity(new StringEntity(obj.toString()));
		postMethod.setHeader( "Content-Type", "application/json");
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
	
	public static String JsonParse(String str) throws JSONException
	{ 
		String res="";
		JSONObject jo = new JSONObject(str);
		JSONArray ja = jo.getJSONArray("concepts");
		if (ja.length()==0)
				{
			
			return "zero";
				}
		int len = ja.length();
		System.out.println(len);
		for(int i=0;i<len;i++)
		{
			JSONObject jo1 = (JSONObject) ja.get(i);
			res= res + (String)jo1.get("display") +"*";
			System.out.println(res);

		}
		
		return res;
	}
	
	public static String JsonParse2(String str) throws JSONException
	{ 
		String res="";
		JSONObject jo = new JSONObject(str);
		JSONArray ja = jo.getJSONArray("concepts");
		if (ja.length()==0)
				{
			
			return "zero";
				}
		int len = ja.length();
		System.out.println(len);
		for(int i=0;i<len;i++)
		{
			JSONObject jo1 = (JSONObject) ja.get(i);
			res= res + (String)jo1.get("uuid") +"*";
			System.out.println(res);

		}
		
		return res;
	}
	
	
	
	
	private class Reading_Post_AsyncTask extends AsyncTask<String, String, String> //Marker
	{
		String result="";
		@Override
		protected String doInBackground(String... params) {
			
				ca.push_readings(username, password, url);

			return result;
			
		}
		
		 protected void onPostExecute(String params){
			 try {
				ca.getDatabase(ca.readings_db_name).getView(ca.pending_reading_view).createQuery().run();
			} catch (CouchbaseLiteException e) {
				System.out.println("Couch Base Exception");
				e.printStackTrace();
			} 
			 Integer rows_left = ca.getDatabase(ca.readings_db_name).getView(ca.pending_reading_view).dump().size();
			 System.out.println("Done");
			 System.out.println("Rows Left = "+rows_left);
			 if (rows_left.equals(0)){
				 Toast.makeText(getApplicationContext(),"Successfully Pushed", Toast.LENGTH_LONG).show();
			 }
			 else{
				 System.out.println("queue not empty yet");
			 }
		 }
		
		
	}

	private  class sensor_concept_mapping_AsyncTask extends AsyncTask<String, String, String>{
		
		String result1,result2;
		  
		  protected String doInBackground(String... params) {
			   result2 = get_sensor_concepts_from_db(params[3]); // params[3] has sensor_id
			   if(result2.length()==0){
				   try {
					   result1 = (sensor_concept_Httpget(params[0],params[1], params[2],params[3]));
					   result2=JsonParse(result1);
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
			   }
			   if(result2.equals("")){
				   return "zero";
			   }
			   return result2;
		  }
		  
		  
		  protected void onPostExecute(String params){
			  
			  String arrayString[];
			  EditText ed;
			final List<EditText> readings = new ArrayList<EditText>();
			 if(params.equals("zero"))
			  {
				  Toast.makeText(getApplicationContext(),"No Concepts found.", Toast.LENGTH_LONG).show();
			  }
			  
			  else{
				
					 arrayString= params.split("\\*");
				     final int size=arrayString.length;
			     for(int i=0;i<size;i++)
						 {
							 TextView rowTextView = new TextView(Sensor_Reading_Manually2.this);
							 rowTextView.setText(arrayString[i]);
							 LLayout1.addView(rowTextView);
						    ed = new EditText(Sensor_Reading_Manually2.this);
						    readings.add(ed);
						    ed.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
						    LLayout1.addView(ed);	 
							
							 
						 }
						 
				
				 b2=new Button(Sensor_Reading_Manually2.this);
			     b2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));   
			     b2.setText("Post Readings");
			     LLayout1.addView(b2);
			     b2.setOnClickListener(new View.OnClickListener() {
			    	    @Override
			    	    public void onClick(View v) {
			    	    	if(patient.getText().toString().length()<1)
			    	    	{
			    	    		Toast.makeText(getApplicationContext(), "Enter Complete Details", Toast.LENGTH_LONG).show();
			    	    	}
			    	    	else{
				    	    	 String[] read = new String[size];
				    	    	 String readings_concat = "" ;
				    	    	 for(int i=0; i < readings.size(); i++)
				    	    	 {
				    	    	     read[i] = readings.get(i).getText().toString();
				    	    	     readings_concat=readings_concat+read[i]+"*";
				    	    	 }
			    	        final String send = readings_concat.substring(0,readings_concat.length()-1);
			    	    	query=extras.getString("Sensor");
			    	    	que(query,send);
			    	   	 	Toast.makeText(getApplicationContext(),"Queued Successfully!", Toast.LENGTH_LONG).show();
			 			 	 new Reading_Post_AsyncTask().execute(username,password,url,query,send);
			    	    	}
			    	    	
			    	    				}
			     	}); 
	
			 
		  }
		  }	  
		  
		  protected void onProgressUpdate(Integer... progress){
			    
		  }
		  }
		 
	
	public void que(String sensor_id,String readings){
		String res;
		Document retrievedDocument = ca.getSensor(sensor_id);
		if(retrievedDocument != null){
			System.out.println("Using Sensor Found in Database");
		}
		else{
			System.out.println("Looking up Sensors online");
				retrievedDocument = ca.getSensor(sensor_id);
		}
		if(retrievedDocument != null){
			HashMap<String,Object> sensor_concepts = (HashMap<String, Object>) ((Map) retrievedDocument.getProperty("sensor_concepts"));
			res = new String("");
			for (String key: sensor_concepts.keySet()){
				System.out.println(key);
				res = res + sensor_concepts.get(key) + "*"; 
			}
			
			String id_array[] = res.split("\\*");
			String reading_array[] = readings.split("\\*");
			//
				System.out.println("Running CouchBase Code");
				HashMap<String,Object> reading = new HashMap<String,Object>();
				for(int i=0;i<id_array.length;i++)
				{
					reading.put(id_array[i], reading_array[i]);
				}
				String pat = patient.getText().toString();
				String sens = sensor.getText().toString();
				ca.createDocument("reading", ca.makeReadingMap(pat, sens, reading));
			//
		}
	}
		
}
