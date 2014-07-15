package com.example.mhealth;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.couchbase.lite.Manager;
import com.couchbase.lite.android.AndroidContext;

import android.content.Context;


//import android.support.v7.app.ActionBar;
//import android.support.v4.app.Fragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.os.Build;
import android.content.Context;

//import com.example.mhealth.R;
import com.couchbase.lite.Manager;

public class Login extends ActionBarActivity implements View.OnClickListener{
	
	EditText user;
	EditText pwd;
	EditText add;
	Button btn;
	public String username;
	public String password;
	public String url;
	public String abc;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.first_act);
		user=(EditText)findViewById(R.id.editText1);
         pwd=(EditText)findViewById(R.id.editText2);
         add=(EditText)findViewById(R.id.editText4);
         
         SharedPreferences sharedPref = getSharedPreferences("mhealth", Context.MODE_PRIVATE);
         String use = sharedPref.getString(getString(R.string.username), "");
         String pass = sharedPref.getString(getString(R.string.password), "");
         String uri = sharedPref.getString(getString(R.string.url), "");
         
         user.setText(use);
         pwd.setText(pass);
         add.setText(uri);         

       btn=(Button)findViewById(R.id.button1);
        btn.setOnClickListener(this);
        //btn.setOnClickListener((android.view.View.OnClickListener) this);;  
        
//        // CouchBase Wrangling
//      Manager manager = null;
//		try {
//			manager = new Manager(new AndroidContext(this), Manager.DEFAULT_OPTIONS);
//			System.out.println("Manager Created!");
//		} catch (IOException e1) {
//			e1.printStackTrace();
//		}
//        couch_api a = new couch_api(manager);
       
//        
//        a.createDocument("concept", a.makeConceptMap(2, "uuid-of00concept2", "Weight or Something"));
//        a.createDocument("patient",a.makePatientMap(2, "Mahesh"));
//        
//        Set<String> concepts = new HashSet<String>() ;
//        concepts.add("First Concept uuid");
//        concepts.add("Second Concept uuid");
//        a.createDocument("sensor", a.makeSensorMap(1, "My Sensor", concepts));
//    
        
//        a.clearAllDatabases();
//        HashMap<String, Object> readings = new HashMap<String, Object>();
//    	readings.put("576b65fc-ddf8-11e3-b4c4-a0b3cc71229c", "100");
//    	readings.put("576b68b0-ddf8-11e3-b4c4-a0b3cc71229c", "100");
//	    a.createDocument("reading",a.makeReadingMap("3", "2",readings ));   
	    
	    
//        
//        a.getAllDocument();
        //End CouchBase Wrangling
	}

	public void buttonClick()
	{
		 username = (user.getText().toString());
		 password= (pwd.getText().toString());
		 url=(add.getText().toString());
		//Toast.makeText(this, username+"  " + password, Toast.LENGTH_LONG).show();
//		Intent intent = new Intent(this, Options.class);
		
		//Previous Implementation
/*		intent.putExtra("uname",username);
//		intent.putExtra("pword",password);
	//	intent.putExtra("url", url);
*/		
		//New Implementation
		SharedPreferences sharedPref = getSharedPreferences("mhealth", Context.MODE_PRIVATE);
        
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putString(getString(R.string.username), username);
        editor.putString(getString(R.string.password), password);
        editor.putString(getString(R.string.url), url);
//        editor.putString("username", username);
        editor.commit();
        //http://192.168.1.7:8080/openmr
//        String user = getResources().getString(R.string.username);
//        String pass = getResources().getString(R.string.password);
//        String uri = getResources().getString(R.string.url);
        String user = sharedPref.getString(getString(R.string.username), "");
        String pass = sharedPref.getString(getString(R.string.password), "");
        String uri = sharedPref.getString(getString(R.string.url), "");
        System.out.println(user+" "+pass+" "+uri);
        Toast.makeText(this, "Settings Saved , trying to validate!", Toast.LENGTH_LONG).show();
//      intent.putExtra("uname",user);
//		intent.putExtra("pword",pass);
//		intent.putExtra("url", uri);
	}
	
	public void SwitchIntent(){
		Intent intent = new Intent(this, Options.class);
		startActivity(intent);
	}
	
	public static int Httpget (String username, String password, String url) throws ClientProtocolException, IOException, HttpHostConnectException
	{
		HttpClient httpClient = new DefaultHttpClient();
		
		HttpGet httpGet = new HttpGet(url+"/ws/rest/v1/patientidentifiertype");
		httpGet.addHeader(BasicScheme.authenticate(
		 new UsernamePasswordCredentials(username, password),
		 "UTF-8", false));

		HttpResponse httpResponse = httpClient.execute(httpGet);
		int status = httpResponse.getStatusLine().getStatusCode();
		return status;
		          	

		
	}
	
private  class MyAsyncTask extends AsyncTask<String, String, String>{
		
		int res;
		  @Override
		  protected String doInBackground(String... params) {
			  
			   try {
				   
				   res = (Httpget(params[0],params[1], params[2]));
				   if(res==401)
					   return "cred";
				   else if (res==404)
					   return "invalid";
				   else if (res == 200)
					   return "successful";
				   
			   } catch (HttpHostConnectException e) {
				   return "refused";
			   }   catch (IllegalStateException e) {
					   return "no"; 
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		       
				return "quit";
		  }
		  
		  
		  protected void onPostExecute(String params){
			  //Toast.makeText(getApplicationContext(), "Invalid credentials", Toast.LENGTH_LONG).show();
			  //buttonClick();
			 if(params.equals("cred"))
			 {
				 Toast.makeText(getApplicationContext(), "Invalid credentials", Toast.LENGTH_LONG).show();
			 }
			 else if(params.equals("invalid"))
			 {
				 Toast.makeText(getApplicationContext(), "Invalid URL", Toast.LENGTH_LONG).show();
			 }
			 else if(params.equals("refused"))
			 {
				 // TODO : reduce timeout of http , currently below message gets shown after two minutes when offline
				 Toast.makeText(getApplicationContext(), "Connection refused: Wrong URL or server  connection error", Toast.LENGTH_LONG).show();
			 }
			 else if (params.equals("successful"))
			 {
				 Toast.makeText(getApplicationContext(), "Settings Verified", Toast.LENGTH_LONG).show();
//				 buttonClick();
			 }
			 else{
				 Toast.makeText(getApplicationContext(), "Unable to Connect , Please Reverify when network comes!", Toast.LENGTH_LONG).show();
			 }
		  }
		  
		  protected void onProgressUpdate(Integer... progress){
			    
		  }
		  }

	public void onClick(View v) {
		if(user.getText().toString().length()<1 || pwd.getText().toString().length()<1 || add.getText().toString().length()<1 ){
					
					// out of range
					Toast.makeText(this, "please enter complete details", Toast.LENGTH_LONG).show();
				}
		
		else
		{
			switch (v.getId())
			{
			case R.id.button1:
				buttonClick();

				username = (user.getText().toString());
				 password= (pwd.getText().toString());
				 url=(add.getText().toString());
				 
				new MyAsyncTask().execute(username,password, url);
					break;
					
			}
			
		
		}
	}
}