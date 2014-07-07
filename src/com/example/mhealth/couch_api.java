/**
 * 
 */
package com.example.mhealth;

/**
 * @author magus
 *
 */

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
//import android.provider.Settings.System;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
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
	final String sensors_db_name = "sensor" ;
	final String concepts_db_name = "concept" ;
	final String patients_db_name = "patient" ;
	final String readings_db_name = "reading" ;
	final String sensor_view = "sensor_view" ;
	final String pending_reading_view = "pending_reading" ;
	final String concepts_view = "concepts_view" ;
	
//	String[] dbs = {"sensor","concept","patient","reading"};
	
	couch_api(Manager manager){
		this.setManager(manager);
		this.UpdateorCreateViews();
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
		//Log.d(TAG, "Writing in database="+dbname+" docContent=" + String.valueOf(docContent));
		Database database = getDatabase(dbname);
		//System.out.println(database);
		Document document = database.createDocument();
		// add content to document and write the document to the database
		try {
		    document.putProperties(docContent);
		  //  Log.d (TAG, "Document written to database named " + dbname + " with ID = " + document.getId());
		} catch (CouchbaseLiteException e) {
		    Log.e(TAG, "Cannot write document to database", e);
		}
		// save the ID of the new document
		//String docID = document.getId();
		// retrieve the document from the database
		//Document retrievedDocument = database.getDocument(docID);
		// display the retrieved document
		//Log.d(TAG, "Checking if written by retrieving , retrievedDocument:\n" + String.valueOf(retrievedDocument.getProperties()));              
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
	public void clearAllDatabases(){
		String[] dbs = {sensors_db_name,patients_db_name,readings_db_name};//,"concept"
		for (String dbname : dbs){
			Log.d(TAG, TAG + " Clearing DB="+ dbname);
			Database database = getDatabase(dbname);
			try {
				database.delete();
			} catch (CouchbaseLiteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public void getAllDocument(){
		String[] dbs = {sensors_db_name,concepts_db_name,patients_db_name,readings_db_name};
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
	public Map<String, Object> makeConceptMap(String concept_id,String concept_name){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("concept_id", concept_id);
		map.put("concept_name", concept_name);		
		return map;
	}
	//Sensor Document = {id:12 , name:”BPReader” concepts:{name:uuid ..} }
	public Map<String, Object> makeSensorMap(String sensor_id,String sensor_name,HashMap<String,String> sensor_concepts){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("sensor_id", sensor_id);
		map.put("sensor_name", sensor_name);
		map.put("sensor_concepts", sensor_concepts);		
		return map;
	}
	//Patient Document = {id = 123, name = “asd”}
	public Map<String, Object> makePatientMap(String patient_id,String patient_name){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("patient_id", patient_id);
		map.put("patient_name", patient_name);		
		return map;
	}
	//Reading Document = {patient = “1” sensor = 12 readings : {5090 = 12, 567 = 134}};
	public Map<String, Object> makeReadingMap(String patient_id,String sensor_id,HashMap<String, Object> readings){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("patient", patient_id);
		map.put("sensor", sensor_id);	
		map.put("readings", readings);			
		map.put("status", "pending");
		return map;
	}
	public Map<String, Object> makeReadingMap(String patient_id,String sensor_id,HashMap<String, Object> readings,String status){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("patient", patient_id);
		map.put("sensor", sensor_id);	
		map.put("readings", readings);			
		map.put("status", status);
		return map;
	}
	public Integer syncSensors(String username, String password, String url){
//		TODO:Remove This
//		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//		StrictMode.setThreadPolicy(policy);
		
		//Flush
		Database database = getDatabase(sensors_db_name);
		System.out.println("Flushing Documents = "+database.getDocumentCount());
		try {
			database.delete();
		} catch (CouchbaseLiteException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		database = getDatabase(sensors_db_name);
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
				String sensor_id =  ((JSONObject) scm.get("sensor")).get("sensor_id").toString() ;
				String sensor_name = ((JSONObject) scm.get("sensor")).get("sensor_name").toString();
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
		return database.getDocumentCount();
	}
	public void UpdateorCreateViews(){
		Database database = getDatabase(sensors_db_name);
		com.couchbase.lite.View sv = database.getView(sensor_view);
		sv.setMap(
			new Mapper() {
		    @Override
		    public void map(Map<String, Object> document, Emitter emitter) {
		    	String sn =document.get("sensor_name").toString();
		    	String si =document.get("sensor_id").toString();
//				Log.d(TAG, TAG+" sn : "+ sn +" si : "+ si);
				emitter.emit(si, document);						
		    }

		}, "2");
		
		Database reading_db = getDatabase(readings_db_name);
		com.couchbase.lite.View pending_reading = reading_db.getView(pending_reading_view);
		pending_reading.setMap(
			new Mapper() {
		    @Override
		    public void map(Map<String, Object> document, Emitter emitter) {
		    	if(document.get("status").equals("pending")){
		    		System.out.println("Pending Reading of Sensor: "+document.get("sensor"));
		    		System.out.println("Emitting "+document.get("sensor")+document);
		    		emitter.emit(document.get("sensor"), document);		
		    	}		    					
		    }
		}, "2");	
		
		Database concept_db = getDatabase(concepts_db_name);
		com.couchbase.lite.View concepts = concept_db.getView(concepts_view);
		concepts.setMap(
			new Mapper() {
		    @Override
		    public void map(Map<String, Object> document, Emitter emitter) {
		    	emitter.emit(document.get("concept_id"), document.get("concept_name"));	
		    }
		}, "1");	
		
	}
	// return Map<String,Object> ? 
	public Document getSensor(String sensor_id){
		System.out.println(sensor_id);
		Database database = getDatabase(sensors_db_name);
		Query query_sr = database.getView(sensor_view).createQuery();
//		query_sr.setLimit(20);
		query_sr.setStartKey(sensor_id);
		query_sr.setEndKey(sensor_id);
//		query_sr.setEndKey("13");
		
		QueryEnumerator result_sr = null;
		try {
			result_sr = query_sr.run();
		} catch (CouchbaseLiteException e) {
			e.printStackTrace();
		}
		Iterator<QueryRow> it = result_sr;
		if(it.hasNext()){
			QueryRow row = it.next();
	//		Log.d(TAG, TAG + " row = "+row);
			System.out.println(row);
			
			Document d = database.getDocument(row.getDocumentId());
			System.out.println("Search Result Document "+ String.valueOf(d.getProperties()));
			return d;
		}
		else {
			System.out.println("No Document Found");
			return null;
		}
		// use d using "d.getProperty(key)"
	}
	public Integer push_readings(String username,String password,String uri){
		Integer pushed = new Integer(0);
		Database database = getDatabase(readings_db_name);
		com.couchbase.lite.View v = database.getView("pending_reading");
		try {
			v.createQuery().run();// refreshes view 
		} catch (CouchbaseLiteException e1) {
			e1.printStackTrace();
		}  
	    System.out.println(v.dump());
		List<Map<String,Object>> dumpResult = v.dump();
		for(Map<String,Object> map : dumpResult){
			try {
				JSONObject json_being_pushed = new JSONObject(map.get("value").toString());
				Document document_being_pushed = database.getDocument(json_being_pushed.get("_id").toString());
				json_being_pushed.remove("_id");
				json_being_pushed.remove("_rev");
				json_being_pushed.remove("status");
				
				System.out.println(" Pusing Reading : "+json_being_pushed);
				Map<String, Object> updateProperties = new HashMap<String, Object>();
				updateProperties.putAll(document_being_pushed.getProperties());
				
				System.out.println(http_reading_post_request(username,password,uri,json_being_pushed));
				
				updateProperties.put("status", "pushed");
				document_being_pushed.putProperties(updateProperties);
			    Log.d(TAG, "updated retrievedDocument=" + String.valueOf(document_being_pushed.getProperties()));
			    pushed += 1;
			} catch (JSONException e) {
				System.out.println("Couldn't Create JSON Object for Pending Reading");
				e.printStackTrace();
			}		
			catch (CouchbaseLiteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return pushed;
	}
	
	public String get_view_data(String dbname,String view_name){
		UpdateorCreateViews();
		Database database = getDatabase(dbname);
		com.couchbase.lite.View v = database.getView(view_name);
		try {
			v.createQuery().run();// refreshes view 
		} catch (CouchbaseLiteException e1) {
			e1.printStackTrace();
		}  
		
		// 
		//getSensor("2");
		//
		//
			
		//
	    return v.dump().toString();		 
	}
	
	
	public String http_reading_post_request(String username,String password,String uri,JSONObject obj){
		//uri = uri+ "/module/sensorreading/sr.form";
		System.out.println(uri);
		obj.remove("status");
		System.out.println(obj);
		HttpClient httpClient = new DefaultHttpClient();
		ResponseHandler<String> resonseHandler = new BasicResponseHandler();
		HttpPost postMethod = new HttpPost(uri+"/module/sensorreading/sr.form");    
		try {
			postMethod.setEntity(new StringEntity(obj.toString()));
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		postMethod.setHeader( "Content-Type", "application/json");
		postMethod.addHeader(BasicScheme.authenticate(new UsernamePasswordCredentials(username,password),"UTF-8", false));
		String response = null;
		try{
			 response = httpClient.execute(postMethod,resonseHandler);
		}catch(Exception e){
			System.out.println("caught exception");
			e.printStackTrace();
		}
		return response;
	}
	//Log.d(TAG, "Begin Hello World App");	
}
