package com.ninetwozero.battlelog.datatypes;

import java.io.Serializable;
import java.util.List;

import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;

import com.ninetwozero.battlelog.misc.RequestHandler;


public class SerializedCookie implements Serializable {

	private static final long serialVersionUID = 2321884400035487579L;
	
	private String name;
	private String value;
	private String domain;

	public SerializedCookie(Cookie cookie){
		this.name = cookie.getName();
		this.value = cookie.getValue();
		this.domain = cookie.getDomain();
	}

	public String getName(){
		return name;
	}

	public String getValue(){
		return value;
	}
	public String getDomain(){
		return domain;
	}
}
