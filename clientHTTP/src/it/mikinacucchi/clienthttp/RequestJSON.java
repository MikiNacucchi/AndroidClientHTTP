package it.mikinacucchi.clienthttp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.json.JSONTokener;

import android.util.Log;

public class RequestJSON{	
	private static final String TAG	=	"RequestJSON";
	private static final boolean D	=	true; //Enable LogCat message
	
	public static final int ERRORCODE_JSON_PARSE_FAIL	= 	-10;
	
	public static interface Response{		
		public abstract void onSuccess(JSONTokener json);//interface for callback result
		public abstract void onError(int errorCode);//interface for callback result
	}

	public RequestJSON(HttpUriRequest request, final RequestJSON.Response I) {
		new RequestHTTP(request, new RequestHTTP.Response() {
			
			@Override
			public void onSuccess(HttpResponse result) {				
				try {
					BufferedReader reader;
					reader = new BufferedReader(new InputStreamReader(result.getEntity().getContent(), "UTF-8"),8);
					StringBuilder builder = new StringBuilder();				
					char[] buf = new char[1024];
				    int l = 0;
				    while (l >= 0) {
				        builder.append(buf, 0, l);
				        l = reader.read(buf);
				    }
				    reader.close();
				    reader = null;
				    
				    I.onSuccess(new JSONTokener(builder.toString()));
				    
				    builder = null;
				    return;				    
				} 
				catch (UnsupportedEncodingException e) {	if(D)Log.e(TAG, "UnsupportedEncodingException");	}
				catch (IllegalStateException e) {	if(D)Log.e(TAG, "IllegalStateException");	}
				catch (IOException e) {	if(D)Log.e(TAG, "IOException");	}
				
				I.onError(ERRORCODE_JSON_PARSE_FAIL);   									
			}
			
			@Override
			public void onError(int errorCode) {
				I.onError(errorCode);
			}
		});
	}

}
