package com.example.mhealth;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.os.Build;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
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

import utils.HTTP.*;

public class Create_Patient extends ActionBarActivity implements View.OnClickListener {

    EditText first_name, family_name, openmrs_id, gender, age;
    Button button;
    String username;
    String password;
    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_layout);
        first_name = (EditText) findViewById(R.id.editText3);
        family_name = (EditText) findViewById(R.id.editText4);
        gender = (EditText) findViewById(R.id.editText5);
        openmrs_id = (EditText) findViewById(R.id.editText6);
        age = (EditText) findViewById(R.id.editText7);
        button = (Button) findViewById(R.id.button5);
        Bundle extras = getIntent().getExtras();
        SharedPreferences sharedPref = getSharedPreferences("mhealth", Context.MODE_PRIVATE);
        username = sharedPref.getString(getString(R.string.username), "");
        password = sharedPref.getString(getString(R.string.password), "");
        url = sharedPref.getString(getString(R.string.url), "");
        button.setOnClickListener(this);

    }

    public static String JsonParse(String str) throws JSONException {
        JSONObject jo = new JSONObject(str);
        JSONArray ja = jo.getJSONArray("results");
        JSONObject j1 = (JSONObject) ja.get(0);
        String uuid = (String) j1.get("uuid");
        return uuid;

    }

    public static String uuidJsonParse(String str) throws JSONException {

        JSONObject obj = new JSONObject(str);
        String uuid = obj.getString("uuid");
        return uuid;

    }

    public static String create_patient(String username, String password, String url, String givenName, String familyName, String Id, String gender, int num) throws JSONException, ClientProtocolException, IOException {
        JSONObject nm = new JSONObject();
        nm.put("givenName", givenName);
        nm.put("familyName", familyName);

        JSONArray ja = new JSONArray();
        ja.put(nm);

        JSONObject json = new JSONObject();
        json.put("gender", gender);
        json.put("names", ja);
        json.put("age", num);
        //json.put("preferredAddress", address);

        String result;
        HTTP_Functions http = new HTTP_Functions();
        result = http.Httppost(username, password, url + "/ws/rest/v1/person", json);

        String uuid = uuidJsonParse(result);
        String idType = JsonParse(http.Httpget(username, password, url + "/ws/rest/v1/patientidentifiertype"));
        String location = JsonParse(http.Httpget(username, password, url + "/ws/rest/v1/location"));

        JSONObject jObject = new JSONObject();
        JSONObject identifier = new JSONObject();
        identifier.put("identifier", Id);
        identifier.put("identifierType", idType);
        identifier.put("location", location);
        identifier.put("preferred", true);

        JSONArray jArray = new JSONArray();
        jArray.put(identifier);
        jObject.put("identifiers", jArray);
        jObject.put("person", uuid);

        String final_string;
        HTTP_Functions http = new HTTP_Functions();
        final_string = http.Httppost(username, password, url + "/ws/rest/v1/patient", jObject);
        return final_string;

    }

    public class Create_patient_AsyncTask extends AsyncTask < String, String, String > {

        String result;
        @Override
        protected String doInBackground(String...params) {

            try {
                Integer age = Integer.parseInt(params[7]);
                result = create_patient(params[0], params[1], params[2], params[3], params[4], params[5], params[6], age);

            } catch (HttpResponseException e) {
                System.out.println("Http Response exception caught.");
                e.printStackTrace();
                return "no";
            } catch (ClientProtocolException e) {
                System.out.println("Client Protocol exception caught.");
                e.printStackTrace();
                return "failed";
            } catch (IOException e) {
                System.out.println("IO exception caught.");
                e.printStackTrace();
                return "failed";
            } catch (JSONException e) {
                System.out.println("JSON exception caught.");
                e.printStackTrace();
                return "failed";
            } catch (NullPointerException e) {
                System.out.println("No internet Connection");
                return "failed";
            }
            return result;
        }

        protected void onPostExecute(String params) {

            if (params.equals("no")) {
                Toast.makeText(getApplicationContext(), "Patient could not be created\nID should be unique!!", Toast.LENGTH_LONG).show();
            } else if (params.equals("failed")) {
                Toast.makeText(getApplicationContext(), "Patient could not be created!! Check your settings and connection.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "Patient Created Successfully!!", Toast.LENGTH_LONG).show();
            }

        }

        protected void onProgressUpdate(Integer...progress) {

        }
    }

    @
    Override
    public void onClick(View v) {

        String name, last, gen, Id, Age;
        name = first_name.getText().toString();
        last = family_name.getText().toString();
        gen = gender.getText().toString();
        Id = openmrs_id.getText().toString();
        Age = age.getText().toString();

        switch (v.getId()) {
        case R.id.button5:
            //Toast.makeText(getApplicationContext(), "create selected", Toast.LENGTH_LONG).show();

            if (name.length() < 1 || last.length() < 1 || gen.length() < 1 || Id.length() < 1 || Age.length() < 1) {
                Toast.makeText(getApplicationContext(), "Enter complete details", Toast.LENGTH_LONG).show();

            } else {

                new Create_patient_AsyncTask().execute(username, password, url, name, last, Id, gen, Age);
                //Toast.makeText(getApplicationContext(), number+" "+full_name+" "+Uuid, Toast.LENGTH_LONG).show();
                break;
            }

        }
    }

}
