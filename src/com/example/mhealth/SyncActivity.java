package com.example.mhealth;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Manager;
import com.couchbase.lite.android.AndroidContext;

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

public class SyncActivity extends ActionBarActivity {
	
	static couch_api ca; //magus
	String username;
	String password;
	String url;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sync);
		Bundle extras = getIntent().getExtras();
//		username= extras.getString("uname");
//		password = extras.getString("pword");
//		url=extras.getString("url");
		SharedPreferences sharedPref = getSharedPreferences("mhealth", Context.MODE_PRIVATE);
        username = sharedPref.getString(getString(R.string.username), "");
		password = sharedPref.getString(getString(R.string.password), "");
		url = sharedPref.getString(getString(R.string.url), "");
		
		//Couch Initialize
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
		//

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sync, menu);
		return true;
	}
	
	/** Called when the user clicks the Sensors button */
	public void syncSensors(View view) {
	    System.out.println("Clicked Sensors Sync Button");
	    new AsyncSensors().execute(username,password,url);
	}
	
	private  class AsyncSensors extends AsyncTask<String, String, String>{
		@Override
		protected String doInBackground(String... arg0) {
			System.out.println("Doing AsyncSensors");
			Integer synced_count = ca.syncSensors(username, password, url);
			return synced_count.toString();
		}
		protected void onPostExecute(String synced_count){
			System.out.println("Doing onPostExecute");
			Toast.makeText(getApplicationContext(), "Synced "+synced_count+" Sensors Data", Toast.LENGTH_LONG).show();
		}
		  
	}
	/** Called when the user clicks the Push Readings button */
	public void pushReadings(View view) {
	    System.out.println("Clicked Push Readings Button");
	    new AsyncpushReadings().execute(username,password,url);
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
			Toast.makeText(getApplicationContext(), "Pushed "+pushed_count+" Readings Data", Toast.LENGTH_LONG).show();
		}
		  
	}
	
	/** Called when the user clicks the Visualize Sensors button */
	public void viewSensors(View view) {
		Intent intent = new Intent(this, ShowDbData.class);
		intent.putExtra("db", ca.sensors_db_name);
		intent.putExtra("view", ca.sensor_view);
		startActivity(intent);
	}
	
	/** Called when the user clicks the Visualize Readings button */
	public void viewReadings(View view) {
		Intent intent = new Intent(this, ShowDbData.class);
		intent.putExtra("db", ca.readings_db_name);
		intent.putExtra("view", ca.pending_reading_view);
		startActivity(intent);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_sync, container,
					false);
			return rootView;
		}
	}
	
	/** Called when the user clicks the syncConcepts button */
	public void syncConcepts(View view) {
		System.out.println("Syncing Concepts");
		String csvFile = "/media/clone/adt-bundle-linux-x86-20140321/ws/Login/res/conceptDictionary1714_1323.csv";
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";
	 
		try {
			ca.getDatabase(ca.concepts_db_name).delete();
			System.out.println("Cleared Concepts Database");
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
				//System.out.println(concept_id+" : "+concept_name);
				ca.createDocument(ca.concepts_db_name, ca.makeConceptMap(concept_id, concept_name));
			}
	 
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (CouchbaseLiteException e) {
			// TODO Auto-generated catch block
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
		Toast.makeText(getApplicationContext(), "Pushed "+ca.getDatabase(ca.concepts_db_name).getDocumentCount()+" Concepts Data", Toast.LENGTH_LONG).show();
		
		System.out.println("Done");
	}
	
	public void viewConcepts(View view) {
		Intent intent = new Intent(this, ShowDbData.class);
		intent.putExtra("db", ca.concepts_db_name);
		intent.putExtra("view", ca.concepts_view);
		startActivity(intent);
	}
	
	
}
