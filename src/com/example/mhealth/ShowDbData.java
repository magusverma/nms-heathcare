package com.example.mhealth;

import java.io.IOException;

import com.couchbase.lite.Manager;
import com.couchbase.lite.android.AndroidContext;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.os.Build;

public class ShowDbData extends ActionBarActivity {
	static couch_api ca; //magus
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_show_db_data);
		Bundle extras = getIntent().getExtras();
		String dbname = extras.getString("db");
		String view_name = extras.getString("view");
		
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
	    
	    TextView x =(TextView) findViewById(R.id.textView1);
	    x.setText(ca.get_view_data(dbname, view_name));
	    System.out.println(ca.get_view_data(dbname, view_name));
		
		/*if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}*/
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.show_db_data, menu);
		return true;
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
			View rootView = inflater.inflate(R.layout.fragment_show_db_data,
					container, false);
			return rootView;
		}
	}

}
