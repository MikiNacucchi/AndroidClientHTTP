package it.mikinacucchi.clienthttp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.Activity;
import android.net.Uri;
import android.net.Uri.Builder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private static final String TAG	=	"MainActivity";
	private static final boolean D	=	true; //Enable LogCat message

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
               
        //GET Button send HTTP GET Request and show result
        findViewById(R.id.bttGET).setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {				
				if(D)Log.d(TAG, "GET Request...");		
				showProgressBar();
				new RequestHTTP( buildHttpGet(), new RequestHTTP.Response() {			
					@Override
					public void onSuccess(HttpResponse result) {
						if(D)Log.d(TAG, "onSuccess...");	
						hideProgressBar();
						appendResult(result);				
					}			
					@Override
					public void onError(int errorCode) {
						if(D)Log.e(TAG, "onError...");
						hideProgressBar();
						showError(errorCode);
					}					
				});				
			}
		});
        //POST Button send HTTP POST Request and show result
        findViewById(R.id.bttPOST).setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {				
				if(D)Log.d(TAG, "POST Request...");
				showProgressBar();
				new RequestHTTP( buildHttpPost(), new RequestHTTP.Response() {			
					@Override
					public void onSuccess(HttpResponse result) {
						if(D)Log.d(TAG, "onSuccess...");	
						hideProgressBar();
						appendResult(result);				
					}			
					@Override
					public void onError(int errorCode) {
						if(D)Log.e(TAG, "onError...");
						hideProgressBar();
						showError(errorCode);
					}
				});				
			}
		});
        //JSON Button send HTTP(GET) Request and show result. Display a Toast with value of key "string"
        findViewById(R.id.bttJSON).setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {				
				if(D)Log.d(TAG, "JSON Request...");
				showProgressBar();
				new RequestJSON( buildHttpGetJSON(), new RequestJSON.Response() {					
					@Override
					public void onSuccess(JSONTokener json) {
						Log.d(TAG, "onSuccess...");
						hideProgressBar();
						appendResult(json.toString());
						appendResult("\n========================================");
						
						//Show Toast
						try {
							Toast.makeText(
									getApplicationContext(), 
									((JSONObject) json.nextValue()).getString("string"), //read key "string"
									Toast.LENGTH_LONG).show();							
						} catch (JSONException e) {
							Log.e(TAG, "JSONException");
						}
					}					
					@Override
					public void onError(int errorCode) {
						Log.e(TAG, "onError...");
						hideProgressBar();
						showError(errorCode);
					}
				});				
			}
		});
    }
    
    /*Show & Hide ProgressBar
     * */
	protected void showProgressBar() {
		findViewById(R.id.progressBar1).setVisibility(View.VISIBLE);
	}
	protected void hideProgressBar() {
		findViewById(R.id.progressBar1).setVisibility(View.GONE);
	}	
	/*Show Toast with error occurred 
     * */
	private void showError(int errorCode) {
		String strError = null;
		switch (errorCode) { 
		//All RequestHTTP Errors
		case RequestHTTP.ERRORCODE_HTTP_RESPONSE_FAIL:
			strError = "ERRORCODE_HTTP_RESPONSE_FAIL";
			break;
		case RequestHTTP.ERRORCODE_NETWORK:
			strError = "ERRORCODE_NETWORK";
			break;
		case RequestHTTP.ERRORCODE_URL_MALFORMED:
			strError = "ERRORCODE_URL_MALFORMED";
			break;
			
		//RequestJSON Error
		case RequestJSON.ERRORCODE_JSON_PARSE_FAIL:
			strError = "ERRORCODE_JSON_PARSE_FAIL";
			break;

		default:
			strError = "UNKNOW ERROR";
			break;
		}
		
		Toast.makeText(getApplicationContext(), strError, Toast.LENGTH_LONG).show();
	}
	
	/*
	 * URI Builders
	 */
	private Builder baseUriHttp() {		
			return new Uri.Builder() 
	    	.scheme("http")
	    	.authority("dev.mikinacucchi.it")
	    	.appendPath("HttpClientExample");
		}	 
    private HttpUriRequest buildHttpGet(){
    	Uri httpGetUri = baseUriHttp()
    	.appendPath("echo.php") //TODO link source code
    	.appendQueryParameter("var1", "1") //GET Params are concat at URI
    	.appendQueryParameter("var2", "2") //GET Params are concat at URI
    	.build();   	
    	
    	try {
    		HttpGet httpget = new HttpGet(httpGetUri.toString());
    		if(D)Log.d(TAG, "buildHttpGet: " + httpGetUri.toString());
    		return httpget;
		} catch (IllegalArgumentException e) {
			if(D)Log.e(TAG, "HttpPost: IllegalArgumentException");
		}    	
    	
    	return null;
    }       
	private HttpUriRequest buildHttpPost(){
    	Uri httpPostUri = baseUriHttp()
    	.appendPath("echo.php") //TODO link source code
    	.build();
    	
    	//POST Params
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);  
		nameValuePairs.add(new BasicNameValuePair("var1", "1"));  
		nameValuePairs.add(new BasicNameValuePair("var2", "2"));
				  
		try {
			HttpPost httppost = new HttpPost(httpPostUri.toString());
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			if(D)Log.d(TAG, "buildHttpPost: " + httpPostUri.toString() + " numbersOfParams" + nameValuePairs.size());			
			return httppost;
		} catch (IllegalArgumentException e) {
			if(D)Log.e(TAG, "HttpPost: IllegalArgumentException");
		} catch (UnsupportedEncodingException e) {
			if(D)Log.e(TAG, "setEntity: UnsupportedEncodingException");
		}
    	   	
		return null;
    }    
    private HttpUriRequest buildHttpGetJSON(){
    	Uri httpGetUri = baseUriHttp()
    	.appendPath("srcJSON.php") //TODO link source code
    	.build();   	
    	
    	try {
    		HttpGet httpget = new HttpGet(httpGetUri.toString());
    		if(D)Log.d(TAG, "buildHttpGet: " + httpGetUri.toString());
    		return httpget;
		} catch (IllegalArgumentException e) {
			if(D)Log.e(TAG, "HttpPost: IllegalArgumentException");
		}    	
    	
    	return null;
    }
    
    /*Show Result in TextView
     * (append new results) 
     * */
    protected void appendResult(String strResult){
    	((TextView)findViewById(R.id.txtResponse)).append(strResult);
    }
    protected void appendResult(HttpResponse result) {
    	try {
    		BufferedReader reader = new BufferedReader(new InputStreamReader(result.getEntity().getContent(), "UTF-8"),8);					
			char buf[] = new char[1024];
			while(reader.read(buf)>0){
				appendResult(new String(buf));
			}
			reader.close();
			appendResult("\n========================================");
		} 
		catch (UnsupportedEncodingException e) {	if(D)Log.e(TAG, "UnsupportedEncodingException");	}
		catch (IllegalStateException e) {	if(D)Log.e(TAG, "IllegalStateException");	}
		catch (IOException e) {	 if(D)Log.e(TAG, "IOException");	}
	}
    
}
