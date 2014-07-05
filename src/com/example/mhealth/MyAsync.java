package com.example.mhealth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import android.support.v7.app.ActionBarActivity;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

//import com.example.mhealth.GetQuery.MyAsyncTask;

//import com.example.mhealth.GetQuery.MyAsyncTask;

import android.os.AsyncTask;
import android.widget.Toast;


	

	
public  class MyAsync extends AsyncTask<String, String, String>{
		OnAsyncResult lis=null;
		  String res1;
		public MyAsync(OnAsyncResult lis)
		{ this.lis=lis;
			
		}
		  @Override
		  protected String doInBackground(String... params) {
			
			   try {
				  
				   res1 = JsonParse(Httpget(params[0],params[1], params[2],params[3]));
				  
				   System.out.println("in back"+res1);
				  }
				   catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		       
				return res1;
		  }
		  
		  
		  protected void onPostExecute(String params){
			  System.out.println("inPost"+params);
			 lis.onResultSuccess(params);
			  
		  }
		  
		  protected void onProgressUpdate(Integer... progress){
			    			
		  }
		  public static StringBuilder inputStreamToString (InputStream is) {
				String line = "";
				StringBuilder total = new StringBuilder();
				// Wrap a BufferedReader around the InputStream
				BufferedReader rd = new BufferedReader(new InputStreamReader(is));
				// Read response until the end
				try {
					while ((line = rd.readLine()) != null) { 
						total.append(line); 
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				// Return full string
				return total;
				}
			
			
			public static String Httpget (String username, String password, String uri,String query) throws ClientProtocolException, IOException
			{
				HttpClient httpClient = new DefaultHttpClient();
				HttpGet httpGet = new HttpGet(uri+"/ws/rest/v1/patient?q="+query);
				httpGet.addHeader(BasicScheme.authenticate(new UsernamePasswordCredentials(username, password),"UTF-8", false));
				HttpResponse httpResponse = httpClient.execute(httpGet);
				HttpEntity responseEntity = httpResponse.getEntity();
				String str = inputStreamToString(responseEntity.getContent()).toString();
				httpClient.getConnectionManager().shutdown();
				System.out.println("get func"+str);
				return str;  
				         
		    	} 
			public static String JsonParse(String str) throws JSONException
			{ 
				String res="";
				JSONObject jo = new JSONObject(str);
				JSONArray ja = jo.getJSONArray("results");
				if (ja.length()==0)
						{
					
					return "zero";
						}
				int len = ja.length();
				//System.out.println(len);
				String s1;
				for(int i=0;i<len;i++)
				{
					JSONObject jo1 = (JSONObject) ja.get(i);
					 s1=(String)jo1.get("display");
					// System.out.println("here"+s1);
					 int j;
					 
					 for(j=0;j<s1.length()-1;j++)
					 {    
						 if(s1.charAt(j)=='-')
		                	break;		 
						 
					 }
					s1=s1.substring(j+2);
					
					res= res + s1 +"*";
					
					
				}
				System.out.println("jason parser"+res);
				return res;
			}
		  }
	
	
	
	


