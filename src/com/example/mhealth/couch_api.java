/**
 * 
 */
package com.example.mhealth;

/**
 * @author magus
 * couch_api is helper class couchbase db related work
 * it contains required string methods for creating new documents
 * Create an instance of this class for using it , sample code
 * couch_api ca;
 * ...
 * Manager manager = null;
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
 */

import java.io.IOException;

import utils.HTTP.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
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
	
	// Tag for logger 
	final String TAG = "couch_db";
	// Database Handling needs a manager
	Manager manager;
	// Context is needed for knowing about the current app's couch store location
	AndroidContext ac;

	// Database and Views Names Strings
	final String sensors_db_name = "sensor" ;
	final String concepts_db_name = "concept" ;
	final String patients_db_name = "patient" ;
	final String readings_db_name = "reading" ;
	final String sensor_view = "sensor_view" ;
	final String pending_reading_view = "pending_reading" ;
	final String concepts_view = "concepts_view" ;
	final String patients_view = "patients_view" ;
	
	// Default Instructor to use , recommended to use this
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
	
	/* 
	 * TODO to make things simpler I was trying to make a constructor which fetches on its own 
	 * but this is not working  
	 */
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
	
	/*
	 * Pass Database Name as one of the Strings defined as final above
	 */
	public Database getDatabase(String dbname) {
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
	
	/*
	 * Document is an entity in Database , For pushing data in Database document must be created
	 * sample usage
	 * ca.createDocument(ca.concepts_db_name, ca.makeConceptMap(concept_id, concept_name));
	 * ca.createDocument(ca.patients_db_name, ca.makePatientMap(patient_id, patient_name));
	 */
	public void createDocument(String dbname,Map<String, Object> docContent){
		Database database = getDatabase(dbname);
		Document document = database.createDocument();
		
		// add content to document and write the document to the database
		try {
		    document.putProperties(docContent);
		  //  Log.d (TAG, "Document written to database named " + dbname + " with ID = " + document.getId());
		} catch (CouchbaseLiteException e) {
		    Log.e(TAG, "Cannot write document to database", e);
		}
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
	
	/* 
	 * For fetching data of any particular view's data 
	 */
	public String get_view_data(String dbname,String view_name){
		UpdateorCreateViews();
		Database database = getDatabase(dbname);
		com.couchbase.lite.View v = database.getView(view_name);
		try {
			v.createQuery().run();// refreshes view 
		} catch (CouchbaseLiteException e1) {
			e1.printStackTrace();
		}  
		
	    return v.dump().toString();		 
	}
	//Concept Map Generator {concept_id:"5090" , concept_name:”Sensor Name” }
	public Map<String, Object> makeConceptMap(String concept_id,String concept_name){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("concept_id", concept_id);
		map.put("concept_name", concept_name);		
		return map;
	}
	
	//Sensor Map Generator {sensor_id:12 , sensor_name:”BPReader” sensor_concepts:{name:id ..} }
	public Map<String, Object> makeSensorMap(String sensor_id,String sensor_name,HashMap<String,String> sensor_concepts){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("sensor_id", sensor_id);
		map.put("sensor_name", sensor_name);
		map.put("sensor_concepts", sensor_concepts);		
		return map;
	}
	
	//Patient Map Generator {patient_id = 123, patient_name = “asd”}
	public Map<String, Object> makePatientMap(String patient_id,String patient_name){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("patient_id", patient_id);
		map.put("patient_name", patient_name);		
		return map;
	}
	
	//Reading Document = {patient = “1” sensor = "12" readings : {"5090" = "12", "567" = "134"}};
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
	
	// 
	/*
	 * This method needs to be run only once , it is already called from constructor of this class so the 
	 * implementer doesn't need to bother about this
	 */
	public void UpdateorCreateViews(){
		Database database = getDatabase(sensors_db_name);
		com.couchbase.lite.View sv = database.getView(sensor_view);
		sv.setMap(
			new Mapper() {
		    @Override
		    public void map(Map<String, Object> document, Emitter emitter) {
		    	String sn =document.get("sensor_name").toString();
		    	String si =document.get("sensor_id").toString();
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
		
		Database patient_db = getDatabase(patients_db_name);
		com.couchbase.lite.View patients = patient_db.getView(patients_view);
		patients.setMap(
			new Mapper() {
		    @Override
		    public void map(Map<String, Object> document, Emitter emitter) {
		    	emitter.emit(document.get("patient_name"), document.get("patient_id"));	
		    }
		}, "1");	
		
	}
	
	/*
	 * returns patient name for a patient id by looking up patient in database
	 */
	public String getPatientId(String patient_name){
		String patient_id = null;
		Database database = getDatabase(patients_db_name);
		Query query_sr = database.getView(patients_view).createQuery();
		query_sr.setStartKey(patient_name);
		query_sr.setEndKey(patient_name);
		
		QueryEnumerator result_sr = null;
		try {
			result_sr = query_sr.run();
		} catch (CouchbaseLiteException e) {
			e.printStackTrace();
		}
		Iterator<QueryRow> it = result_sr;
		if(it.hasNext()){
			QueryRow row = it.next();//n = 10000;
			System.out.println(row);
			
			Document d = database.getDocument(row.getDocumentId());
			System.out.println("Search Result Document "+ String.valueOf(d.getProperties()));
			System.out.println("Returning:"+d.getProperty("patient_id")+" for "+patient_name);
			return (String) d.getProperty("patient_id");
		}
		else {
			System.out.println("No Document Found");
			return null;
		}
	}

	/*
	 * returns Database Document for given sensor_id
	 * returned document can be simply converted to map if just information needs to be retrieved or
	 * the document can be manipulated and updated in database using the returned Document handle
	 */
	public Document getSensor(String sensor_id){
		System.out.println(sensor_id);
		Database database = getDatabase(sensors_db_name);
		Query query_sr = database.getView(sensor_view).createQuery();
		query_sr.setStartKey(sensor_id);
		query_sr.setEndKey(sensor_id);
		
		QueryEnumerator result_sr = null;
		try {
			result_sr = query_sr.run();
		} catch (CouchbaseLiteException e) {
			e.printStackTrace();
		}
		Iterator<QueryRow> it = result_sr;
		if(it.hasNext()){
			QueryRow row = it.next();//n = 10000;
			System.out.println(row);
			
			Document d = database.getDocument(row.getDocumentId());
			System.out.println("Search Result Document "+ String.valueOf(d.getProperties()));
			return d;
		}
		else {
			System.out.println("No Document Found");
			return null;
		}
	}
	
	/*
	 * This method fetches pending readings que from database and tries to push them to server one by one
	 * Returns count of objects which were succesfully pushed to server	 
	 */
	public Integer push_readings(String username,String password,String uri) throws HttpResponseException, ClientProtocolException, IOException{
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
		HTTP_Functions http = new HTTP_Functions();
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
				
				json_being_pushed.remove("status");			
				System.out.println(http.Httppost(username,password,uri+"/module/sensorreading/sr.form",json_being_pushed));
				
				updateProperties.put("status", "pushed");
				document_being_pushed.putProperties(updateProperties);
			    Log.d(TAG, "updated retrievedDocument=" + String.valueOf(document_being_pushed.getProperties()));
			    pushed += 1;
			} catch (NullPointerException e){
				e.printStackTrace();
			}
			catch (JSONException e) {
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
	
	
	
}
