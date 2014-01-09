#AndroidClientHTTP
=================

##HTTP client: GET, POST, JSON example

Simple class to use for sending HTTP request with callback function with result or error returned.
The class RequestHTTP use an AsyncTask to execute the request in background, so it's easy to use in Activities.


Examples
---------------------


Build an Uri:
`````java
  Uri httpGetUri = new Uri.Builder() 
  	.scheme("http")
  	.authority("dev.mikinacucchi.it")
  	.appendPath("HttpClientExample")
  	.appendPath("echo.php") //TODO link source code
  	.appendQueryParameter("var1", "1") //GET Params are concat at URI
  	.appendQueryParameter("var2", "2") //GET Params are concat at URI
  	.build();   	
  	
  try {
  	HttpGet httpget = new HttpGet(httpGetUri.toString());
  	if(D)Log.d(TAG, "buildHttpGet: " + httpGetUri.toString());
  } catch (IllegalArgumentException e) {
  	if(D)Log.e(TAG, "HttpPost: IllegalArgumentException");
  }    	

`````
Send Request to Server
`````java
  new RequestHTTP( 
    httpget, 
    new RequestHTTP.Response() {			
  		@Override
  		public void onSuccess(HttpResponse result) {
  			if(D)Log.d(TAG, "onSuccess");	
  			
  			//TODO manage Response ...
  			
  		}	
  		
  		@Override
  		public void onError(int errorCode) {
  			if(D)Log.e(TAG, "onError " + errorCode);
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
      		default:
      			strError = "UNKNOW ERROR";
      			break;  
    		}
    		
    		Toast.makeText(getApplicationContext(), strError, Toast.LENGTH_LONG).show();
  			
  		}					
    }
  );
`````
