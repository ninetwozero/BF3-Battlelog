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

public class ChatMessage {

    // Attributes
    private long chatId, timestamp;
    private String sender, message;

    // Construct
    public ChatMessage(long c, long t, String s, String m) {

        this.chatId = c;
        this.timestamp = t;
        this.sender = s;
        this.message = m;

    }

    // Getters
    public long getChatId() {
        return this.chatId;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public String getSender() {
        return this.sender;
    }

    public String getMessage() {
        return this.message;
    }

}
