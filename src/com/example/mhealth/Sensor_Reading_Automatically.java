package com.example.mhealth;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import utils.HTTP.HTTP_Functions;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Document;
import com.couchbase.lite.Manager;
import com.couchbase.lite.android.AndroidContext;

import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class Sensor_Reading_Automatically extends ActionBarActivity implements View.OnClickListener {

    EditText sensor, patient;
    Button get_concept, b2, b3, b4;
    String query;
    LinearLayout LLayout1;
    String username;
    String password;
    String url;
    TextView enter_patient_id;
    Bundle extras;
    HashMap < String, String > scm_name_to_id;
    String[] sensor_name_hinting_array;
    static couch_api ca;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScrollView sv = new ScrollView(this);
        LLayout1 = new LinearLayout(this);
        LLayout1.setOrientation(LinearLayout.VERTICAL);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        extras = getIntent().getExtras();
        SharedPreferences sharedPref = getSharedPreferences("mhealth", Context.MODE_PRIVATE);
        username = sharedPref.getString(getString(R.string.username), "");
        password = sharedPref.getString(getString(R.string.password), "");
        url = sharedPref.getString(getString(R.string.url), "");
        Manager manager = null;
        try {
            manager = new Manager(new AndroidContext(this), Manager.DEFAULT_OPTIONS);
            System.out.println("Manager Created!");
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        this.ca = new couch_api(manager);
        if (ca == null) {
            System.out.println("Failed to Set CouchApi");
        }
        enter_patient_id = new TextView(this);
        enter_patient_id.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        enter_patient_id.setText("Enter Patient Id");
        LLayout1.addView(enter_patient_id);
        patient = new EditText(this);
        patient.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        LLayout1.addView(patient);
        get_concept = new Button(this);
        get_concept.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        get_concept.setText("Get Concepts");
        get_concept.setOnClickListener(this);
        LLayout1.addView(get_concept);
        sv.addView(LLayout1);
        setContentView(sv);

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

    public static String get_sensor_concepts_from_db(String id) {
        String concepts_string = "";
        Document retrievedDocument = ca.getSensor(id);
        if (retrievedDocument != null) {
            System.out.println("Using Sensor Found in Database");
            HashMap < String, Object > sensor_concepts = (HashMap < String, Object > )((Map) retrievedDocument.getProperty("sensor_concepts"));
            for (String key: sensor_concepts.keySet()) {
                concepts_string = concepts_string + key + "*";
            }
        } else {
            System.out.println("No Such Sensor Found with ID=" + id);
        }
        return concepts_string;
    }

    public static JSONObject readings_json_create(String id_string, String readings_string, String sens, String pat) throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("sensor", sens);
        obj.put("patient", pat);
        JSONObject id_reading = new JSONObject();
        String id_array[] = id_string.split("\\*");
        String readings_array[] = readings_string.split("\\*");
        int size = id_array.length;
        for (int i = 0; i < size; i++) {
            id_reading.put(id_array[i], readings_array[i]);
        }
        obj.put("readings", id_reading);
        System.out.println(obj);
        //
        System.out.println("Running CouchBase Code");
        HashMap < String, Object > readings = new HashMap < String, Object > ();
        for (int i = 0; i < size; i++) {
            readings.put(id_array[i], readings_array[i]);
        }
        ca.createDocument("reading", ca.makeReadingMap(pat, sens, readings));
        ca.getAllDocument("reading");
        return obj;

    }

    public static String JsonParse(String str) throws JSONException {
        String name_display = "";
        JSONObject jo = new JSONObject(str);
        JSONArray ja = jo.getJSONArray("concepts");
        if (ja.length() == 0) {

            return "zero";
        }
        int len = ja.length();
        System.out.println(len);
        for (int i = 0; i < len; i++) {
            JSONObject jo1 = (JSONObject) ja.get(i);
            name_display = name_display + (String) jo1.get("display") + "*";
            System.out.println(name_display);

        }

        return name_display;
    }

    private class Post_reading_AsyncTask extends AsyncTask < String, String, String > //Marker
        {

            
            @Override
            protected String doInBackground(String...params) {

                Document retrievedDocument = ca.getSensor(params[3]);
                if (retrievedDocument != null) {
                    System.out.println("Using Sensor Found in Database");
                } else {
                    System.out.println("Looking up Sensors online");
                    retrievedDocument = ca.getSensor(params[3]);
                }

                String id_array[] = params[5].split("\\*");
                String reading_array[] = params[4].split("\\*");

                System.out.println("Running CouchBase Code");
                HashMap < String, Object > readings = new HashMap < String, Object > ();
                for (int i = 0; i < id_array.length; i++) {
                    readings.put(id_array[i], reading_array[i]);
                }
                String pat = patient.getText().toString();

                ca.createDocument("reading", ca.makeReadingMap(pat, params[3], readings));
                //
                try {
                    ca.push_readings(username, password, url);
                } catch (ClientProtocolException e) {
                    System.out.println("Client Protocol exception caught.");
                    e.printStackTrace();
                    return "failed";
                } catch (IOException e) {
                    System.out.println("IO exception caught.");
                    e.printStackTrace();
                    return "failed";
                } catch (NullPointerException e) {
                    System.out.println("No internet Connection");
                    return "failed";
                }

                return "done";

            }

            protected void onPostExecute(String params) {
                if (params.equals("failed")) {
                    Toast.makeText(getApplicationContext(), "Unsuccessful. Check your settings and connection.", Toast.LENGTH_LONG).show();
                } else {
                    try {
                        ca.getDatabase(ca.readings_db_name).getView(ca.pending_reading_view).createQuery().run();
                    } catch (CouchbaseLiteException e) {
                        System.out.println("Couch base Lite Exception");
                        e.printStackTrace();
                    } //refresh view
                    Integer rows_left = ca.getDatabase(ca.readings_db_name).getView(ca.pending_reading_view).dump().size();
                    System.out.println("Rows Left = " + rows_left);
                    if (rows_left > 0) {
                        Toast.makeText(getApplicationContext(), "Queued , Will be Sent when Network Available", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Successful", Toast.LENGTH_LONG).show();
                    }
                    final String READINGS = "Sensor_Readings";
                    SharedPreferences prefs = getApplicationContext().getSharedPreferences(READINGS, MODE_PRIVATE);
                    prefs.edit().clear().commit();
                }
            }

        }

    private class Sensor_concept_AsyncTask extends AsyncTask < String, String, String > {

        String result1, result2;

        protected String doInBackground(String...params) {
            result2 = get_sensor_concepts_from_db(params[3]);
            HTTP_Functions http = new HTTP_Functions();// params[3] has sensor_id
            if (result2.length() == 0) {
                try {
                    result1 = (http.Httpget(params[0], params[1], url + "/ws/rest/v1/sensor/scm/" + params[3]));
                    result2 = JsonParse(result1);
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
            }
            if (result2.equals("")) {
                return "zero";
            }
            return result2;
        }

        protected void onPostExecute(String params) {

            String arrayString[];
            EditText ed;
            final List < EditText > concept_readings = new ArrayList < EditText > ();
            if (params.equals("zero")) {
                Toast.makeText(getApplicationContext(), "No Concepts found.", Toast.LENGTH_LONG).show();
            } else {

                arrayString = params.split("\\*");
                final int size = arrayString.length;
                System.out.println("concept_size " + size);
                b3 = new Button(Sensor_Reading_Automatically.this);
                b3.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                b3.setText("Go to app");
                LLayout1.addView(b3);
                b3.setOnClickListener(new View.OnClickListener() {
                	@Override
                    public void onClick(View v) {
                        String pack;
                        Intent i = null;
                        final String REGISTER = "Register_Sensor";
                        PackageManager manager = getPackageManager();
                        SharedPreferences pref = getApplicationContext().getSharedPreferences(REGISTER, getApplicationContext().MODE_PRIVATE);
                        try {
                            JSONObject jo = new JSONObject(pref.getString("sensor_pack_map", ""));
                            String sensor = extras.getString("sensor");
                            pack = jo.getString(sensor);
                            i = manager.getLaunchIntentForPackage(pack);
                            if (i == null)
                                throw new PackageManager.NameNotFoundException();
                            i.addCategory(Intent.CATEGORY_LAUNCHER);
                            startActivityForResult(i, 0);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (PackageManager.NameNotFoundException e) {
                            Toast.makeText(getApplicationContext(), "Package not found exception", Toast.LENGTH_LONG).show();
                        }
                    }
                });

                b4 = new Button(Sensor_Reading_Automatically.this);
                b4.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                b4.setText("Get the readings");
                LLayout1.addView(b4);
                b4.setOnClickListener(new View.OnClickListener() {
                	@Override
                    public void onClick(View v) {
                        final String READINGS = "Sensor_Readings";
                        SharedPreferences prefs = getApplicationContext().getSharedPreferences(READINGS, MODE_PRIVATE);
                        String a[] = new String[size];
                        Integer a2[] = new Integer[size];
                        int s1 = prefs.getInt("array_size", 0);
                        for (int j = 0; j < size; j++) {
                            EditText reading;
                            TextView rowTextView = new TextView(Sensor_Reading_Automatically.this);
                            rowTextView.setText(getConceptName(prefs.getString(String.valueOf(j), "0")));
                            LLayout1.addView(rowTextView);
                            reading = new EditText(Sensor_Reading_Automatically.this);
                            concept_readings.add(reading);
                            reading.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
                            LLayout1.addView(reading);
                            int val = (prefs.getInt(prefs.getString(String.valueOf(j), "0"), 0));
                            reading.setText(String.valueOf(val));

                        }

                    }

                });

                b2 = new Button(Sensor_Reading_Automatically.this);
                b2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                b2.setText("Post Readings");
                b2.setOnClickListener(Sensor_Reading_Automatically.this);
                LLayout1.addView(b2);
                b2.setOnClickListener(new View.OnClickListener() {
                	@Override
                    public void onClick(View v) {
                        final String READINGS = "Sensor_Readings";
                        SharedPreferences prefs = getApplicationContext().getSharedPreferences(READINGS, MODE_PRIVATE);
                        String ids = "";
                        String[] readings = new String[size];
                        String readings_concat = "";
                        for (int i = 0; i < concept_readings.size(); i++) {
                            readings[i] = concept_readings.get(i).getText().toString();
                            readings_concat = readings_concat + readings[i] + "*";
                            ids = ids + (prefs.getString(String.valueOf(i), "0")) + "*";
                            System.out.println("printing string......." + ids);

                        }
                        final String to_send = readings_concat.substring(0, readings_concat.length() - 1);
                        final String send_id = ids.substring(0, ids.length() - 1);

                        final String REGISTERING = "Register_Sensor";
                        SharedPreferences pref = getApplicationContext().getSharedPreferences(REGISTERING, getApplicationContext().MODE_PRIVATE);
                        String js = pref.getString("sensor_id_map", "");
                        JSONObject jo;

                        try {
                            jo = new JSONObject(js);
                            String sid = jo.getString(extras.getString("sensor"));
                            new Post_reading_AsyncTask().execute(username, password, url, sid, to_send, send_id);
                        } catch (JSONException e) {
                            System.out.println("JSON Exception caught");
                            e.printStackTrace();
                        }
                    }
                });

            }
        }

        protected void onProgressUpdate(Integer...progress) {

        }
    }

    @Override
    public void onClick(View v) {

        if (patient.getText().toString().length() < 1) {
            Toast.makeText(getApplicationContext(), "Enter Complete Details", Toast.LENGTH_LONG).show();
        } else {
            final String REGISTERING = "Register_Sensor";
            SharedPreferences pref = getApplicationContext().getSharedPreferences(REGISTERING, getApplicationContext().MODE_PRIVATE);
            String js = pref.getString("sensor_id_map", "");
            JSONObject jo;
            try {
                jo = new JSONObject(js);
                String sid = jo.getString(extras.getString("sensor"));
                new Sensor_concept_AsyncTask().execute(username, password, url, sid);
            } catch (JSONException e) {
                System.out.println("Failed to create json from string");
            }

        }

    }
}
