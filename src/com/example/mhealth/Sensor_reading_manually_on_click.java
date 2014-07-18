package com.example.mhealth;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import utils.HTTP.HTTP_Functions;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Document;
import com.couchbase.lite.Manager;
import com.couchbase.lite.android.AndroidContext;

public class Sensor_reading_manually_on_click extends ActionBarActivity {

    EditText sensor, patient;
    Button b1, b2;
    String query;
    LinearLayout LLayout1;
    String username;
    String password;
    String url;
    TextView enter_patient_id, message;
    Bundle extras;
    HashMap < String, String > scm_name_to_id;
    String[] sensor_name_hinting_array;
    ScrollView sv;
    static couch_api ca; //magus

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScrollView sv = new ScrollView(this);
        LLayout1 = new LinearLayout(this);
        LLayout1.setOrientation(LinearLayout.VERTICAL);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        sv.addView(LLayout1);
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
        ca = new couch_api(manager);
        if (ca == null) {
            System.out.println("Failed to Set CouchApi");
        }

        new Sensor_name_AsyncTask().execute(username, password, url);
        enter_patient_id = new TextView(this);
        enter_patient_id.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        enter_patient_id.setText("Enter Patient Id");
        LLayout1.addView(enter_patient_id);
        patient = new EditText(this);
        patient.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        LLayout1.addView(patient);
        setContentView(sv);
        new sensor_concept_mapping_AsyncTask().execute(username, password, url, extras.getString("Sensor"));

    }

    public void process_sensor_hinting() throws ClientProtocolException, IOException {
        JSONObject query = null;
        try {
            query = new JSONObject(HTTP_Functions.Httpget(username, password, url + "/ws/rest/v1/sensor/scm"));
        } catch (JSONException e1) {

            e1.printStackTrace();
        }
        JSONArray results = null;
        try {
            results = query.getJSONArray("results");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Iterates over SCMs JSON Array
        for (int it = 0; it < results.length(); it++) {
            JSONObject current_sensor = null;
            try {
                current_sensor = (JSONObject)((JSONObject) results.get(it)).get("sensor");
                scm_name_to_id.put(current_sensor.get("sensor_name").toString(), current_sensor.get("sensor_id").toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        Set < String > hinting_set = scm_name_to_id.keySet();
        sensor_name_hinting_array = hinting_set.toArray(new String[hinting_set.size()]);
    }

    public static String SensorName_JsonParse(String str) throws JSONException {
        JSONObject jo = new JSONObject(str);
        return jo.getString("sensor_name");

    }

    private class Sensor_name_AsyncTask extends AsyncTask < String, String, String > {
        String result = "";@
        Override
        protected String doInBackground(String...params) {

            try {
                result = SensorName_JsonParse(HTTP_Functions.Httpget(username, password, url + "/ws/rest/v1/sensor/sm/" + extras.getString("Sensor")));
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
            return result;

        }

        protected void onPostExecute(String params) {

            message = new TextView(Sensor_reading_manually_on_click.this);
            message.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            message.setText("Readings for sensor " + params);
            LLayout1.addView(message);

        }

    }

    public static String get_sensor_concepts_from_db(String id) {
        String str = "";
        Document retrievedDocument = ca.getSensor(id);
        if (retrievedDocument != null) {
            System.out.println("Using Sensor Found in Database");
            HashMap < String, Object > sensor_concepts = (HashMap < String, Object > )((Map) retrievedDocument.getProperty("sensor_concepts"));
            for (String key: sensor_concepts.keySet()) {
                System.out.println(key);
                str = str + key + "*";
            }
        } else {
            System.out.println("No Such Sensor Found with ID=" + id);
        }
        return str;
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
        String res = "";
        JSONObject jo = new JSONObject(str);
        JSONArray ja = jo.getJSONArray("concepts");
        if (ja.length() == 0) {

            return "zero";
        }
        int len = ja.length();
        System.out.println(len);
        for (int i = 0; i < len; i++) {
            JSONObject jo1 = (JSONObject) ja.get(i);
            res = res + (String) jo1.get("display") + "*";
            System.out.println(res);

        }

        return res;
    }

    public static String JsonParse2(String str) throws JSONException {
        String res = "";
        JSONObject jo = new JSONObject(str);
        JSONArray ja = jo.getJSONArray("concepts");
        if (ja.length() == 0) {

            return "zero";
        }
        int len = ja.length();
        System.out.println(len);
        for (int i = 0; i < len; i++) {
            JSONObject jo1 = (JSONObject) ja.get(i);
            res = res + (String) jo1.get("uuid") + "*";
            System.out.println(res);

        }

        return res;
    }

    private class Reading_Post_AsyncTask extends AsyncTask < String, String, String > //Marker
        {
            String result = "";@
            Override
            protected String doInBackground(String...params) {

                try {
                    ca.push_readings(username, password, url);
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

                return result;

            }

            protected void onPostExecute(String params) {
                try {
                    ca.getDatabase(ca.readings_db_name).getView(ca.pending_reading_view).createQuery().run();
                } catch (CouchbaseLiteException e) {
                    System.out.println("Couch Base Exception");
                    e.printStackTrace();
                }
                Integer rows_left = ca.getDatabase(ca.readings_db_name).getView(ca.pending_reading_view).dump().size();
                System.out.println("Done");
                System.out.println("Rows Left = " + rows_left);
                if (rows_left.equals(0)) {
                    Toast.makeText(getApplicationContext(), "Successfully Pushed", Toast.LENGTH_LONG).show();
                } else {
                    System.out.println("queue not empty yet");
                }
            }

        }

    private class sensor_concept_mapping_AsyncTask extends AsyncTask < String, String, String > {

        String result1, result2;

        protected String doInBackground(String...params) {
            result2 = get_sensor_concepts_from_db(params[3]); // params[3] has sensor_id
            System.out.println("result2 >>> " + result2);
            if (result2.length() == 0) {
                try {
                    result1 = (HTTP_Functions.Httpget(username, password, url + "/ws/rest/v1/sensor/scm/" + params[3]));
                    result2 = JsonParse(result1);
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
            }
            if (result2.equals("")) {
                return "zero";
            }
            return result2;
        }

        protected void onPostExecute(String params) {

            String arrayString[];
            EditText ed;
            final List < EditText > readings = new ArrayList < EditText > ();
            if (params.equals("zero")) {
                Toast.makeText(getApplicationContext(), "No Concepts found.", Toast.LENGTH_LONG).show();
            } else {

                arrayString = params.split("\\*");
                final int size = arrayString.length;
                for (int i = 0; i < size; i++) {
                    TextView rowTextView = new TextView(Sensor_reading_manually_on_click.this);
                    rowTextView.setText(arrayString[i]);
                    LLayout1.addView(rowTextView);
                    ed = new EditText(Sensor_reading_manually_on_click.this);
                    readings.add(ed);
                    ed.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
                    LLayout1.addView(ed);

                }

                b2 = new Button(Sensor_reading_manually_on_click.this);
                b2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                b2.setText("Post Readings");
                LLayout1.addView(b2);
                b2.setOnClickListener(new View.OnClickListener() {@
                    Override
                    public void onClick(View v) {
                        if (patient.getText().toString().length() < 1) {
                            Toast.makeText(getApplicationContext(), "Enter Complete Details", Toast.LENGTH_LONG).show();
                        } else {
                            String[] read = new String[size];
                            String readings_concat = "";
                            for (int i = 0; i < readings.size(); i++) {
                                read[i] = readings.get(i).getText().toString();
                                readings_concat = readings_concat + read[i] + "*";
                            }
                            final String send = readings_concat.substring(0, readings_concat.length() - 1);
                            query = extras.getString("Sensor");
                            System.out.println("query>>> " + query);
                            System.out.println("send>>>> " + send);
                            que(query, send);
                            Toast.makeText(getApplicationContext(), "Queued Successfully!", Toast.LENGTH_LONG).show();
                            new Reading_Post_AsyncTask().execute(username, password, url, query, send);
                        }

                    }
                });

            }
        }

        protected void onProgressUpdate(Integer...progress) {

        }
    }

    public void que(String sensor_id, String readings) {
        String res;
        Document retrievedDocument = ca.getSensor(sensor_id);
        if (retrievedDocument != null) {
            System.out.println("Using Sensor Found in Database");
        } else {
            System.out.println("Looking up Sensors online");
            retrievedDocument = ca.getSensor(sensor_id);
        }
        if (retrievedDocument != null) {
            HashMap < String, Object > sensor_concepts = (HashMap < String, Object > )((Map) retrievedDocument.getProperty("sensor_concepts"));
            res = new String("");
            for (String key: sensor_concepts.keySet()) {
                System.out.println(key);
                res = res + sensor_concepts.get(key) + "*";
            }

            String id_array[] = res.split("\\*");
            String reading_array[] = readings.split("\\*");
            //
            System.out.println("Running CouchBase Code");
            HashMap < String, Object > reading = new HashMap < String, Object > ();
            for (int i = 0; i < id_array.length; i++) {
                reading.put(id_array[i], reading_array[i]);
            }
            String pat = patient.getText().toString();
            ca.createDocument("reading", ca.makeReadingMap(pat, sensor_id, reading));
            //
        }
    }

}