package com.ninetwozero.battlelog.datatypes;

import com.ninetwozero.battlelog.R;
import android.content.Context;



public class NotificationData {

	//Attributes
	private long itemId, ownerId, commenterId, date;
	private int typeId;
	private String owner, commenter, type, extra;
	
	//Constructs
	public NotificationData( 
			
		long iId, long oId, long cId, long d, int tId,
		String oName, String cName, String t, String x
		
	) {
		
		this.itemId = iId;
		this.ownerId = oId;
		this.commenterId = cId;
		this.date = d;
		this.typeId = tId;
		this.owner = oName;
		this.commenter = cName;
		this.type = t;
		this.extra = x;
		
	}
	
	//Getters
	public long getItemId() {return this.itemId; }
	public long getOwnerId() {return this.ownerId; }
	public long getCommenterId() {return this.commenterId; }
	public long getDate() {return this.date; }
	public int getTypeId() {return this.typeId; }
	public String getOwner() {return this.owner; }
	public String getCommenter() {return this.commenter; }
	public String getType() {return this.type; }
	public String getExtra() {return this.extra; }
	
	//Generate a message
	public String getMessage(final Context c, final long userId) {

		//What have we here? 
		String message;
		if( this.type.equals( "feedcomment" ) ) {
			
			if( 0 == this.ownerId || userId == this.ownerId ) {
	
				message = c.getString( R.string.info_feed_comment_own ).replace( 
					 
					"{action}", 
					resolveActionFromId(c, this.typeId) 
					
				);
				
			} else {
				
				if( owner.endsWith( "s" ) ) {

					message = c.getString( R.string.info_feed_comment_other ).replace( 
						 
						"{action}", 
						resolveActionFromId(c, this.typeId) 
						
					);
				
				} else {
					
					message = c.getString( R.string.info_feed_comment_other_v2 ).replace( 
							 
						"{action}", 
						resolveActionFromId(c, this.typeId) 
						
					);
					
				}
				
			}
		
		} else if( this.type.equals( "feedcommentreply" ) ) {
			
			if( 0 == this.ownerId || userId == this.ownerId ) {
				
				message = c.getString( R.string.info_feed_comment_own_2 ).replace( 
					 
					"{action}", 
					resolveActionFromId(c, this.typeId) 
					
				);
				
			} else {
				
				if( owner.endsWith( "s" ) ) {

					message = c.getString( R.string.info_feed_comment_other_2 ).replace( 
						 
						"{action}", 
						resolveActionFromId(c, this.typeId) 
						
					);
				
				} else {
					
					message = c.getString( R.string.info_feed_comment_other_v2_2 ).replace( 
							 
						"{action}", 
						resolveActionFromId(c, this.typeId) 
						
					);
					
				}
				
			}
			 
		} else if( this.type.equals( "wallpostcreated" ) ) {
			
			 message = c.getString( R.string.info_feed_post_own );
			
		} else if( this.type.equals( "feedlike" ) ) {
			
			if( 0 == this.ownerId || userId == this.ownerId ) {
				
				message = c.getString( R.string.info_feed_hooah_own ).replace( 
					 
					"{action}", 
					resolveActionFromId(c, this.typeId) 
					
				);
				
			} else {
				
				if( owner.endsWith( "s" ) ) {

					message = c.getString( R.string.info_feed_hooah_other ).replace( 
						 
						"{action}", 
						resolveActionFromId(c, this.typeId) 
						
					);
				
				} else {
					
					message = c.getString( R.string.info_feed_hooah_other_2 ).replace( 
							 
						"{action}", 
						resolveActionFromId(c, this.typeId) 
						
					);
					
				}
				
			}
			 
		} else if( this.type.equals( "platoonjoinrequestaccepted" ) ) {
			
			message = c.getString( R.string.info_platoon_join_ok ).replace( 
		 
				"{platoon}", 
				this.extra
			
			);
			
		} else if( this.type.equals( "platoonjoinrequestdeclined" ) ) {
			
			message = c.getString( R.string.info_platoon_join_no ).replace( 
		 
				"{platoon}", 
				this.extra
			
			);
			
		} else if( this.type.equals( "platoonjoinrequest" ) ) {
			
			message = c.getString( R.string.info_platoon_join_request ).replace( 
					 
				"{platoon}", 
				this.extra
				
			);
			
		} else if( this.type.equals( "platoonmemberjoined") ) {
			
			message = c.getString( R.string.info_platoon_join_request_ok ).replace( 
					 
				"{platoon}", 
				this.extra
				
			);
			
		}  else if( this.type.equals( "platoonleft") ) {
			
			message = c.getString( R.string.info_platoon_left ).replace( 
					 
				"{platoon}", 
				this.extra
				
			);
			
		} else {
			
			message = c.getString( R.string.info_unknown_notification );
			
		}
		
		return message.replace( "{username}", this.commenter ).replace( "{owner}", this.owner );
		
	}
	
	//Misc
	public String resolveActionFromId(Context c, int id) {
		
		switch( this.typeId ) {
			 
			case 1: //Friendship
				return c.getString( R.string.info_friendship );
			
			case 2: //Forum post
				return c.getString( R.string.info_forum_post );
				 
			case 4: //Game report
				return c.getString( R.string.info_game_report );
				 
			case 6: //Wall post
				return c.getString( R.string.info_wallpost );
				
			case 7: //Status message
				return c.getString( R.string.info_status_message );
			
			case 8: //Award
				return c.getString( R.string.info_award );
				 
			case 9: //Battle report?
				return c.getString( R.string.info_battle_report );
			
			case 11: //Rank
				return c.getString( R.string.info_new_rank);
			
			case 20: //Platoon wall post
				return c.getString( R.string.info_platoon_wall_post );
				
			case 22: //Got game
				return c.getString( R.string.info_p_activated_expansion );
				
			default:
				return "{unknown action}";
		}
		
	}
	
	//TO STRING
	@Override
	public String toString() { 
		
		return (
			
			"(" + this.itemId + ")" + " " + this.commenter + 
			"(" + this.commenterId + ") <" + this.type + "> " +
			this.owner + "(" + this.ownerId + ")"
		
		);
	}
 
}
