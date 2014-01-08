package it.mikinacucchi.clienthttp;

import java.io.IOException;
import java.net.SocketTimeoutException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.os.AsyncTask;
import android.util.Log;

public class RequestHTTP {	
	private static final String TAG	=	"RequestHTTP";
	private static final boolean D	=	true; //Enable LogCat message

	public static final int ERRORCODE_HTTP_RESPONSE_FAIL	= 	-3;
	public static final int ERRORCODE_NETWORK				=	-2;
	public static final int ERRORCODE_URL_MALFORMED			= 	-1;
	public static final int SUCCESS							= 	0;
	
	private static final int CONNECTION_TIMEOUT	=	3000;//ms
	private static final int SOCKET_TIMEOUT		=	5000;//ms
	
	public static interface Response{ 		
		public abstract void onSuccess(HttpResponse result);//interface for callback result
		public abstract void onError(int errorCode);//interface for callback error
	}

	private AsyncTask<Object, Object, Integer> task = null;
	
	public RequestHTTP(final HttpUriRequest request, final Response I){
		if(D)Log.d(TAG, "requestJSON contructor");
		
		task = new AsyncTask<Object, Object, Integer>(){

			private HttpResponse response;

			@Override
			protected Integer doInBackground(Object... params) {					
				try {					
					DefaultHttpClient client = new DefaultHttpClient();
					final HttpParams httpParams = client.getParams();										
					// Set the timeout in milliseconds until a connection is established.
					// The default value is zero, that means the timeout is not used. 
					HttpConnectionParams.setConnectionTimeout(httpParams, CONNECTION_TIMEOUT);
					// Set the default socket timeout (SO_TIMEOUT) 
					// in milliseconds which is the timeout for waiting for data.
					HttpConnectionParams.setSoTimeout(httpParams, SOCKET_TIMEOUT);
										
					if(D)Log.d( TAG, "execute : " + request.getURI() + " ["+ request.getMethod() + "]");
					
					response = client.execute(request); // Wait Server Response throw Exception if timeout
					if(D)Log.d( TAG, "Status code = " + response.getStatusLine().getStatusCode());
					
					if(response.getStatusLine().getStatusCode() == 200){	
						if(D)Log.d( TAG, "SUCCESS");
						return SUCCESS;						
					}
					else{
						if(D)Log.e( TAG, "ERRORCODE_HTTP_RESPONSE_FAIL");
						return ERRORCODE_HTTP_RESPONSE_FAIL;
					}
					
				}
				catch(ClientProtocolException e){	
					if(D)Log.e( TAG , "ClientProtocolException");			
					return ERRORCODE_HTTP_RESPONSE_FAIL;	}
				catch(SocketTimeoutException e){	
					if(D)Log.e( TAG , "SocketException");			
					return ERRORCODE_NETWORK;	}
				catch(ConnectTimeoutException e){	
					if(D)Log.e( TAG , "ConnectTimeoutException");	
					return ERRORCODE_NETWORK;	} 
				catch (IOException e) {
					if(D)Log.e( TAG , "IOException");	
					return ERRORCODE_NETWORK;	}
				
			}
			
			@Override
			protected void onPostExecute(Integer code) {
				super.onPostExecute(code);				
				if(code == SUCCESS)
					I.onSuccess(response);
				else
					I.onError(code);					
				
				response = null;				
				forceStop();
			}			
		}.execute();		
	}


	public void forceStop() {
		if(D)Log.w(TAG , "forceStop");
		if(task != null) {
			if(D)Log.w(TAG , "cancel task");			
			if(task.cancel(true)) task = null;
		}
	}

}
