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


public class CommentData {

	//Attributes
	private long id, itemId, creationDate, authorId;
	private String author, content;
	
	//Constructs
	public CommentData( long id, long iId, long cDate, long aId, String a, String c) {
		
		this.id = id;
		this.itemId = iId;
		this.creationDate = cDate;
		this.authorId = aId;
		this.author = a;
		this.content = c;
		
	}
	
	//Getters
	public long getId() { return this.id; }
	public long getItemId() { return this.itemId; }
	public long getCreationDate() { return this.creationDate; }
	public long getAuthorId() { return this.authorId; }
	public String getAuthor() { return this.author; }
	public String getContent() { return this.content; }
	
}