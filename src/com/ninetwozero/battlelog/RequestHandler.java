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

package com.ninetwozero.battlelog;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.ByteArrayBuffer;

import android.util.Log;

import com.ninetwozero.battlelog.datatypes.PostData;
import com.ninetwozero.battlelog.datatypes.RequestHandlerException;

public class RequestHandler {

	//Attributes
	public static DefaultHttpClient httpClient = new DefaultHttpClient();	

	// Constructor
	public RequestHandler() {}
	
	public String get( String link ) throws RequestHandlerException {
		
		// Check defaults
		if ( link.equals( "" ) ) throw new RequestHandlerException("No link found.");
		
		//Default
		String httpContent = "";
		
		try {
			
			//Init the HTTP-related attributes
			HttpGet httpGet = new HttpGet(link);
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

	public String post( String link, PostData[] postDataArray ) throws RequestHandlerException {

		// Check so it's not empty
		if ( link.equals( "" ) ) throw new RequestHandlerException("No link found.");

		//Init yo
		HttpPost httpPost = new HttpPost(link);
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
				
			}
		
		} catch ( ClientProtocolException e ) {
			
			e.printStackTrace();
		
		} catch ( IOException e ) {
			
			e.printStackTrace();
		
		}
		
		return httpContent;

	}

	/** @author http://androidgenuine.com/?p=402 */
	public String hash( String str ) {

		try {
			// Create MD5 Hash
			MessageDigest digest = MessageDigest.getInstance( "SHA-256" );
			digest.update( str.getBytes() );
			byte messageDigest[] = digest.digest();

			// Create Hex String
			StringBuffer hexString = new StringBuffer();
			for ( int i = 0; i < messageDigest.length; i++ ) {
				String h = Integer.toHexString( 0xFF & messageDigest[i] );
				while ( h.length() < 2 )
					h = "0" + h;
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
				if ( byte_read == -1 ) {
					break;
				}
	
				// Add the buffered bytes to the array for storage
				buff_array.append( byte_buffer, 0, byte_read );
			}

			return new String( buff_array.toByteArray());
		
		} catch ( IOException ex ) {

			// Read/write error
			Log.e( "com.ninetwozero.shared", ex.getMessage(), ex );
			return null;
		}
	}

	public void close() {
	
		if( httpClient.getConnectionManager() != null ) { httpClient.getConnectionManager().closeExpiredConnections(); }
		
	}
	
}