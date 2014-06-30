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

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
//import android.provider.Settings.System;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Emitter;
import com.couchbase.lite.Manager;
import com.couchbase.lite.Mapper;
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
	public Map<String, Object> makeSensorMap(Integer sensor_id,String sensor_name,HashMap<String,String> sensor_concepts){
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
//		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//		StrictMode.setThreadPolicy(policy);
		
		//Flush
		Database database = getDatabase("sensor");
		System.out.println("Flushing Documents = "+database.getDocumentCount());
		try {
			database.delete();
		} catch (CouchbaseLiteException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		database = getDatabase("sensor");
		System.out.println("Document Count = "+database.getDocumentCount());
		//
		
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
		String str = null;
		try {
			HttpEntity responseEntity = httpResponse.getEntity();
			str = GetQuery.inputStreamToString(responseEntity.getContent()).toString();
			httpClient.getConnectionManager().shutdown();
//			System.out.println(str);
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try{
			JSONObject j = new JSONObject(str);
			JSONArray ja = (JSONArray) j.get("results");
			for (int it = 0 ; it < ja.length(); it++) {
				//System.out.println(ja.get(it));
				JSONObject scm = (JSONObject) ja.get(it);
				Integer sensor_id =  (Integer) ((JSONObject) scm.get("sensor")).get("sensor_id") ;
				String sensor_name = (String) ((JSONObject) scm.get("sensor")).get("sensor_name");
				JSONArray concepts_array = (JSONArray) scm.get("concepts");
				HashMap<String,String> concepts = new HashMap<String,String>();
				for (int i_ca = 0 ; i_ca < concepts_array.length(); i_ca++) {
					//display":"BANDS","uuid":"576829b5-ddf8-11e3-b4c4-a0b3cc71229c"}]}
					JSONObject concept = (JSONObject) concepts_array.get(i_ca);
					concepts.put((String)concept.get("display"),(String) concept.get("uuid"));
				}
				System.out.println("Adding following SCM to LocalStore"+ sensor_id+sensor_name+concepts);
				this.createDocument("sensor", this.makeSensorMap(sensor_id,sensor_name,concepts));
			}
		}
		catch(Exception e){
			
		}
	}
	public void UpdateorCreateViews(){
		Database database = getDatabase("sensor");
		com.couchbase.lite.View sntoi = database.getView("sensor_name_to_id");
		sntoi.setMap(
			new Mapper() {
		    @Override
		    public void map(Map<String, Object> document, Emitter emitter) {
		    	String sn =(String) document.get("sensor_name");
		    	Integer si =(Integer) document.get("sensor_id");
				Log.d(TAG, TAG+" sn : "+ sn +" si : "+ si);
				emitter.emit(sn, si);						
		    }

		}, "1");		
	}
	// return Map<String,Object> ? 
	public Document getSensor(String sensor_name){
		Database database = getDatabase("sensor");
		Query query_sr = database.getView("sensor_name_to_id").createQuery();
//		query_sr.setLimit(20);
		query_sr.setStartKey(sensor_name);
//		query_sr.setEndKey("13");
		
		QueryEnumerator result_sr = null;
		try {
			result_sr = query_sr.run();
		} catch (CouchbaseLiteException e) {
			e.printStackTrace();
		}
		Iterator<QueryRow> it = result_sr;
		QueryRow row = it.next();
		Log.d(TAG, TAG + " row = "+row);
		System.out.println(row);
		Document d = database.getDocument(row.getDocumentId());
		System.out.println("Retrieved Document "+ String.valueOf(d.getProperties()));
		return d; 
		// use d using "d.getProperty(key)"
	}
	//Log.d(TAG, "Begin Hello World App");	
}
