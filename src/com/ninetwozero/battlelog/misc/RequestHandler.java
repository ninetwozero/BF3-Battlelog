/*
	This file is part of BF3 Battlelog

    BF3 Battlelog is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    BF3 Battlelog is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
*/   

package com.ninetwozero.battlelog.misc;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.ByteArrayBuffer;
import org.apache.http.util.EntityUtils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.datatypes.PostData;
import com.ninetwozero.battlelog.datatypes.RequestHandlerException;
import com.ninetwozero.battlelog.datatypes.ShareableCookie;

public class RequestHandler {

	//Attributes
	public static DefaultHttpClient httpClient = getThreadSafeClient();	

	//ENCODING
	private static final String HEADER_ACCEPT_ENCODING = "Accept-Encoding";
	private static final String ENCODING_GZIP = "gzip";

	// Constructor
	public RequestHandler() {}
	
	public String get( String link, int extraHeaders ) throws RequestHandlerException {
		
		// Check defaults
		if ( link.equals( "" ) ) throw new RequestHandlerException("No link found.");
		
		//Default
		String httpContent = "";
		try {
			
			//Init the HTTP-related attributes
			HttpGet httpGet = new HttpGet(link);
			
			//Do we need those extra headers?
			if( extraHeaders == 1 ) {
			
				httpGet.setHeader( "X-Requested-With", "XMLHttpRequest");
				httpGet.setHeader( "X-AjaxNavigation", "1");
				httpGet.setHeader( "Referer", link);
				httpGet.setHeader( "Accept", "application/json, text/javascript, */*" );
				httpGet.setHeader( "Content-Type", "application/x-www-form-urlencoded; charset=UTF-8" );
			
			} else if( extraHeaders == 2 ) {
				
				httpGet.setHeader( "X-JSON", "1");
				httpGet.setHeader( "Referer", link);
				httpGet.setHeader( "Accept", "application/json, text/javascript, */*" );
				httpGet.setHeader( "Content-Type", "application/x-www-form-urlencoded; charset=UTF-8" );
				
			}
			
			HttpResponse httpResponse = RequestHandler.httpClient.execute(httpGet);	
			HttpEntity httpEntity = httpResponse.getEntity();
			
			//Anything?
			if (httpEntity != null) {

				//Get the content!
				httpContent = EntityUtils.toString( httpEntity );

			}
			
		} catch ( ClientProtocolException e ) {
			
			e.printStackTrace();
		
		} catch ( IOException e ) {

			e.printStackTrace();
		
		}
		
		return httpContent;
		
	}
	
	public Bitmap getImageFromStream( String link, boolean extraHeaders ) throws RequestHandlerException {
		
		// Check defaults
		if ( link.equals( "" ) ) throw new RequestHandlerException("No link found.");
		
		//Default
		Bitmap image = null;
		try {
			
			//Init the HTTP-related attributes
			HttpGet httpGet = new HttpGet(link);
			
			//Do we need those extra headers?
			if( extraHeaders ) {
			
				httpGet.setHeader( "X-Requested-With", "XMLHttpRequest");
				httpGet.setHeader( "X-AjaxNavigation", "1");
				httpGet.setHeader( "Referer", link);
				httpGet.setHeader( "Accept", "application/json, text/javascript, */*" );
				httpGet.setHeader( "Content-Type", "application/x-www-form-urlencoded; charset=UTF-8" );
			
			}
			
			HttpResponse httpResponse = RequestHandler.httpClient.execute(httpGet);	
			HttpEntity httpEntity = httpResponse.getEntity();
			
			//Create the image
			if (httpEntity != null) {

				//Get the content!
				image = BitmapFactory.decodeStream( httpEntity.getContent() );

			}
			
		} catch ( Exception ex ) {

			ex.printStackTrace();
			return null;
		}
		
		return image;
		
	}
	
	public boolean saveFileFromURI( String link, String directory, String filename ) throws RequestHandlerException {
			
		// Check defaults
		if ( link.equals( "" ) ) throw new RequestHandlerException("No link found.");
		
		//Default
		byte[] httpContent = null;
		FileOutputStream fileStream = null;
		String path = "";
		
		//Let's get a *nice* path
		if( filename != null && !filename.equals( "" ) ) {
			
			path = directory + filename;
			
		} else {
			
			path = directory + String.valueOf(System.currentTimeMillis());
		
		}

		//Let's do this!
		try {
			
			//Init the HTTP-related attributes
			HttpGet httpGet = new HttpGet(link);
			
			HttpResponse httpResponse = RequestHandler.httpClient.execute(httpGet);	
			HttpEntity httpEntity = httpResponse.getEntity();
			
			//Anything?
			if (httpEntity != null) {

				//Grab the response
				if( 
					
					httpResponse.containsHeader( "Encoding-Type" ) && 
					httpResponse.getFirstHeader( "Encoding-Type" ).getValue().equalsIgnoreCase( "gzip" ) 
					
				) {
					
					//*Fix* the entity
					httpEntity = new InflatingEntity(httpEntity);
					
					//Grab the gzipped response!
					httpContent = getStringFromGzipStream( new GZIPInputStream(httpEntity.getContent()) ).getBytes(); 
				
				} else {
				
					//Grab the response
					httpContent = EntityUtils.toString( httpEntity ).getBytes();
					
				}
				
			}
			
			//Let's see...
			if( httpContent.length <= 0 ) { return false; }
			else fileStream = new FileOutputStream( path );
			
			//Iterate over the bytes
			for( byte b : httpContent ) {
				
				fileStream.write( b );
				
			}
			
			//Clear the stream
			fileStream.flush();
			fileStream.close();
			
			return true;
			
		} catch( UnknownHostException ex ) { 
			
			ex.printStackTrace();
			throw new RequestHandlerException("No route to host");
			
		} catch ( Exception ex ) {

			ex.printStackTrace();
			throw new RequestHandlerException("No file found");
		
		}

			
	}

	public String post( String link, PostData[] postDataArray, int extraHeaders ) throws RequestHandlerException {

		// Check so it's not empty
		if ( link.equals( "" ) ) throw new RequestHandlerException("No link found.");

		//DEBUG
		//for( PostData p : postDataArray) { Log.d(Constants.debugTag, p.getField() + " => " + p.getValue()); }
		
		//Init...
		HttpPost httpPost = new HttpPost(link);
		
		//Do we need 'em?
		if( extraHeaders > 0 ) {
			
			if( extraHeaders == 1 ) {
								
				//Set headers
				httpPost.setHeader( "Host", "battlelog.battlefield.com" );
				httpPost.setHeader( "X-Requested-With", "XMLHttpRequest");
				httpPost.setHeader( "X-AjaxNavigation", "1");
				httpPost.setHeader( "Accept-Encoding", "gzip, deflate" );
				httpPost.setHeader( "Referer", Constants.URL_MAIN);
				httpPost.setHeader( "Accept", "application/json, text/javascript, */*" );
				httpPost.setHeader( "Content-Type", "application/x-www-form-urlencoded; charset=UTF-8" );

			} else if( extraHeaders == 2 ) {
				
				//Set headers
				httpPost.setHeader( "Host", "battlelog.battlefield.com" );
				httpPost.setHeader( "X-Requested-With", "XMLHttpRequest");
				httpPost.setHeader( "Accept-Encoding", "gzip, deflate" );
				httpPost.setHeader( "Referer", Constants.URL_MAIN);
				httpPost.setHeader( "Accept", "application/json, text/javascript, */*" );
				httpPost.setHeader( "Content-Type", "application/x-www-form-urlencoded; charset=UTF-8" );
				
			} else if( extraHeaders == 3 ) {
				
				httpPost.setHeader( "Host", "battlelog.battlefield.com" );
				httpPost.setHeader( "X-Requested-With", "XMLHttpRequest");
				httpPost.setHeader( "Accept-Encoding", "gzip, deflate" );
				httpPost.setHeader( "Referer", Constants.URL_MAIN);
				httpPost.setHeader( "Accept", "application/json, text/javascript, */*" );
				httpPost.setHeader( "Content-Type", "application/x-www-form-urlencoded; charset=UTF-8" );
				httpPost.setHeader( "Accept-Charset", "utf-8,ISO-8859-1;");
			}
			
		}
			
		//More init
		List<BasicNameValuePair> nameValuePairs = new ArrayList<BasicNameValuePair>();
		HttpEntity httpEntity;
		HttpResponse httpResponse;
		String httpContent = "";
		
		// Iterate over the fields and add the NameValuePairs
		for ( PostData data : postDataArray ) {

			nameValuePairs.add( 
				new BasicNameValuePair( 
					data.getField(),
					( data.isHash() ) ? this.hash( data.getValue() ) : data.getValue() 
				)
			);

		}
		
		try {
			
			//Set the entity
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
			httpResponse = httpClient.execute(httpPost);
			httpEntity = httpResponse.getEntity();
			
			//Anything?
			if (httpEntity != null) {

				//Get the content!
				httpContent = EntityUtils.toString( httpEntity );

			} else {
				
				Log.d(Constants.DEBUG_TAG, "The response was null. Weird.");
				
			}
		
		} catch( UnknownHostException ex ) { 
			
			ex.printStackTrace();
			
		} catch ( Exception e ) {
			
			e.printStackTrace();
		
		}
		
		return httpContent;

	}

	/** @author http://androidgenuine.com/?p=402 */
	public String hash( String str ) {

		try {
			
			// Create SHA-256
			MessageDigest digest = MessageDigest.getInstance( "SHA-256" );
			digest.update( str.getBytes() );
			byte messageDigest[] = digest.digest();

			// Create Hex String
			StringBuffer hexString = new StringBuffer();
			for ( int i = 0; i < messageDigest.length; i++ ) {
			
				String h = Integer.toHexString( 0xFF & messageDigest[i] );
				while ( h.length() < 2 ) { h = "0" + h; }
				hexString.append( h );
			
			}
			return hexString.toString();

		}
		catch ( NoSuchAlgorithmException e ) {
			e.printStackTrace();
		}
		return "";
	}
	
	private String getStringFromGzipStream( GZIPInputStream in ) {

		//Initialize variables
		BufferedInputStream buff_in = new BufferedInputStream(in);
		int byte_read = 0;
		int buff_size = 512;
		byte[] byte_buffer = new byte[buff_size];
		ByteArrayBuffer buff_array = new ByteArrayBuffer(50);
		
		try {
			
			// While we still can read data - go for it. Break loose when done!
			while ( true ) {
	
				//Read the byte
				byte_read = buff_in.read( byte_buffer );
				
				//If it's -1, we break
				if ( byte_read == -1 ) { break; }
	
				// Add the buffered bytes to the array for storage
				buff_array.append( byte_buffer, 0, byte_read );
			}

			return new String( buff_array.toByteArray());
		
		} catch ( Exception ex ) {

			// Read/write error
			ex.printStackTrace();
			return null;
			
		}
		
	}
	
	public void close() {
	
		if( httpClient.getConnectionManager() != null ) { httpClient.getConnectionManager().closeExpiredConnections(); }
		
	}

	public static DefaultHttpClient getThreadSafeClient() {
		
	     DefaultHttpClient client = new DefaultHttpClient();
	     ClientConnectionManager mgr = client.getConnectionManager();
	     HttpParams params = client.getParams();
	     client = new DefaultHttpClient(
	    		 
	    	new ThreadSafeClientConnManager(
	    			
	    		params, 
	    		mgr.getSchemeRegistry()
	    		
	    	), 
	    
	    	params
	    
		 );
	  
	     return client;
	 
	}

	public static ArrayList<ShareableCookie> getCookies() {
	
		//Get our cookie storage
		List<Cookie> cookies = RequestHandler.httpClient.getCookieStore().getCookies();
		ArrayList<ShareableCookie> serializedCookies = new ArrayList<ShareableCookie>();

		//Empty?
		if (!cookies.isEmpty()){
			
			for( Cookie c : cookies ) { serializedCookies.add( new ShareableCookie(c) ); }
		
		}

		return serializedCookies;
	}
	
	public static void setCookies( ArrayList<ShareableCookie> sc ) {
		
		//Init
    	CookieStore cookieStore = RequestHandler.httpClient.getCookieStore();
    	
    	//Did we have an icicle?
    	if( cookieStore.getCookies().isEmpty() ) {
	    	
			//Set it up
			ArrayList<ShareableCookie> serializedCookies = sc;
			
			//Loop & add
			for( ShareableCookie sCookie : serializedCookies ) {

				BasicClientCookie tempCookie = new BasicClientCookie(sCookie.getName(), sCookie.getValue());
				tempCookie.setDomain(sCookie.getDomain());
				cookieStore.addCookie( tempCookie );
				
			}
			
    		RequestHandler.httpClient.setCookieStore( cookieStore );
	    		 
    	}
		
	}
	
	static {

		httpClient.addRequestInterceptor(
		
			new HttpRequestInterceptor() {
			
				public void process(HttpRequest request, HttpContext context) {
				
					// Add header to accept gzip content
					if (!request.containsHeader(HEADER_ACCEPT_ENCODING)) { request.addHeader(HEADER_ACCEPT_ENCODING, ENCODING_GZIP); }
				
				}
				
			}
		
		);

		httpClient.addResponseInterceptor(
		
			new HttpResponseInterceptor() {
			
				public void process(HttpResponse response, HttpContext context) {
				
					// Inflate any responses compressed with gzip
					final HttpEntity entity = response.getEntity();
					final Header encoding = entity.getContentEncoding();
					
					//How's the encoding you ask?
					if (encoding != null) {
					
						for (HeaderElement element : encoding.getElements()) {
						
							if (element.getName().equalsIgnoreCase(ENCODING_GZIP)) {
							
								response.setEntity(new InflatingEntity(response.getEntity()));
								break;
							
							}
						
						}
					
					}
				
				}
			
			}
		
		);

	}

	 private static class InflatingEntity extends HttpEntityWrapper {
		 
		 public InflatingEntity(HttpEntity wrapped) { super(wrapped); }

         @Override
         public InputStream getContent() throws IOException { return new GZIPInputStream(wrappedEntity.getContent()); }

         @Override
         public long getContentLength() { return -1; }
	 
	 }

	
}