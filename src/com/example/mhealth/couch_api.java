/**
 * 
 */
package com.example.mhealth;

/**
 * @author magus
 *
 */

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpResponse;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
//import android.provider.Settings.System;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;

import android.annotation.SuppressLint;
import android.os.StrictMode;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Manager;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.QueryRow;
import com.couchbase.lite.android.AndroidContext;
import com.couchbase.lite.util.Log;
     
//extends ActionBarActivity needed for AndroidContext .. what to do?
@SuppressLint("NewApi")
public class couch_api{
	final String TAG = "couch_db";
	Manager manager;
//	Database database;
	AndroidContext ac;
	
	couch_api(Manager manager){
		this.setManager(manager);
	}
	
	public Manager getManager() {
		if (this.manager == null){
			this.setManager();
		}
		return manager;
	}
	public void setManager(Manager manager){
		this.manager = manager;
	}
	public void setManager() {
		if (this.manager == null){
				Manager manager;
			try {
			    manager = new Manager(ac, Manager.DEFAULT_OPTIONS);
			    Log.d (TAG, "Manager created");
			} catch (IOException e) {
			    Log.e(TAG, "Cannot create manager object");
			    return;
			}
			this.manager = manager;
		}
	}
	
	public Database getDatabase(String dbname) {
//		setDatabase(dbname);
		if (!Manager.isValidDatabaseName(dbname)) {
		    Log.e(TAG, "Bad database name");
		    return null;
		}
		try {
			return manager.getDatabase(dbname);
		} catch (CouchbaseLiteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public void createDocument(String dbname,Map<String, Object> docContent){
		Log.d(TAG, "Writing in database="+dbname+" docContent=" + String.valueOf(docContent));
		Database database = getDatabase(dbname);
		System.out.println(database);
		Document document = database.createDocument();
		// add content to document and write the document to the database
		try {
		    document.putProperties(docContent);
		    Log.d (TAG, "Document written to database named " + dbname + " with ID = " + document.getId());
		} catch (CouchbaseLiteException e) {
		    Log.e(TAG, "Cannot write document to database", e);
		}
		// save the ID of the new document
		String docID = document.getId();
		// retrieve the document from the database
		Document retrievedDocument = database.getDocument(docID);
		// display the retrieved document
		Log.d(TAG, "Checking if written by retrieving , retrievedDocument:\n" + String.valueOf(retrievedDocument.getProperties()));              
	}
	
	public QueryEnumerator getAllDocument(String dbname){
		Database database = getDatabase(dbname);
		Query query = database.createAllDocumentsQuery();
		QueryEnumerator result = null;
		try {
			result = query.run();
		} catch (CouchbaseLiteException e) {
			e.printStackTrace();
		}
		Log.d(TAG, TAG + " Iterating ");
		for (Iterator<QueryRow> it = result; it.hasNext(); ) {
		    QueryRow row = it.next();
		    Log.d(TAG, TAG + " row = "+row);
		}
		return result;
	}
	
	public void getAllDocument(){
		String[] dbs = {"sensor","concept","patient","reading"};
		for (String dbname : dbs){
			Log.d(TAG, TAG + " Data for DB="+ dbname);
			Database database = getDatabase(dbname);
			Query query = database.createAllDocumentsQuery();
			QueryEnumerator result = null;
			try {
				result = query.run();
			} catch (CouchbaseLiteException e) {
				e.printStackTrace();
			}
			Log.d(TAG, TAG + " Iterating ");
			for (Iterator<QueryRow> it = result; it.hasNext(); ) {
			    QueryRow row = it.next();
			    Log.d(TAG, TAG + " row = "+row);
			}
		}
	}	
	//Concept Document = {id:5090 , uuid:asda-asdasd ,name:”asdaSD” }
	public Map<String, Object> makeConceptMap(Integer concept_id,String concept_uuid,String concept_name){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("concept_id", concept_id);
		map.put("concept_uuid", concept_uuid);
		map.put("concept_name", concept_name);		
		return map;
	}
	
	//Sensor Document = {id:12 , name:”BPReader” concepts:[uuid,uuid] }
	public Map<String, Object> makeSensorMap(Integer sensor_id,String sensor_name,Set<String> sensor_concepts){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("sensor_id", sensor_id);
		map.put("sensor_name", sensor_name);
		map.put("sensor_concepts", sensor_concepts);		
		return map;
	}
	
	//Patient Document = {id = 123, name = “asd”}
	public Map<String, Object> makePatientMap(Integer patient_id,String patient_name){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("patient_id", patient_id);
		map.put("patient_name", patient_name);		
		return map;
	}
	
	//Reading Document = {patient = “1” sensor = 12 readings : {5090 = 12, 567 = 134}};
	public Map<String, Object> makeReadingMap(Integer patient_id,Integer sensor_id,HashMap<String, Object> readings){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("patient", patient_id);
		map.put("sensor", sensor_id);	
		map.put("readings", readings);			
		return map;
	}
	
	public void syncSensors(String username, String password, String url){
//		TODO:Remove This
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		
		System.out.println("Username="+username);
		System.out.println("Password="+password);
		System.out.println("URL="+url);
		
		
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(url+"/ws/rest/v1/sensor/scm");
		httpGet.addHeader(BasicScheme.authenticate(
		 new UsernamePasswordCredentials(username, password),
		 "UTF-8", false));

		HttpResponse httpResponse = null;
		try {
			httpResponse = httpClient.execute(httpGet);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			System.out.println(httpResponse.getEntity().getContent());
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int status = httpResponse.getStatusLine().getStatusCode();
	}
	
	//Log.d(TAG, "Begin Hello World App");	
}