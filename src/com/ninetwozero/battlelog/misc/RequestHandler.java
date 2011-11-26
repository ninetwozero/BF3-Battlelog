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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.ByteArrayBuffer;

import android.graphics.drawable.Drawable;
import android.util.Log;

import com.ninetwozero.battlelog.datatypes.PostData;
import com.ninetwozero.battlelog.datatypes.RequestHandlerException;
import com.ninetwozero.battlelog.datatypes.SerializedCookie;

public class RequestHandler {

	//Attributes
	public static DefaultHttpClient httpClient = getThreadSafeClient();	

	// Constructor
	public RequestHandler() {}
	
	public String get( String link, boolean extraHeaders ) throws RequestHandlerException {
		
		// Check defaults
		if ( link.equals( "" ) ) throw new RequestHandlerException("No link found.");
		
		//Default
		String httpContent = "";
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
			httpContent = this.getStringFromStream( httpEntity.getContent() );
			
			//Is the entity <> null?
			if( httpEntity != null ) {
				
				httpEntity.consumeContent();
				
			}
			
		} catch ( ClientProtocolException e ) {
			
			e.printStackTrace();
		
		} catch ( IOException e ) {

			e.printStackTrace();
		
		}
		
		return httpContent;
		
	}
	
	public byte[] getImageFromStream( String link, boolean extraHeaders ) throws RequestHandlerException {
		
		// Check defaults
		if ( link.equals( "" ) ) throw new RequestHandlerException("No link found.");
		
		//Default
		InputStream httpContent = null;
		String image;
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
			httpContent = httpEntity.getContent();
			
			//Create the image
			image = getStringFromStream(httpContent);
			
			//Is the entity <> null?
			if( httpEntity != null ) {
				
				httpEntity.consumeContent();
				
			}
			
		} catch ( Exception ex ) {

			ex.printStackTrace();
			return null;
		}
		
		return image.getBytes();
		
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
			httpContent = getStringFromStream(httpEntity.getContent()).getBytes();
			
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
			
		} catch ( Exception ex ) {

			ex.printStackTrace();
			throw new RequestHandlerException("No file found");
		
		}

			
	}

	public String post( String link, PostData[] postDataArray, int extraHeaders ) throws RequestHandlerException {

		// Check so it's not empty
		if ( link.equals( "" ) ) throw new RequestHandlerException("No link found.");

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
				httpPost.setHeader( "Referer", Constants.urlMain);
				httpPost.setHeader( "Accept", "application/json, text/javascript, */*" );
				httpPost.setHeader( "Content-Type", "application/x-www-form-urlencoded; charset=UTF-8" );

			} else if( extraHeaders == 2 ) {
				
				//Set headers
				httpPost.setHeader( "Host", "battlelog.battlefield.com" );
				httpPost.setHeader( "X-Requested-With", "XMLHttpRequest");
				httpPost.setHeader( "Accept-Encoding", "gzip, deflate" );
				httpPost.setHeader( "Referer", Constants.urlMain);
				httpPost.setHeader( "Accept", "application/json, text/javascript, */*" );
				httpPost.setHeader( "Content-Type", "application/x-www-form-urlencoded; charset=UTF-8" );
				
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
				
				//Get the content
				httpContent = getStringFromStream(httpEntity.getContent());

				
				//Clear the entity
				httpEntity.consumeContent();
				
			} else {
				
				Log.d(Constants.debugTag, "The response was null. Weird.");
				
			}
		
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

	
	private String getStringFromStream( InputStream in ) {

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

	public static ArrayList<SerializedCookie> getSerializedCookies() {
	
		//Get our cookie storage
		List<Cookie> cookies = RequestHandler.httpClient.getCookieStore().getCookies();
		ArrayList<SerializedCookie> serializedCookies = new ArrayList<SerializedCookie>();

		//Empty?
		if (!cookies.isEmpty()){
			
			for( Cookie c : cookies ) { serializedCookies.add( new SerializedCookie(c) ); }
		
		}

		return serializedCookies;
	}
	
	public static void setSerializedCookies( ArrayList<SerializedCookie> sc ) {
		
		//Init
    	CookieStore cookieStore = RequestHandler.httpClient.getCookieStore();
    	
    	//Did we have an icicle?
    	if( cookieStore.getCookies().isEmpty() ) {
	    	
			//Set it up
			ArrayList<SerializedCookie> serializedCookies = sc;
			
			//Loop & add
			for( SerializedCookie sCookie : serializedCookies ) {

				BasicClientCookie tempCookie = new BasicClientCookie(sCookie.getName(), sCookie.getValue());
				tempCookie.setDomain(sCookie.getDomain());
				cookieStore.addCookie( tempCookie );
				
			}
			
    		RequestHandler.httpClient.setCookieStore( cookieStore );
	    		 
    	}
		
	}
	
}