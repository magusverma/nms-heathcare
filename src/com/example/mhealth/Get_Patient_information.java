package com.example.mhealth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import utils.HTTP.HTTP_Functions;

import com.couchbase.lite.Manager;
import com.couchbase.lite.android.AndroidContext;
//import com.example.mhealth.Login.MyAsyncTask;

import android.support.v7.app.ActionBarActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class Get_Patient_information extends ActionBarActivity implements View.OnClickListener {

    Button get;
    AutoCompleteTextView patient_name; //magus
    String query;
    ProgressBar pb;
    String username;
    String password;
    String url;
    TextView num;
    TextView name;
    TextView uuid;
    TextView age, gender, birthdate;
    public Patient patient = new Patient();
    static couch_api ca; //magus

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.get_layout);
        get = (Button) findViewById(R.id.button1);
        patient_name = (AutoCompleteTextView) findViewById(R.id.editText2); //magus
        num = (TextView) findViewById(R.id.textView7);
        name = (TextView) findViewById(R.id.textView9);
        age = (TextView) findViewById(R.id.textView13);
        gender = (TextView) findViewById(R.id.textView15);
        birthdate = (TextView) findViewById(R.id.textView17);
        Bundle extras = getIntent().getExtras();
        SharedPreferences sharedPref = getSharedPreferences("mhealth", Context.MODE_PRIVATE);
        username = sharedPref.getString(getString(R.string.username), "");
        password = sharedPref.getString(getString(R.string.password), "");
        url = sharedPref.getString(getString(R.string.url), "");
        System.out.println(username + " " + password + " " + url + " GetQuery");
        get.setOnClickListener(this);
        System.out.println("before manager");
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

    }

    public String jsonParse(String str) throws JSONException {
        String info;
        String Id;
        String name;
        String uuid;
        JSONObject obj = new JSONObject(str);
        JSONArray ja = obj.getJSONArray("results");
        if (ja.length() == 0) {
            return "zero";
        } else {
            System.out.println(str);
            JSONObject j1 = (JSONObject) ja.get(0);
            info = (String) j1.get("display");
            uuid = (String) j1.get("uuid");
            patient.setIdentifier(uuid);
            String stri[] = info.split(" - ");
            String temp = stri[0];
            Id = temp;
            patient.setPatientId(Id);
            name = stri[1];
            System.out.println(name);
            String[] name_split = name.split("\\s+");

            if (name_split.length == 1) {
                patient.setGivenName(name_split[0]);
            } else if (name_split.length == 2) {
                patient.setGivenName(name_split[0]);
                patient.setFamilyName(name_split[1]);
            } else {
                patient.setGivenName(name_split[0]);
                patient.setMiddleName(name_split[1]);
                patient.setFamilyName(name_split[2]);
            }

            return Id + "*" + name + "*" + uuid;
        }

    }

    public String DetailsjsonParse(String str) throws JSONException {
        JSONObject obj = new JSONObject(str);
        String gender, birthdate;
        Integer age;
        gender = obj.getString("gender");
        patient.setGender(gender);
        age = obj.getInt("age");
        patient.setAge(age);
        birthdate = obj.getString("birthdate");
        patient.setBirthDate(birthdate);
        return patient.getAge() + "*" + patient.getGender() + "*" + patient.getBirthdate();

    }

    private class Patient_Get_AsyncTask extends AsyncTask < String, String, String > {

        String result1, result2;@
        Override
        protected String doInBackground(String...params) {
            HTTP_Functions http = new HTTP_Functions();
            try {

                result1 = jsonParse(http.Httpget(params[0], params[1], url + "/ws/rest/v1/person?q=" + params[3]));
                if (result1.equals("zero")) {
                    return "zero";
                } else {
                    String details[] = result1.split("\\*");
                    result2 = DetailsjsonParse(http.Httpget(params[0], params[1], url + "/ws/rest/v1/patient/" + details[2] + "?v=custom:(gender,age,birthdate)"));
                }

            } catch (ClientProtocolException e) {
                System.out.println("Client Protocol exception caught.");
                e.printStackTrace();
            } catch (IOException e) {
                System.out.println("IO exception caught.");
                e.printStackTrace();
                return "boo";
            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.println("ArrayIndexOutOfBoundsException");
                e.printStackTrace();
                return "too";
            } catch (JSONException e) {
                System.out.println("JSON exception caught.");
                e.printStackTrace();
            }

            return result1 + "*" + result2;
        }

        protected void onPostExecute(String params) {

            if (params.equals("zero")) {
                Toast.makeText(getApplicationContext(), "No patients found.", Toast.LENGTH_LONG).show();
            } else if (params.equals("boo")) {
                Toast.makeText(getApplicationContext(), "Kindly check your Internet Connection, Settings and Server.", Toast.LENGTH_LONG).show();
            } else if (params.equals("too")) {
                Toast.makeText(getApplicationContext(), "Please enter correct and full name.", Toast.LENGTH_LONG).show();
            } else {
                String arrayString[] = params.split("\\*");
                num.setText(arrayString[0]);
                name.setText(arrayString[1]);
                age.setText(arrayString[3]);
                gender.setText(arrayString[4]);
                birthdate.setText(arrayString[5]);

                //TODO Move this to some place appropriate as well
                String patient_id = arrayString[2];
                String patient_name = arrayString[1];

                if (ca.getPatientId(patient_name) == null) {
                    ca.createDocument(ca.patients_db_name, ca.makePatientMap(patient_id, patient_name));
                    Toast.makeText(getApplicationContext(), "Reading for this patient can now be taken", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "This patient is already added to list with id = " + ca.getPatientId(patient_name), Toast.LENGTH_LONG).show();
                }
            }
        }

        protected void onProgressUpdate(Integer...progress) {

        }
    }

    @Override
    public void onClick(View v) {
        //query=q.getText().toString();
        query = patient_name.getText().toString();

        switch (v.getId()) {
        case R.id.button1:
            if (query.equalsIgnoreCase("u")) {
                Toast.makeText(getApplicationContext(), "Invalid Argument", Toast.LENGTH_LONG).show();
            } else if (patient_name.getText().toString().length() < 1) {

                // out of range
                Toast.makeText(this, "Please enter complete details", Toast.LENGTH_LONG).show();
            } else {
                new Patient_Get_AsyncTask().execute(username, password, url, query);
            }
            //Toast.makeText(getApplicationContext(), number+" "+full_name+" "+Uuid, Toast.LENGTH_LONG).show();
            break;

        }

    }

}
