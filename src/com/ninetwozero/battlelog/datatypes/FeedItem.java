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

package com.ninetwozero.battlelog.datatypes;

import java.util.ArrayList;



public class FeedItem {

	//Attributes
	private long ownerId, itemId, date;
	private int numLikes;
	private String title, content, type;
	private String[] username;
	private ArrayList<CommentData> comments;
	
	//Construct
	public FeedItem( 
	
		long oid, long iid, long nDate, int num,
		String t, String c, String type, String[] u,
		ArrayList<CommentData> cda
		
	) {
		
		this.ownerId = oid;
		this.itemId = iid;
		this.date = nDate;
		this.numLikes = num;
		this.title = t;
		this.content = c;
		this.type = type;
		this.username = u;
		this.comments = cda;
		
	}
	
	//Getters
	public long getOwnerId() { return this.ownerId; }
	public long getItemId() { return this.itemId; }
	public long getDate() { return this.date; }
	public int getNumLikes() { return this.numLikes; }
	public String getTitle() { 
		
		//Get the correct format depending on the type
		if( this.type.equals( "becamefriends" )  ) {
			
			return this.title.replace(
					
				"{player1}", this.username[0]
			
			).replace( 
					
				"{player2}", this.username[1]
						
			); 
		
		} else {
		
			return this.title;
			
		}
		
	}
	public String getContent() { return this.content; }
	public String getType() { return this.type; }
	public String[] getUsername() { return this.username; }
	public ArrayList<CommentData> getComments() { return this.comments; }
}
