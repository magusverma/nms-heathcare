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

import utils.HTTP.HTTP_Functions;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;



public class Create_Sensor_Automatically extends Activity {
	String Sensor_name;
	String Package_name;
	public Bundle bundle;
	private String username,password,url;
	Button b1;
	EditText Sensor;
	LinearLayout LLayout1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create__sensor);
		bundle = getIntent().getExtras();
		LLayout1=(LinearLayout) findViewById(R.id.lay1);
		Sensor=(EditText) findViewById(R.id.editText1);
		Sensor.setText(bundle.getString("Sensor"));
		SharedPreferences sharedPref = getSharedPreferences("mhealth", Context.MODE_PRIVATE);
        username = sharedPref.getString(getString(R.string.username), "");
		password = sharedPref.getString(getString(R.string.password), "");
		url = sharedPref.getString(getString(R.string.url), "");
		System.out.println(username + " "+password+" "+url);
		List<EditText> allEds = new ArrayList<EditText>();
	 	String [] concepts = bundle.getStringArray("Concepts");
	 	for(int j=0;j<concepts.length;j++) 
 			{      	

				EditText ed; 			
				TextView rowTextView = new TextView(Create_Sensor_Automatically.this);
				rowTextView.setText("Concept"+(j+1));
				LLayout1.addView(rowTextView);
				ed = new EditText(Create_Sensor_Automatically.this);
				allEds.add(ed);
				ed.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
				LLayout1.addView(ed); 		    
				ed.setText(getConceptName(concepts[j]));
			}
	    Button create = new Button(this);
        create.setText("Create");
        create.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        create.setId(9);
        LLayout1.addView(create);	      
        create.setOnClickListener(new View.OnClickListener(){
        	  	
	    	 @Override
	    	 public void onClick(View v){
	    		 
	    		 new Sensor_Create_AsynTask().execute(username,password,url);
	    	
	    	    		
	    	 							}	 
	    	 												}  		
        							); 

		
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

	public static Integer idJsonParse(String str) throws JSONException
	{
		
		JSONObject obj = new JSONObject(str);
		Integer uuid = obj.getInt("sensor_id");
		return uuid;
		 	 
		
	}
	
	public  String  saveInPreference(String sens_id)
	{
		final String REGISTER = "Register_Sensor";		
		getApplicationContext();
		SharedPreferences pref = getApplicationContext().getSharedPreferences(REGISTER,Context.MODE_PRIVATE);
	    SharedPreferences.Editor editor = pref.edit();
		
		HashMap <String,String> sensor_package_name=new HashMap<String, String>();
		HashMap <String,String> sensor_id = new HashMap<String, String>();
 		HashMap <String,List<String>> sensor_concept=new HashMap<String, List<String>>();
			
		List<String> concepts=new ArrayList<String>();	
			
	    		 if(bundle!=null)
				
	    		 { 
	    			 	Set<String> keys=bundle.keySet();
		    			 for(String key:keys)
		    			 {
			    			    
		    				 	if(key.equals("Sensor"))
		    			 
					    			 {  Sensor_name=bundle.getString(key);
					    			 	continue; }
		    				 
			    				 if(key.equals("Package_Name"))
				    				 {  Package_name=bundle.getString(key);
					    				 continue;	
				    				 }
		    		
			    				 else
			    				 {
				    				String [] array_of_concepts = bundle.getStringArray("Concepts");
				    				for(int i =0;i<array_of_concepts.length;i++)
					    			 concepts.add(array_of_concepts[i]);
					    			 
			    				 }
			    		 }
		    			 sensor_concept.put(Sensor_name,concepts);
		    			 sensor_package_name.put(Sensor_name,Package_name);
		    			 sensor_id.put(Sensor_name, sens_id);
	    		 }
	    		
	    		 GsonBuilder gsonb = new GsonBuilder();
	    		 Gson gson = gsonb.create();
	    		 String json=new Gson().toJson(sensor_package_name);	    		 
	    		String json2=new Gson().toJson(sensor_concept);
	    		String json3=new Gson().toJson(sensor_id);
	    		System.out.println(json.toString()+"  "+json2.toString());
				editor.putString("sensor_pack_map",json );
				editor.putString("sensor_concept_map",json2 );
				editor.putString("sensor_id_map",json3 );
				editor.commit();
				return "success";
		
	}
	
	private class Sensor_Create_AsynTask extends AsyncTask<String, String, String>
	{
		String result1;
		Integer sensor_id;
		@Override
		protected String doInBackground(String... params) {
			try {
				JSONObject sensor_create = new JSONObject();				
				sensor_create.put("sensor_id", 1);
				sensor_create.put("sensor_name",bundle.getString("Sensor"));
				result1 = HTTP_Functions.Httppost(params[0],params[1],url+"/ws/rest/v1/sensor/sm",sensor_create);
				sensor_id=idJsonParse(result1);
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
			   
			   return sensor_id.toString();
		}
		
		 protected void onPostExecute(String params){
				 Toast.makeText(getApplicationContext(),"Sensor created successfully with ID "+params, Toast.LENGTH_LONG).show();
				 SharedPreferences sharedPref = getSharedPreferences("mhealth", Context.MODE_PRIVATE);
			     username = sharedPref.getString(getString(R.string.username), "");
			     password = sharedPref.getString(getString(R.string.password), "");
				 url = sharedPref.getString(getString(R.string.url), "");
				 new Sensor_Concept_Async_Task().execute(sharedPref.getString(getString(R.string.username), ""),sharedPref.getString(getString(R.string.password), ""),sharedPref.getString(getString(R.string.url), ""),params);
					
			 
		  
		  }
		
	}
	
	private class Sensor_Concept_Async_Task extends AsyncTask<String, String, String>
	{
		String result1;
		@Override
		protected String doInBackground(String... params) {
			try {
				JSONObject sensor_concept = new JSONObject();
				sensor_concept.put("sensor", params[3]);
				JSONArray concept = new JSONArray();
				String[] concepts = bundle.getStringArray("Concepts");
				for(int i=0;i<bundle.getStringArray("Concepts").length;i++)
				{
					System.out.println("Concept_"+(i+1)+" "+concepts[i]);
					concept.put(concepts[i]);
				}
				
				sensor_concept.put("concepts",concept);
				result1 = HTTP_Functions.Httppost(params[0],params[1],url+"/module/sensorreading/scm.form",sensor_concept);
				System.out.println("returned...   "+result1);
				
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
		
		 protected void onPostExecute(String params)
		 {
				 Toast.makeText(getApplicationContext(),params, Toast.LENGTH_LONG).show();
				 if(saveInPreference(params).equalsIgnoreCase("success"))
						 {
						 Toast.makeText(getApplicationContext(),"Concepts Mapped Successfully", Toast.LENGTH_LONG).show();
						 }
		 
			 
		  
		}
		
	}

	
	}

	