package com.example.mhealth;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

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

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;



public class Create_Sensor extends Activity {
	String Sensor_name;
	String Pack_name;
	int conc_num;
	public Bundle bundle;
	final String CRED = "Credentials";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create__sensor);
		bundle = getIntent().getExtras();
		SharedPreferences prefs = getSharedPreferences("mhealth", Context.MODE_PRIVATE);
	    new MyAsync().execute(prefs.getString("username", "admin"),prefs.getString("password", "Admin123"),prefs.getString("url",""));
		
	
	}
	
	public static String NormalHttppost(String username, String password ,String url, JSONObject jo) throws ClientProtocolException, IOException
	{
		HttpClient httpClient = new DefaultHttpClient();
		ResponseHandler<String> resonseHandler = new BasicResponseHandler();
		HttpPost postMethod = new HttpPost(url+"/ws/rest/v1/sensor/sm");    
		postMethod.setEntity(new StringEntity(jo.toString()));
		postMethod.setHeader( "Content-Type", "application/json");
		//String authorizationString = "Basic " + Base64.encodeToString(("admin" + ":" + "Admin123").getBytes(), Base64.DEFAULT); //this line is diffe
		//authorizationString.replace("\n", "");
		//postMethod.setHeader("Authorization", authorizationString);
		postMethod.addHeader(BasicScheme.authenticate(new UsernamePasswordCredentials(username,password),"UTF-8", false));
		String response = httpClient.execute(postMethod,resonseHandler);
		return response;
		
	}
	
	public static String Httppost(String username, String password ,String url, JSONObject jo) throws ClientProtocolException, IOException
	{
		HttpClient httpClient = new DefaultHttpClient();
		ResponseHandler<String> resonseHandler = new BasicResponseHandler();
		HttpPost postMethod = new HttpPost(url+"/module/sensorreading/scm.form");    
		postMethod.setEntity(new StringEntity(jo.toString()));
		postMethod.setHeader( "Content-Type", "application/json");
		//String authorizationString = "Basic " + Base64.encodeToString(("admin" + ":" + "Admin123").getBytes(), Base64.DEFAULT); //this line is diffe
		//authorizationString.replace("\n", "");
		//postMethod.setHeader("Authorization", authorizationString);
		postMethod.addHeader(BasicScheme.authenticate(new UsernamePasswordCredentials(username,password),"UTF-8", false));
		String response = httpClient.execute(postMethod,resonseHandler);
		return response;
		
	}
	
	
	public static Integer idJsonParse(String str) throws JSONException
	{
		
		JSONObject obj = new JSONObject(str);
		Integer uuid = obj.getInt("sensor_id");
		return uuid;
		 	 
		
	}
	
	public  String  saveInPreference(String sens_id)
	{
			final String REGISTERING = "Registering";
		
		
		SharedPreferences pref = getApplicationContext().getSharedPreferences(REGISTERING,getApplicationContext().MODE_PRIVATE);
	    SharedPreferences.Editor editor = pref.edit();
		
		HashMap <String,String> sensor_packname=new HashMap();
		HashMap <String,String> sensor_id = new HashMap();
 		HashMap <String,List<String>> sensor_concept=new HashMap();
			
		List<String> concepts=new ArrayList();	
			
	    		 if(bundle!=null)
				
	    		 {   Set<String> keys=bundle.keySet();
    			 
	    		 for(String s1:keys)
    			 {
	    			    
    				 if(s1.equals("Sensor"))
    			 
    			 { Sensor_name=bundle.getString(s1);
    			 	System.out.println(Sensor_name);
    			 	continue; }
    				 
    				 if(s1.equals("Package_Name"))
    				 {  Pack_name=bundle.getString(s1);
    				 System.out.println(Pack_name);
    				 continue;	
    				 }
    		
    				 else{
	    			 concepts.add(bundle.getString(s1));
	    			 
    				 }
	    		 }
    			 sensor_concept.put(Sensor_name,concepts);
    			 sensor_packname.put(Sensor_name,Pack_name);
    			 sensor_id.put(Sensor_name, sens_id);
	    		 }
	    		
	    		 GsonBuilder gsonb = new GsonBuilder();
	    		 Gson gson = gsonb.create();
	    		 String json=new Gson().toJson(sensor_packname);
	    		 
	    		String json2=new Gson().toJson(sensor_concept);
	    		String json3=new Gson().toJson(sensor_id);
	    		System.out.println(json.toString()+"  "+json2.toString());
				editor.putString("sensor_pack_map",json );
				editor.putString("sensor_concept_map",json2 );
				editor.putString("sensor_id_map",json3 );
				editor.commit();
				return "success";
		
	}
	
	private class MyAsync extends AsyncTask<String, String, String>
	{
		String res1,res2,res3;
		Integer rs;
		@Override
		protected String doInBackground(String... params) {
			try {
				JSONObject nm = new JSONObject();
				nm.put("sensor_id", 1);
				nm.put("sensor_name",bundle.getString("Sensor"));
				res1 = NormalHttppost(params[0],params[1],params[2],nm);
				rs=idJsonParse(res1);
			} catch (ClientProtocolException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			   
			   return rs.toString();
		}
		
		 protected void onPostExecute(String params){
		 Toast.makeText(getApplicationContext(),"Successful sensor created with ID "+params, Toast.LENGTH_LONG).show();
		 SharedPreferences prefs = getApplicationContext().getSharedPreferences(CRED,getApplicationContext().MODE_PRIVATE);
		 new MyAsync2().execute(prefs.getString("username", "admin"),prefs.getString("password", "Admin123"),prefs.getString("url",""),params);
			
			 
		  
		  }
		
	}
	
	private class MyAsync2 extends AsyncTask<String, String, String>
	{
		String res1,res2,res3;
		Integer rs;
		@Override
		protected String doInBackground(String... params) {
			try {
				JSONObject nm = new JSONObject();
				nm.put("sensor", params[3]);
				JSONArray ja = new JSONArray();
				for(int i=0;i<bundle.getInt("Conc_num");i++)
				{
					ja.put(bundle.getString("Concept_"+(i+1)));
				}
				
				nm.put("concepts",ja);
				res1 = NormalHttppost(params[0],params[1],params[2],nm);
				
			} catch (ClientProtocolException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			   
			   return params[3];
		}
		
		 protected void onPostExecute(String params){
			 Toast.makeText(getApplicationContext(),params, Toast.LENGTH_LONG).show();
		 if(saveInPreference(params).equalsIgnoreCase("success"))
			 {
			 Toast.makeText(getApplicationContext(),"Successful concepts mapped", Toast.LENGTH_LONG).show();
			 };
		 
			 
		  
		  }
		
	}

		
	}

	