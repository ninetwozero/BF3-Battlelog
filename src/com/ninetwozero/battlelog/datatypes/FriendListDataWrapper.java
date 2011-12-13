package com.ninetwozero.battlelog.datatypes;

import com.ninetwozero.battlelog.R;
import java.util.ArrayList;



public class FriendListDataWrapper {

	//Attributes
	private ArrayList<ProfileData> requests, onlineFriends, offlineFriends;
	
	//Construct
	public FriendListDataWrapper(ArrayList<ProfileData> r, ArrayList<ProfileData> on, ArrayList<ProfileData> off) {
		
		//Set the data
		requests = r;
		onlineFriends = on;
		offlineFriends = off;
	}
	
	//Getters
	public ArrayList<ProfileData> getRequests() { return this.requests; }
	public ArrayList<ProfileData> getOnlineFriends() { return this.onlineFriends; }
	public ArrayList<ProfileData> getOfflineFriends() { return this.offlineFriends; }
	public ArrayList<ProfileData> getFriends() {
		
		//Init
		ArrayList<ProfileData> merged = new ArrayList<ProfileData>();
		
		//Merge
		if( this.onlineFriends != null ) { merged.addAll( this.onlineFriends ); }
		if( this.offlineFriends != null ) { merged.addAll( this.offlineFriends ); }
		
		//Return
		return merged;
	}
	public int getOnlineCount() { 
		
		if( this.onlineFriends != null && this.onlineFriends.size() > 0 ) {
		
			return this.onlineFriends.size()-1;
		
		} else {
			
			return 0;

		}
	
	}
	
}
