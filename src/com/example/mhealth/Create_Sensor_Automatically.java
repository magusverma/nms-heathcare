package com.example.mhealth;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;



public class Create_Sensor_Automatically extends Activity {
	String Sensor_name;
	String Pack_name;
	int conc_num;
	public Bundle bundle;
	final String CRED = "Credentials";
	private String username,password,url;
	Button b1;
	EditText e1;
	LinearLayout main1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create__sensor);
		bundle = getIntent().getExtras();
//		SharedPreferences prefs = getApplicationContext().getSharedPreferences(CRED,getApplicationContext().MODE_PRIVATE);
//	    new MyAsync().execute(prefs.getString("username", "admin"),prefs.getString("password", "Admin123"),prefs.getString("url",""));
		//ScrollView sv = new ScrollView(this);
		main1=(LinearLayout) findViewById(R.id.lay1);
		e1=(EditText) findViewById(R.id.editText1);
		e1.setText(bundle.getString("Sensor"));
		SharedPreferences sharedPref = getSharedPreferences("mhealth", Context.MODE_PRIVATE);
        username = sharedPref.getString(getString(R.string.username), "");
		password = sharedPref.getString(getString(R.string.password), "");
		url = sharedPref.getString(getString(R.string.url), "");
		System.out.println(username + " "+password+" "+url);
	 List<EditText> allEds = new ArrayList<EditText>();
	 	String [] concepts = bundle.getStringArray("Concepts");
	 	System.out.println("fsjhjshfkjd8787&*&&&"+bundle.getStringArray("Concepts").length);
		for(int j=0;j<concepts.length;j++) 
 		{      	

				EditText ed;
 			
 			TextView rowTextView = new TextView(Create_Sensor_Automatically.this);
 			 rowTextView.setText("Concept"+(j+1));
 			 main1.addView(rowTextView);
 		    ed = new EditText(Create_Sensor_Automatically.this);
 		    allEds.add(ed);
 	     ed.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
 		    main1.addView(ed);	
 		    
 		    
 		    ed.setText(getConceptName(concepts[j]));




		

 		}
		 Button b = new Button(this);
	        b.setText("Create");
	        b.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
	        b.setId(9);
	        main1.addView(b);	      
	        b.setOnClickListener(new View.OnClickListener(){
		    	
		    	
		    	 @Override
		    	 public void onClick(View v){
		    		 
		    		 new MyAsync().execute(username,password,url);
		    	
		    	    		
		    	}	 }  ); 

	     
		//sv.addView(main1);
		//setContentView(sv);
		
	
	}
	
	public static String NormalHttppost(String username, String password ,String url, JSONObject jo) throws ClientProtocolException, IOException
	{
		HttpClient httpClient = new DefaultHttpClient();
		ResponseHandler<String> resonseHandler = new BasicResponseHandler();
		System.out.println("in n URL  "+url);
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
	
	/*
	 * Returns concept name for a concept id by looking it up from a csv
	 */
	public String getConceptName(String concept_id_to_search_for) {
		System.out.println(concept_id_to_search_for);
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";
		//concept_name_to_search_for = concept_name_to_search_for.replaceAll(",","");
		try {
			InputStream is = getResources().openRawResource(R.raw.cd);
			br = new BufferedReader(new InputStreamReader(is));
			while ((line = br.readLine()) != null) {
				String[] concept_line = line.split(cvsSplitBy);
				String concept_id = "", concept_name = "";
				for (int i = 0; i < concept_line.length; i++) {
					if (i==0) concept_id = concept_line[0];
					else{
						concept_name = concept_name + concept_line[i];
					}
				}
				concept_name = concept_name.replaceAll("\"", "");
				if(concept_id.equals(concept_id_to_search_for))
					return concept_name;
				//System.out.println(concept_id+" : "+concept_name);
			}
	 
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return "-1";
	}

	
	public static String Httppost(String username, String password ,String url, JSONObject jo) throws ClientProtocolException, IOException
	{
		System.out.println("in http post "+ jo);
		HttpClient httpClient = new DefaultHttpClient();
		ResponseHandler<String> resonseHandler = new BasicResponseHandler();
		
		HttpPost postMethod = new HttpPost(url+"/module/sensorreading/scm.form");    
		System.out.println("URL  "+url);
		postMethod.setEntity(new StringEntity(jo.toString()));
		postMethod.setHeader( "Content-Type", "application/json");
		//String authorizationString = "Basic " + Base64.encodeToString(("admin" + ":" + "Admin123").getBytes(), Base64.DEFAULT); //this line is diffe
		//authorizationString.replace("\n", "");
		//postMethod.setHeader("Authorization", authorizationString);
		postMethod.addHeader(BasicScheme.authenticate(new UsernamePasswordCredentials(username,password),"UTF-8", false));
		System.out.println("respose^^^^^^^");
		String response = httpClient.execute(postMethod,resonseHandler);
		System.out.println("respose%%%%%%");
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
    				String [] arr = bundle.getStringArray("Concepts");
    				for(int i =0;i<arr.length;i++)
	    			 concepts.add(arr[i]);
	    			 
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
				System.out.println("sensor_name "+bundle.getString("Sensor"));
				res1 = NormalHttppost(params[0],params[1],params[2],nm);
				rs=idJsonParse(res1);
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
			   
			   return rs.toString();
		}
		
		 protected void onPostExecute(String params){
		 Toast.makeText(getApplicationContext(),"Sensor created successfully with ID "+params, Toast.LENGTH_LONG).show();
		 SharedPreferences sharedPref = getSharedPreferences("mhealth", Context.MODE_PRIVATE);
	        username = sharedPref.getString(getString(R.string.username), "");
			password = sharedPref.getString(getString(R.string.password), "");
			url = sharedPref.getString(getString(R.string.url), "");
			System.out.println(username + " "+password+" "+url);
			//new MyAsync().execute(username,password,url);
		 new MyAsync2().execute(sharedPref.getString(getString(R.string.username), ""),sharedPref.getString(getString(R.string.password), ""),sharedPref.getString(getString(R.string.url), ""),params);
			
			 
		  
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
				System.out.println("URL>>>>"+params[2]);
				System.out.println("id os sensor" + params[3]);
				nm.put("sensor", params[3]);
				JSONArray ja = new JSONArray();
				System.out.println("............. in myasync2");
				String[] concepts = bundle.getStringArray("Concepts");
				for(int i=0;i<bundle.getStringArray("Concepts").length;i++)
				{
					System.out.println("Concept_"+(i+1)+" "+concepts[i]);
					ja.put(concepts[i]);
				}
				
				nm.put("concepts",ja);
				res1 = Httppost(params[0],params[1],params[2],nm);
				System.out.println("returned...   "+res1);
				
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
			   
			   return params[3];
		}
		
		 protected void onPostExecute(String params){
			 Toast.makeText(getApplicationContext(),params, Toast.LENGTH_LONG).show();
		 if(saveInPreference(params).equalsIgnoreCase("success"))
			 {
			 Toast.makeText(getApplicationContext(),"Concepts Mapped Successfully", Toast.LENGTH_LONG).show();
			 };
		 
			 
		  
		  }
		
	}

	
	}

	