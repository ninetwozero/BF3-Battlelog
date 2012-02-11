package com.ninetwozero.battlelog.datatypes;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.misc.Constants;



public class NotificationData implements Parcelable {

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

	public NotificationData( Parcel in ) {
			
		this.itemId = in.readLong();
		this.ownerId = in.readLong();
		this.commenterId = in.readLong();
		this.date = in.readLong();
		this.typeId = in.readInt();
		this.owner = in.readString();
		this.commenter = in.readString();
		this.type = in.readString();
		this.extra = in.readString();
		
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
			
		} else if( this.type.equals( "platooninviterequest" ) ) {
			
			message = c.getString( R.string.info_platoon_invite ).replace( 
					 
				"{platoon}", 
				this.extra
				
			);		
			
		} else if( this.type.equals( "platoonpromoted" ) ) {
			
			message = c.getString( R.string.info_platoon_promote ).replace( 
					 
				"{platoon}", 
				this.extra
				
			);		
			
		} else if( this.type.equals( "platoonkicked" ) ) {
			
			message = c.getString( R.string.info_platoon_kick ).replace( 
					 
				"{platoon}", 
				this.extra
				
			);		
		} else if( this.type.equals( "platoondemoted" ) ) {
			
			message = c.getString( R.string.info_platoon_demote ).replace( 
					 
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
			
		} else if( this.type.equals( "friendrequestaccepted" ) ) {

			message = c.getString( R.string.info_feed_friend_accept );
			
		} else {
			
			message = c.getString( R.string.info_unknown_notification );
			Log.d( Constants.DEBUG_TAG, "noticationType => " + this.type );
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

			case 13: //Platoon activity				
			case 16: //Platoon activity
			case 17: //New platoon /* TODO  */
				return c.getString(R.string.info_platoon_activity);
				
			case 20: //Platoon wall post
				return c.getString( R.string.info_platoon_wall_post );
				
			case 21: //Assignment completed
				return c.getString( R.string.info_assignment_completed );
				
			case 22: //Got game
				return c.getString( R.string.info_p_activated_expansion );
				
			default:
				Log.d( Constants.DEBUG_TAG, "noticationType => " + this.type );
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
 

	@Override
	public int describeContents() {

		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel( Parcel dest, int flags ) {

		dest.writeLong( this.itemId );
		dest.writeLong( this.ownerId );
		dest.writeLong( this.commenterId );
		dest.writeLong( this.date );
		dest.writeInt( this.typeId );
		dest.writeString( this.owner );
		dest.writeString( this.commenter );
		dest.writeString( this.type );
		dest.writeString( this.extra );
		
	}
	
	public static final Parcelable.Creator<NotificationData> CREATOR = new Parcelable.Creator<NotificationData>() {
		
		public NotificationData createFromParcel(Parcel in) { return new NotificationData(in); }
        public NotificationData[] newArray(int size) { return new NotificationData[size]; }
	
	};
	
}
