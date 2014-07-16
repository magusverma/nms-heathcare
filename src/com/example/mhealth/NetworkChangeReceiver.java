package com.example.mhealth;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Manager;
import com.couchbase.lite.android.AndroidContext;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Toast;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.os.Build;

public class NetworkChangeReceiver extends BroadcastReceiver {
	static couch_api ca;
	String username,password,url;
	
	public void init_ca(Context context){
		if(ca==null){
			Manager manager = null;
			try {
				manager = new Manager(new AndroidContext(context), Manager.DEFAULT_OPTIONS);
				System.out.println("Manager Created!");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		    this.ca = new couch_api(manager);
		    if(ca == null){
		    	System.out.println("Failed to Set CouchApi");
		    }      
		    System.out.println("Initialized ca in Sync Activity");
		}
		else{
			System.out.println("ca variable already initialized");
		}
	}
	
	Context context;
	@Override
	public void onReceive(final Context context, final Intent intent) {
		this.context = context;
		init_ca(context);
		Integer rows_left = ca.getDatabase(ca.readings_db_name).getView(ca.pending_reading_view).dump().size();
		if(rows_left>0){
			try {
		    Thread.sleep(5000);
		} catch(InterruptedException ex) {
		    Thread.currentThread().interrupt();
		}

		String status = NetworkUtil.getConnectivityStatusString(context);
		//System.out.println("Status: "+ status);
		SyncActivity sync_act = new SyncActivity();
		SharedPreferences sharedPref = context.getSharedPreferences("mhealth", Context.MODE_PRIVATE);
		username = sharedPref.getString("username", "");
		password = sharedPref.getString("password", "");
		url = sharedPref.getString("url", "");
		
		System.out.println(username+" "+password+" "+ url + " network change");
		new AsyncpushReadings().execute(username,password,url);
		//ca.push_readings(username, password, url);
		//sync_act.pushReadings(null);
//		Toast.makeText(context, status, Toast.LENGTH_LONG).show();
		try {
			ca.getDatabase(ca.readings_db_name).getView(ca.pending_reading_view).createQuery().run();
		} catch (CouchbaseLiteException e) {
			e.printStackTrace();
		} //refresh view
		 
			rows_left = ca.getDatabase(ca.readings_db_name).getView(ca.pending_reading_view).dump().size();
			 System.out.println("Done");
			 System.out.println("Rows Left = "+rows_left);
			 if (rows_left.equals(0)){
				 Toast.makeText(context,"Successfully Pushed", Toast.LENGTH_LONG).show();
			 }
			 else{
				 System.out.println("que not empty yet");
			 }
	    
		}
		else{
			System.out.println("Everything Already Synced");
		}
	}
	
	private  class AsyncpushReadings extends AsyncTask<String, String, String>{
		@Override
		protected String doInBackground(String... arg0) {
			System.out.println("Doing AsyncpushReadings");
			Integer pushed_count=0;
			try {
				pushed_count = ca.push_readings(username, password, url);
			} catch (HttpResponseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return pushed_count.toString();
		}
		protected void onPostExecute(String pushed_count){
			System.out.println("Doing onPostExecute in AsyncpushReadings");
			//Toast.makeText(context(), "Pushed "+pushed_count+" Readings Data", Toast.LENGTH_LONG).show();
		}
		  
	}

	
}