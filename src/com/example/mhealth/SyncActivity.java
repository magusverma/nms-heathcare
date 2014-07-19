package com.example.mhealth;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.json.JSONArray;
import org.json.JSONObject;

import utils.HTTP.HTTP_Functions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Manager;
import com.couchbase.lite.android.AndroidContext;

public class SyncActivity extends ActionBarActivity {

    static couch_api ca; 
    String username;
    String password;
    String url;

    public void init_ca() {
        if (ca == null) {
            Manager manager = null;
            try {
                manager = new Manager(new AndroidContext(this), Manager.DEFAULT_OPTIONS);
                System.out.println("Manager Created!");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            ca = new couch_api(manager);
            if (ca == null) {
                System.out.println("Failed to Set CouchApi");
            }
            System.out.println("Initialized ca in Sync Activity");
        } else {
            System.out.println("ca variable already initialized");
        }
    }

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("In Oncreate");
        setContentView(R.layout.activity_sync);
        SharedPreferences sharedPref = getSharedPreferences("mhealth", Context.MODE_PRIVATE);
        username = sharedPref.getString(getString(R.string.username), "");
        password = sharedPref.getString(getString(R.string.password), "");
        url = sharedPref.getString(getString(R.string.url), "");
        System.out.println(username + " " + password + " " + url);
        init_ca();

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
        new AsyncSensors().execute(username, password, url);
    }

    private class AsyncSensors extends AsyncTask < String, String, String > {@
            Override
            protected String doInBackground(String...arg0) {
                System.out.println("Doing AsyncSensors");
                //			Integer synced_count = ca.syncSensors(username, password, url);
                Integer synced_count = 0;
                try {
                    synced_count = syncSensors(username, password, url);
                } catch (ClientProtocolException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                return synced_count.toString();
            }
            protected void onPostExecute(String synced_count) {
                System.out.println("Doing onPostExecute");
                Toast.makeText(getApplicationContext(), "Synced " + synced_count + " Sensors Data", Toast.LENGTH_LONG).show();
            }

        }
        /** Called when the user clicks the Push Readings button */
    public void pushReadings(View view) {
        System.out.println("Clicked Push Readings Button");
        new AsyncpushReadings().execute(username, password, url);
    }

    private class AsyncpushReadings extends AsyncTask < String, String, String > {@
        Override
        protected String doInBackground(String...arg0) {
            System.out.println("Doing AsyncpushReadings");
            Integer pushed_count = 0;
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
        protected void onPostExecute(String pushed_count) {
            System.out.println("Doing onPostExecute in AsyncpushReadings");
            Toast.makeText(getApplicationContext(), "Pushed " + pushed_count + " Readings Data", Toast.LENGTH_LONG).show();
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

    /** Called when the user clicks the Visualize Patients button */
    public void viewPatients(View view) {
        Intent intent = new Intent(this, ShowDbData.class);
        intent.putExtra("db", ca.patients_db_name);
        intent.putExtra("view", ca.patients_view);
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

        public PlaceholderFragment() {}

        
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
                    if (i == 0) concept_id = concept_line[0];
                    else {
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
        Toast.makeText(getApplicationContext(), "Pushed " + ca.getDatabase(ca.concepts_db_name).getDocumentCount() + " Concepts Data", Toast.LENGTH_LONG).show();

        System.out.println("Done");
    }

    public void viewConcepts(View view) {
        Intent intent = new Intent(this, ShowDbData.class);
        intent.putExtra("db", ca.concepts_db_name);
        intent.putExtra("view", ca.concepts_view);
        startActivity(intent);
    }

    public Integer syncSensors(String username, String password, String url) throws ClientProtocolException, IOException {
        //		TODO:Remove This
        //		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        //		StrictMode.setThreadPolicy(policy);

        //Flush
        Database database = ca.getDatabase(ca.sensors_db_name);
        System.out.println("Flushing Documents = " + database.getDocumentCount());
        try {
            database.delete();
        } catch (CouchbaseLiteException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        database = ca.getDatabase(ca.sensors_db_name);
        System.out.println("Document Count = " + database.getDocumentCount());
        HTTP_Functions http = new HTTP_Functions();
        String str = http.Httpget(username, password, url + "/ws/rest/v1/sensor/scm");
        try {
            JSONObject j = new JSONObject(str);
            JSONArray ja = (JSONArray) j.get("results");
            for (int it = 0; it < ja.length(); it++) {
                System.out.println(ja.get(it));
                JSONObject scm = (JSONObject) ja.get(it);
                String sensor_id = ((JSONObject) scm.get("sensor")).get("sensor_id").toString();
                String sensor_name = ((JSONObject) scm.get("sensor")).get("sensor_name").toString();
                JSONArray concepts_array = (JSONArray) scm.get("concepts");
                HashMap < String, String > concepts = new HashMap < String, String > ();
                for (int i_ca = 0; i_ca < concepts_array.length(); i_ca++) {
                    //display":"BANDS","uuid":"576829b5-ddf8-11e3-b4c4-a0b3cc71229c"}]}
                    JSONObject concept = (JSONObject) concepts_array.get(i_ca);
                    String concept_id = getConceptId(concept.get("display").toString());
                    System.out.println("concept_id: " + concept_id);
                    // Old UUID Implementation
                    //					 concepts.put((String)concept.get("display"),(String) concept.get("uuid"));
                    // New ID Approach
                    concepts.put((String) concept.get("display"), concept_id);
                }
                System.out.println("Adding following SCM to LocalStore" + sensor_id + sensor_name + concepts);
                ca.createDocument("sensor", ca.makeSensorMap(sensor_id, sensor_name, concepts));
            }
        } catch (Exception e) {

        }
        return database.getDocumentCount();
    }

    /*
     * Returns concept id for a concept name by looking it up from a csv
     */
    public String getConceptId(String concept_name_to_search_for) {
        System.out.println(concept_name_to_search_for);
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";
        concept_name_to_search_for = concept_name_to_search_for.replaceAll(",", "");
        try {
            InputStream is = getResources().openRawResource(R.raw.cd);
            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                String[] concept_line = line.split(cvsSplitBy);
                String concept_id = "", concept_name = "";
                for (int i = 0; i < concept_line.length; i++) {
                    if (i == 0) concept_id = concept_line[0];
                    else {
                        concept_name = concept_name + concept_line[i];
                    }
                }
                concept_name = concept_name.replaceAll("\"", "");
                if (concept_name.equals(concept_name_to_search_for))
                    return concept_id;
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
                    if (i == 0) concept_id = concept_line[0];
                    else {
                        concept_name = concept_name + concept_line[i];
                    }
                }
                concept_name = concept_name.replaceAll("\"", "");
                if (concept_id.equals(concept_id_to_search_for))
                    return concept_name;
                System.out.println(concept_id + " : " + concept_name);
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

}
