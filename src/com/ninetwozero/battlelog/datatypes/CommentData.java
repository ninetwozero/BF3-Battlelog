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

import com.ninetwozero.battlelog.R;
import java.io.Serializable;


public class CommentData implements Serializable {

	//Attributes
	private static final long serialVersionUID = -6685250652253512690L;
	private long id, itemId, timestamp, authorId;
	private String author, content;
	
	//Constructs
	public CommentData( long id, long iId, long cDate, long aId, String a, String c) {
		
		this.id = id;
		this.itemId = iId;
		this.timestamp = cDate;
		this.authorId = aId;
		this.author = a;
		this.content = c;
		
	}
	
	//Getters
	public long getId() { return this.id; }
	public long getItemId() { return this.itemId; }
	public long getTimestamp() { return this.timestamp; }
	public long getAuthorId() { return this.authorId; }
	public String getAuthor() { return this.author; }
	public String getContent() { return this.content; }
	
	@Override
	public String toString() { return getId() + ":" + getAuthorId() + ":" + getAuthor() + ":" + getContent(); }
}