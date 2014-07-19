package com.example.mhealth;

import java.io.IOException;
import org.apache.http.HttpResponse;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import android.content.SharedPreferences;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends ActionBarActivity implements View.OnClickListener {

    EditText user;
    EditText pwd;
    EditText address;
    Button button;
    public String username;
    public String password;
    public String url;

    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.first_act);
        user = (EditText) findViewById(R.id.editText1);
        pwd = (EditText) findViewById(R.id.editText2);
        address = (EditText) findViewById(R.id.editText4);

        SharedPreferences sharedPref = getSharedPreferences("mhealth", Context.MODE_PRIVATE);
        String use = sharedPref.getString(getString(R.string.username), "");
        String pass = sharedPref.getString(getString(R.string.password), "");
        String uri = sharedPref.getString(getString(R.string.url), "");

        user.setText(use);
        pwd.setText(pass);
        address.setText(uri);

        button = (Button) findViewById(R.id.button1);
        button.setOnClickListener(this);

    }

    public void buttonClick() {
        username = (user.getText().toString());
        password = (pwd.getText().toString());
        url = (address.getText().toString());
        SharedPreferences sharedPref = getSharedPreferences("mhealth", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.username), username);
        editor.putString(getString(R.string.password), password);
        editor.putString(getString(R.string.url), url);
        editor.commit();
        Toast.makeText(this, "Settings Saved!", Toast.LENGTH_LONG).show();

    }

    public void SwitchIntent() {
        Intent intent = new Intent(this, Options.class);
        startActivity(intent);
    }

    public static int Check_Credentials_Httpget(String username, String password, String url) throws ClientProtocolException, IOException, HttpHostConnectException {
        HttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(url + "/ws/rest/v1/patientidentifiertype");
        httpGet.addHeader(BasicScheme.authenticate(new UsernamePasswordCredentials(username, password), "UTF-8", false));
        HttpResponse httpResponse = httpClient.execute(httpGet);
        int status = httpResponse.getStatusLine().getStatusCode();
        return status;

    }

    private class Check_Credentials_AsyncTask extends AsyncTask < String, String, String > {

        int status_code;@
        Override
        protected String doInBackground(String...params) {

            try {

                status_code = (Check_Credentials_Httpget(params[0], params[1], params[2]));
                if (status_code == 401)
                    return "cred";
                else if (status_code == 404)
                    return "invalid";
                else if (status_code == 200)
                    return "successful";

            } catch (HttpHostConnectException e) {
                System.out.println("HTTP Host Connect exception caught.");
                e.printStackTrace();
                return "refused";
            } catch (IllegalStateException e) {
                System.out.println("Illegal State exception caught.");
                e.printStackTrace();
                return "no";
            } catch (ClientProtocolException e) {
                System.out.println("Client Protocol exception caught.");
                e.printStackTrace();
            } catch (IOException e) {
                System.out.println("IO exception caught.");
                e.printStackTrace();
            }

            return "quit";
        }

        protected void onPostExecute(String params) {

            if (params.equals("cred")) {
                Toast.makeText(getApplicationContext(), "Invalid credentials", Toast.LENGTH_LONG).show();
            } else if (params.equals("invalid")) {
                Toast.makeText(getApplicationContext(), "Invalid URL", Toast.LENGTH_LONG).show();
            } else if (params.equals("refused")) {
                // TODO : reduce timeout of http , currently below message gets shown after two minutes when offline
                Toast.makeText(getApplicationContext(), "Connection refused: Wrong URL or server  connection error", Toast.LENGTH_LONG).show();
            } else if (params.equals("successful")) {
                Toast.makeText(getApplicationContext(), "Settings Verified", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "Unable to Connect , Please Reverify when network comes!", Toast.LENGTH_LONG).show();
            }
        }

        protected void onProgressUpdate(Integer...progress) {

        }
    }

    public void onClick(View v) {
        if (user.getText().toString().length() < 1 || pwd.getText().toString().length() < 1 || address.getText().toString().length() < 1) {

            // out of range
            Toast.makeText(this, "please enter complete details", Toast.LENGTH_LONG).show();
        } else {
            switch (v.getId()) {
            case R.id.button1:
                buttonClick();

                username = (user.getText().toString());
                password = (pwd.getText().toString());
                url = (address.getText().toString());

                new Check_Credentials_AsyncTask().execute(username, password, url);
                break;

            }

        }
    }
}
