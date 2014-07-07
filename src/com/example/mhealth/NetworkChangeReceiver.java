package com.example.mhealth;

import java.io.IOException;

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
		String status = NetworkUtil.getConnectivityStatusString(context);
		//System.out.println("Status: "+ status);
		SyncActivity sync_act = new SyncActivity();
		init_ca(context);
		SharedPreferences sharedPref = context.getSharedPreferences("mhealth", Context.MODE_PRIVATE);
		username = sharedPref.getString("username", "");
		password = sharedPref.getString("password", "");
		url = sharedPref.getString("url", "");
		
		System.out.println(username+" "+password+" "+ url + " network change");
		new AsyncpushReadings().execute(username,password,url);
		//ca.push_readings(username, password, url);
		//sync_act.pushReadings(null);
		Toast.makeText(context, status, Toast.LENGTH_LONG).show();
	}
	
	private  class AsyncpushReadings extends AsyncTask<String, String, String>{
		@Override
		protected String doInBackground(String... arg0) {
			System.out.println("Doing AsyncpushReadings");
			Integer pushed_count = ca.push_readings(username, password, url);
			return pushed_count.toString();
		}
		protected void onPostExecute(String pushed_count){
			System.out.println("Doing onPostExecute in AsyncpushReadings");
			//Toast.makeText(context(), "Pushed "+pushed_count+" Readings Data", Toast.LENGTH_LONG).show();
		}
		  
	}

	private SharedPreferences getSharedPreferences(String string,
			int modePrivate) {
		// TODO Auto-generated method stub
		return null;
	}
}