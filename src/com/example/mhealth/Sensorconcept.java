package com.example.mhealth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Iterator;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.widget.Toast;
import android.content.*;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.text.TextWatcher;
import android.os.Bundle;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import android.content.SharedPreferences.Editor;
import android.text.Editable;
public class Sensorconcept extends ActionBarActivity implements View.OnClickListener {

	AutoCompleteTextView  sensor;
	EditText patient;
	Button b1,b2;;
	Button b3,b4;
	String query;
	LinearLayout main1;
	String username;
	String password;
	String url;
	TextView S, S1;
	static HashMap<String, String> scm_name_to_id;
	String[] sensor_name_hinting_array;
	 List<EditText> allEds;
	 SharedPreferences prefs;
	   ArrayAdapter<String> sensor_adapter;
	   
	  /*
	public String get_scm() throws ClientProtocolException, IOException{
		HttpClient httpClient = new DefaultHttpClient();
		ResponseHandler<String> resonseHandler = new BasicResponseHandler();
	//magus	System.out.println(url+"/ws/rest/v1/sensor/scm"+" "+username+password);
		HttpGet getMethod = new HttpGet(this.url+"/ws/rest/v1/sensor/sm");    
		getMethod.setHeader( "Content-Type", "application/json");
		getMethod.addHeader(BasicScheme.authenticate(new UsernamePasswordCredentials(username,password),"UTF-8", false));
		String response = httpClient.execute(getMethod,resonseHandler);
		Toast.makeText(getApplicationContext(),"get scm "+response, Toast.LENGTH_LONG).show();
		return response;
	}
	
	// By Magus
		/*public void process_sensor_hinting() throws ClientProtocolException, IOException{
			
			
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
				Toast.makeText(getApplicationContext(),"result length"+results.length(), Toast.LENGTH_LONG).show();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Toast.makeText(getApplicationContext(),"Exceptn 2", Toast.LENGTH_LONG).show();
			}
			
			// Iterates over SCMs JSON Array
			for (int it = 0 ; it < results.length(); it++) {
				JSONObject current_sensor = null;
				try {
					current_sensor = (JSONObject) ((JSONObject)results.get(it)).get("sensor");
					scm_name_to_id.put(current_sensor.get("sensor_name").toString(), current_sensor.get("sensor_id").toString());
					Toast.makeText(getApplicationContext(),"herer loop", Toast.LENGTH_SHORT).show();
				} catch (JSONException e) {
					e.printStackTrace();
				}
	        }
			
			Set<String> hinting_set = scm_name_to_id.keySet();
			sensor_name_hinting_array = hinting_set.toArray(new String[hinting_set.size()]);
			Toast.makeText(getApplicationContext(),"herer outloop", Toast.LENGTH_SHORT).show();
	     try {
				process_sensor_hinting();
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    
		   //    Toast(getApplicationContext(),"here"+sensor_name_hinting_array,Toast.LENGTH_SHORT).show();
	     for(String e:sensor_name_hinting_array)
			{    Toast.makeText(getApplicationContext(),"herer"+e, Toast.LENGTH_LONG).show();
				
			}
		    sensor_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, sensor_name_hinting_array );
		     ((AutoCompleteTextView) sensor).setAdapter(sensor_adapter);
}
*/
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final String HIGH_SCORES = "HighScores";
		 prefs =getSharedPreferences(HIGH_SCORES,getApplicationContext().MODE_PRIVATE); // 0 - for private mode
		main1=new LinearLayout(this);
       main1.setOrientation(LinearLayout.VERTICAL);
  	 setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
  	 Bundle extras = getIntent().getExtras(); 
  	 username= extras.getString("uname");
	 password = extras.getString("pword");	
	 url=extras.getString("url");
     S=new TextView(this);
      S.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));     
      S.setText("Enter sensor Id");
      main1.addView(S);
  	 sensor =new  AutoCompleteTextView(this);
  	 sensor.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));     
      main1.addView(sensor);
      sensor.addTextChangedListener(new TextWatcher(){
    	  public void onTextChanged(CharSequence s, int start, int before, int count) {
  System.out.println();
              // TODO Auto-generated method stub
          }

          @Override
          public void beforeTextChanged(CharSequence s, int start, int count, int after) {

              // TODO Auto-generated method stub
        	try{
        		//Toast.makeText(getApplicationContext(),"before",Toast.LENGTH_SHORT).show();
        	
        
        		 new MyAsync3().execute(username,password,url,query);
        	}catch(Exception e)
        	{ }
        	
          }

          @Override
          public void afterTextChanged(Editable s) {
              // TODO Auto-generated method stub
        	  System.out.println();    }
    	  
    	  
      });
      sensor.setAdapter(sensor_adapter);
      
      S1=new TextView(this);
      S1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));     
      S1.setText("Enter Patient Id");
      main1.addView(S1);
  	 patient =new EditText(this);
  	 patient.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));     
      main1.addView(patient);
      b1=new Button(this);
      b1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));   
      b1.setText("Get Concepts");
      b1.setOnClickListener(this);
      
      main1.addView(b1);
      setContentView(main1);
		
		
	
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
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(uri+"/ws/rest/v1/sensor/scm/"+query);
		httpGet.addHeader(BasicScheme.authenticate(new UsernamePasswordCredentials(username, password),"UTF-8", false));
		HttpResponse httpResponse = httpClient.execute(httpGet);
		HttpEntity responseEntity = httpResponse.getEntity();
		String str = inputStreamToString(responseEntity.getContent()).toString();
		httpClient.getConnectionManager().shutdown();
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
	
	public static String Httpget2 (String username, String password, String uri,String query) throws ClientProtocolException, IOException
	{
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(uri+"/ws/rest/v1/sensor/scm");
		httpGet.addHeader(BasicScheme.authenticate(new UsernamePasswordCredentials(username, password),"UTF-8", false));
		HttpResponse httpResponse = httpClient.execute(httpGet);
		HttpEntity responseEntity = httpResponse.getEntity();
		String str = inputStreamToString(responseEntity.getContent()).toString();
		httpClient.getConnectionManager().shutdown();
	//	Toast.makeText(getApplicationContext().this,"Successful", Toast.LENGTH_LONG).show();
	System.out.println("httpget22222"+str);	
		
		return str;  
		         
    	} 
	
	
	private class MyAsyncTask2 extends AsyncTask<String, String, String>
	{
		String res1,res2,res3;
		@Override
		protected String doInBackground(String... params) {
			try {
				res1 = (Httpget(params[0],params[1], params[2],params[3]));
			} catch (ClientProtocolException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			   try {
				res2=JsonParse2(res1);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			   try {
				res3=Httppost(params[0],params[1],params[2],res2,params[4],sensor.getText().toString(),patient.getText().toString());
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			   return res3;
		}
		
		 protected void onPostExecute(String params){
			 Toast.makeText(getApplicationContext(),"Successful", Toast.LENGTH_LONG).show();
		 }
		
		
	}
private  class MyAsyncTask extends AsyncTask<String, String, String>{
		
		String res1,res2;
		  
		  protected String doInBackground(String... params) {
			  
			   try {
				   
				   res1 = (Httpget(params[0],params[1], params[2],params[3]));
				   res2=JsonParse(res1);
				   
				  
				  
				   
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		       
				return res2;
		  }
		  
		  
		  protected void onPostExecute(String params){
			  
			  String arrayString[];
			 
			
			  EditText ed;
			 allEds = new ArrayList<EditText>();
			
			  if(params.equals("zero"))
			  {
				  Toast.makeText(getApplicationContext(),"No Concepts found.", Toast.LENGTH_LONG).show();
			  }
			  
			  else{
				
				 System.out.println("array string length");
	      arrayString= params.split("\\*");
	     
	     final int size=arrayString.length;
        
          System.out.println("array string length"+size);

      
	 for(int i=0;i<size;i++)
	 {  
		 
		// Toast.makeText(getApplicationContext(),i+"  ", Toast.LENGTH_LONG).show();
		 TextView rowTextView = new TextView(Sensorconcept.this);
		 rowTextView.setText(arrayString[i]);
		 main1.addView(rowTextView);
	    ed = new EditText(Sensorconcept.this);
	  
	  
	    allEds.add(ed);
     ed.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
	    main1.addView(ed);	 
	 
	      
	 }
	 
	
	 b2=new Button(Sensorconcept.this);
     b2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));   
     b2.setText("Post Readings");
     b2.setOnClickListener(Sensorconcept.this);
    
     b2.setOnClickListener(new View.OnClickListener() {
    	    @Override
    	    public void onClick(View v) {
    	    	 String[] strings = new String[size];
    	    	 String fin = "" ;
    	    	 for(int i=0; i < allEds.size(); i++){
    	    	     strings[i] = allEds.get(i).getText().toString();
    	    	     fin=fin+strings[i]+"*";
    	    	 }
    	    	 final String send = fin.substring(0,fin.length()-1);
    	    	 
    	    	query=sensor.getText().toString();
    	    	 new MyAsyncTask2().execute(username,password,url,query,send);
    	    }
    	});
     main1.addView(b2);
     b3=new Button(Sensorconcept.this);
     b3.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));   
     b3.setText("Go to app");
     main1.addView(b3);
     b3.setOnClickListener(new View.OnClickListener(){
    	 @Override
    	 public void onClick(View v)
    	 { Intent i1 = new Intent();

    	    Intent i=null;
    	     PackageManager manager = getPackageManager();
    	     try {
    	         i = manager.getLaunchIntentForPackage("org.opendatakit.sensors.drivers.xpodpulseox");
    	         if (i == null)
    	             throw new PackageManager.NameNotFoundException();
    	         i.addCategory(Intent.CATEGORY_LAUNCHER);
    	         startActivityForResult(i,0);
    	     } catch (PackageManager.NameNotFoundException e) {
    	    	 Toast.makeText(getApplicationContext(),"Package not found exception", Toast.LENGTH_LONG).show(); 
    	     }
    	     
    	     
    	
    	 
    	 }
     });
    
	     b4=new Button(Sensorconcept.this);
	     b4.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));   
	     b4.setText("Get the values");
	     main1.addView(b4);
	     b4.setOnClickListener(new View.OnClickListener(){
	    	 @Override
	    	 public void onClick(View v){
	    		 
	    		   String a[]=new String[size]; 
	    	       Integer a2[]=new Integer[size];
	    	    	
	    	    		int s1=prefs.getInt("array_size",0);
	    	    	    for(int j=0;j<size;j++) 
	    	    		{      
	    	    	    	
	    	   			{ 
	    	   				
	    	   				EditText e;
	    	    			e=allEds.get(j);
	    	    			a2[j]=prefs.getInt("Value"+j,0);
	    	    			e.setText(Integer.toString(a2[j]));
	    	 	    		  
	    	   				
	    	   				
	    	   			 
	    	   		 }
 	    	
	    	    		}
	    		 
	    		 
	    	 }	 }); 
	
	
			 
		  }
		  }	  
		  
		  protected void onProgressUpdate(Integer... progress){
			    
		  }
		  }
public static String JsonParse3(String str) throws JSONException
{ String res=" ";
scm_name_to_id=new HashMap<String, String>();
JSONObject query = null;
	query = new JSONObject(str);
	JSONArray results = null;
	results = query.getJSONArray("results");
	for (int it = 0 ; it < results.length(); it++) {
		JSONObject current_sensor = null;
		
			current_sensor = (JSONObject) ((JSONObject)results.get(it)).get("sensor");
		
			res=res+current_sensor.get("sensor_name").toString()+"*";
			System.out.println("id"+current_sensor.get("sensor_id").toString());
			
		scm_name_to_id.put(current_sensor.get("sensor_name").toString(), current_sensor.get("sensor_id").toString());
			//Toast.makeText(getApplicationContext(),"herer loop", Toast.LENGTH_SHORT).show();
		System.out.println("in jason"+res);
	} 

	return res;
	
}

		 
	private class MyAsync3 extends AsyncTask<String, String, String>
	{
		String res1,res2,res3;
		@Override
		protected String doInBackground(String... params) {
			try {
				res1 = JsonParse3(Httpget2(username,password, url,null));
			} catch (ClientProtocolException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			   
			   return res1;
		}
		
		 protected void onPostExecute(String params){
			// Toast.makeText(getApplicationContext(),"Successful"+params, Toast.LENGTH_LONG).show();
	System.out.println("post strtg"+params);
			 String[] arrayString;
			try{  
			  arrayString= params.split("\\*");
			for(String e:arrayString)
			{
				System.out.println(e);
			}
			  
			//  System.out.println("in post"+arrayString);
			  sensor_adapter = new ArrayAdapter<String>(Sensorconcept.this, android.R.layout.simple_list_item_1, arrayString );	  
			  // ((AutoCompleteTextView) sensor).setAdapter(sensor_adapter); 
		 sensor.setAdapter(sensor_adapter);
		 }
		  catch(Exception e) {
			  }
		  }
		
	}
		

@Override
public void onClick(View v) {
	
	System.out.println(scm_name_to_id.get("new"));
	String q1;
		query=sensor.getText().toString();
		
	q1=	scm_name_to_id.get(query);
	  new MyAsyncTask().execute(username,password,url,q1);	
}
	
}

