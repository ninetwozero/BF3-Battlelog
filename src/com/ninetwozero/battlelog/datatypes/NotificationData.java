package com.ninetwozero.battlelog.datatypes;

import android.text.Html;
import android.util.Log;

import com.ninetwozero.battlelog.misc.Constants;


public class NotificationData {

	//Attributes
	private long itemId, ownerId, commenterId, date;
	private int typeId;
	private String owner, commenter, type;
	
	//Constructs
	public NotificationData( 
			
		long iId, long oId, long cId, long d, int tId,
		String oName, String cName, String t
		
	) {
		
		this.itemId = iId;
		this.ownerId = oId;
		this.commenterId = cId;
		this.date = d;
		this.typeId = tId;
		this.owner = oName;
		this.commenter = cName;
		this.type = t;
		
	}
	
	//Getters
	public long getItemId() { return this.itemId; }
	public long getOwnerId() { return this.ownerId; }
	public long getCommenterId() { return this.commenterId; }
	public long getDate() { return this.date; }
	public int getTypeId() { return this.typeId; }
	public String getOwner() { return this.owner; }
	public String getCommenter() { return this.commenter; }
	public String getType() { return this.type; }
	
	//Generate a message
	public String getMessage(long userId) {
		
		Log.d( Constants.debugTag, userId + " :|: " + this.toString() );
		
		//What have we here?
		String message = "";
		if( this.type.equals( "feedcomment" ) ) {
			
			if( userId == this.getOwnerId() ) {
	
				message = "<b>{username}</b> commented on your <b>{action}</b>.".replace( 
					 
					"{action}", 
					resolveActionFromId(this.typeId) 
					
				);
				
			} else {
				
				if( owner.endsWith( "s" ) ) {

					message = "<b>{username}</b> commented on {owner}' <b>{action}</b>.".replace( 
						 
						"{action}", 
						resolveActionFromId(this.typeId) 
						
					);
				
				} else {
					
					message = "<b>{username}</b> commented on {owner}'s <b>{action}</b>.".replace( 
							 
						"{action}", 
						resolveActionFromId(this.typeId) 
						
					);
					
				}
				
			}
		
		} else if( this.type.equals( "feedcommentreply" ) ) {
			
			if( userId == this.getOwnerId() ) {
				
				message = "<b>{username}</b> also commented on your <b>{action}</b>.".replace( 
					 
					"{action}", 
					resolveActionFromId(this.typeId) 
					
				);
				
			} else {
				
				if( owner.endsWith( "s" ) ) {

					message = "<b>{username}</b> also commented on {owner}' <b>{action}</b>.".replace( 
						 
						"{action}", 
						resolveActionFromId(this.typeId) 
						
					);
				
				} else {
					
					message = "<b>{username}</b> also commented on {owner}'s <b>{action}</b>.".replace( 
							 
						"{action}", 
						resolveActionFromId(this.typeId) 
						
					);
					
				}
				
			}
			 
		} else if( this.type.equals( "wallpostcreated" ) ) {
			
			 message = "<b>{username}</b> wrote in your <b>Battle feed</b>.";
			
		} else if( this.type.equals( "feedlike" ) ) {
			
			if( userId == this.getOwnerId() ) {
				
				message = "<b>{username}</b> hooahs your <b>{action}</b>.".replace( 
					 
					"{action}", 
					resolveActionFromId(this.typeId) 
					
				);
				
			} else {
				
				if( owner.endsWith( "s" ) ) {

					message = "<b>{username}</b> hooahs {owner}' <b>{action}</b>.".replace( 
						 
						"{action}", 
						resolveActionFromId(this.typeId) 
						
					);
				
				} else {
					
					message = "<b>{username}</b> hooahs {owner}'s <b>{action}</b>.".replace( 
							 
						"{action}", 
						resolveActionFromId(this.typeId) 
						
					);
					
				}
				
			}
			 
		} else if( this.type.equals( "platoonjoinrequest" ) ) {
			
			message = "<b>{username}</b> wants to join <b>{platoon}</b>.";
			
		} else if( this.type.equals( "platoonmemberjoined") ) {
			
			message = "<b>{username}</b> joined your platoon <b>{platoon}</b>.";
			
		}  else if( this.type.equals( "platoonleft") ) {
			
			message = "<b>{username}</b> left the platoon <b>{platoon}</b>.";
			
		} else {
			
			message = "Unknown notification type.";
			
		}
		
		return Html.fromHtml( message ).toString().replace( "{username}", this.commenter ).replace( "{owner}", this.owner );
		
	}
	
	//Misc
	public String resolveActionFromId(int id) {
		
		switch( this.typeId ) {
			 
			case 1: //Friendship
				return "friendship";
			
			 case 2: //Forum post
				 return "forum post";
				 
			 case 4: //Game report
				 return "Game report";
				 
			 case 7: //Status message
				 return "Status message";
				 
			 case 9: //Battle report?
				 return "Battle report";
				 
			 case 20: //Platoon wall post
				 return "platoon wall post";
				 
			default:
				return "{unknown action}";
		}
		
	}
	
	//TO STRING
	@Override
	public String toString() { return "(" + this.itemId + ")" + " " + this.commenter + "<" + this.type + "> " + this.owner; }
 
}
