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
	Button b1,b2;;
	String query;
	LinearLayout main1;
	String username;
	String password;
	String url;
	TextView S, S1;
	Bundle extras;
	HashMap<String, String> scm_name_to_id;
	String[] sensor_name_hinting_array;
	ScrollView sv;
	static couch_api ca; //magus
	
	
		
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ScrollView sv = new ScrollView(this);
		main1=new LinearLayout(this);
       main1.setOrientation(LinearLayout.VERTICAL);
  	 setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
  	sv.addView(main1);
  	 extras = getIntent().getExtras(); 
  	SharedPreferences sharedPref = getSharedPreferences("mhealth", Context.MODE_PRIVATE);
    username = sharedPref.getString(getString(R.string.username), "");
	password = sharedPref.getString(getString(R.string.password), "");
	url = sharedPref.getString(getString(R.string.url), "");
	 //Magus
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
     
	 new MyAsyncTask3().execute(username,password,url);
  	 S1=new TextView(this);
      S1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));     
      S1.setText("Enter Patient Id");
      main1.addView(S1);
  	 patient =new EditText(this);
  	 patient.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));     
      main1.addView(patient);
       setContentView(sv);
       new MyAsyncTask().execute(username,password,url,extras.getString("Sensor"));
		
	
}
	
	public String get_scm() throws ClientProtocolException, IOException{
		HttpClient httpClient = new DefaultHttpClient();
		ResponseHandler<String> resonseHandler = new BasicResponseHandler();
		System.out.println(url+"/ws/rest/v1/sensor/scm"+" "+username+password);
		HttpGet getMethod = new HttpGet(this.url+"/ws/rest/v1/sensor/scm");    
		getMethod.setHeader( "Content-Type", "application/json");
		getMethod.addHeader(BasicScheme.authenticate(new UsernamePasswordCredentials(username,password),"UTF-8", false));
		String response = httpClient.execute(getMethod,resonseHandler);
		return response;
	}
	
	// By Magus
		public void process_sensor_hinting() throws ClientProtocolException, IOException{
			JSONObject query = null;
			try {
				query = new JSONObject(get_scm());
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			JSONArray results = null;
			try {
				results = query.getJSONArray("results");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
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

			String res;
			JSONObject jo = new JSONObject(str);
			return jo.getString("sensor_name");
			
		}
		
		private class MyAsyncTask3 extends AsyncTask<String, String, String> 
		{
			String res1,res2,res3="";
			@Override
			protected String doInBackground(String... params) {
				
					try {
						res3 = SensorName_JsonParse(SensorName_Httpget(params[0], params[1], params[2], extras.getString("Sensor")));
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
					return res3;
				
			}
			
			 protected void onPostExecute(String params){
				 
				 S=new TextView(Sensor_Reading_Manually2.this);
			      S.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));     
			      S.setText("Readings for sensor "+params);
			      main1.addView(S);
				 
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
	
	
	public static String Httpget (String username, String password, String uri,String query) throws ClientProtocolException, IOException
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
	

	public static String Httppost(String username, String password ,String uri, String uu, String dat, String sens, String pat) throws ClientProtocolException, IOException, JSONException
	{
		JSONObject obj = new JSONObject();
		
		obj.put("sensor", sens);
		obj.put("patient", pat);
		JSONObject nestedObject = new JSONObject();
		String uua[]=uu.split("\\*");
		String daa[] = dat.split("\\*");
		int siz=uua.length;
		for(int i=0;i<siz;i++)
		{
			nestedObject.put(uua[i], daa[i]);
		}
		obj.put("readings", nestedObject);
		System.out.println(obj);
		//
			System.out.println("Running CouchBase Code");
			HashMap<String,Object> readings = new HashMap<String,Object>();
			for(int i=0;i<siz;i++)
			{
				readings.put(uua[i], daa[i]);
			}
			ca.createDocument("reading", ca.makeReadingMap(pat, sens, readings));
			ca.getAllDocument("reading");
		//
		HttpClient httpClient = new DefaultHttpClient();
		ResponseHandler<String> resonseHandler = new BasicResponseHandler();
		HttpPost postMethod = new HttpPost(uri+"/module/sensorreading/sr.form");    
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
	
	
	
	
	private class MyAsyncTask2 extends AsyncTask<String, String, String> //Marker
	{
		String res1,res2,res3="";
		@Override
		protected String doInBackground(String... params) {
			
				ca.push_readings(username, password, url);

			return res3;
			
		}
		
		 protected void onPostExecute(String params){
			 try {
				ca.getDatabase(ca.readings_db_name).getView(ca.pending_reading_view).createQuery().run();
			} catch (CouchbaseLiteException e) {
				e.printStackTrace();
			} //refresh view
			 Integer rows_left = ca.getDatabase(ca.readings_db_name).getView(ca.pending_reading_view).dump().size();
			 System.out.println("Done");
			 System.out.println("Rows Left = "+rows_left);
			 if (rows_left.equals(0)){
				 Toast.makeText(getApplicationContext(),"Successfully Pushed", Toast.LENGTH_LONG).show();
			 }
			 else{
				 System.out.println("que not empty yet");
			 }
		 }
		
		
	}

	private  class MyAsyncTask extends AsyncTask<String, String, String>{
		
		String res1,res2;
		  
		  protected String doInBackground(String... params) {
			   res2 = get_sensor_concepts_from_db(params[3]); // params[3] has sensor_id
			   if(res2.length()==0){
				   try {
					   res1 = (Httpget(params[0],params[1], params[2],params[3]));
					   res2=JsonParse(res1);
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
			   if(res2.equals("")){
				   return "zero";
			   }
			   return res2;
		  }
		  
		  
		  protected void onPostExecute(String params){
			  
			  String arrayString[];
			  //Toast.makeText(getApplicationContext(),params, Toast.LENGTH_LONG).show();
			 // Toast.makeText(getApplicationContext(),"PostExecute.", Toast.LENGTH_LONG).show();
			  EditText ed;
			final List<EditText> allEds = new ArrayList<EditText>();
			  //Toast.makeText(getApplicationContext(),"PostExecute2.", Toast.LENGTH_LONG).show();
			
			  if(params.equals("zero"))
			  {
				  Toast.makeText(getApplicationContext(),"No Concepts found.", Toast.LENGTH_LONG).show();
			  }
			  
			  else{
				
				 System.out.println("array string length");
	      arrayString= params.split("\\*");
	     
	     final int size=arrayString.length;
         // Toast.makeText(getApplicationContext(),size +"  ", Toast.LENGTH_LONG).show();
          System.out.println("array string length"+size);

	 
	 
	 for(int i=0;i<size;i++)
	 {
		// Toast.makeText(getApplicationContext(),i+"  ", Toast.LENGTH_LONG).show();
		 TextView rowTextView = new TextView(Sensor_Reading_Manually2.this);
		 rowTextView.setText(arrayString[i]);
		 main1.addView(rowTextView);
	    ed = new EditText(Sensor_Reading_Manually2.this);
	    allEds.add(ed);
     ed.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
	    main1.addView(ed);	 
		
		 
	 }
	 
	
	 b2=new Button(Sensor_Reading_Manually2.this);
     b2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));   
     b2.setText("Post Readings");
     main1.addView(b2);
     b2.setOnClickListener(new View.OnClickListener() {
    	    @Override
    	    public void onClick(View v) {
    	    	if(patient.getText().toString().length()<1)
    	    	{
    	    		Toast.makeText(getApplicationContext(), "Enter Complete Details", Toast.LENGTH_LONG).show();
    	    	}
    	    	else{
    	    	 String[] strings = new String[size];
    	    	 String fin = "" ;
    	    	 for(int i=0; i < allEds.size(); i++){
    	    	     strings[i] = allEds.get(i).getText().toString();
    	    	     fin=fin+strings[i]+"*";
    	    	 }
    	    	 final String send = fin.substring(0,fin.length()-1);
    	    	 
    	    	query=extras.getString("Sensor");
    	    	que(query,send);
    	   	 Toast.makeText(getApplicationContext(),"Queued Successfully!", Toast.LENGTH_LONG).show();
 			
    	    	 new MyAsyncTask2().execute(username,password,url,query,send);
    	    }}
    	});
	 
		  
	
			 
		  }
		  }	  
		  
		  protected void onProgressUpdate(Integer... progress){
			    
		  }
		  }
		 
	
	public void que(String params3,String params4){
		String res2;
		Document retrievedDocument = ca.getSensor(params3);
		if(retrievedDocument != null){
			System.out.println("Using Sensor Found in Database");
		}
		else{
			System.out.println("Looking up Sensors online");
			//TODO
//			new SyncActivity().syncSensors(params[0],params[1], params[2]);
			retrievedDocument = ca.getSensor(params3);
		}
		if(retrievedDocument != null){
			HashMap<String,Object> sensor_concepts = (HashMap<String, Object>) ((Map) retrievedDocument.getProperty("sensor_concepts"));
			res2 = new String("");
			for (String key: sensor_concepts.keySet()){
				System.out.println(key);
				res2 = res2 + sensor_concepts.get(key) + "*"; 
			}
			//2=uu, 3=dat
			String uua[] = res2.split("\\*");
			String daa[] = params4.split("\\*");
			//
				System.out.println("Running CouchBase Code");
				HashMap<String,Object> readings = new HashMap<String,Object>();
				for(int i=0;i<uua.length;i++)
				{
					readings.put(uua[i], daa[i]);
				}
				String pat = patient.getText().toString();
				String sens = sensor.getText().toString();
				ca.createDocument("reading", ca.makeReadingMap(pat, sens, readings));
			//
		}
	}
		
}
