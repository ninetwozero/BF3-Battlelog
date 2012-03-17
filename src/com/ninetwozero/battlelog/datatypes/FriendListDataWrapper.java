
package com.ninetwozero.battlelog.datatypes;

import com.ninetwozero.battlelog.R;
import java.util.ArrayList;
import java.util.List;

public class FriendListDataWrapper {

    // Attributes
    private List<ProfileData> requests, onlineFriends, offlineFriends;

    // Construct
    public FriendListDataWrapper(List<ProfileData> r,
            ArrayList<ProfileData> on, List<ProfileData> off) {

        // Set the data
        requests = r;
        onlineFriends = on;
        offlineFriends = off;
    }

    // Getters
    public List<ProfileData> getRequests() {
        return this.requests;
    }

    public List<ProfileData> getOnlineFriends() {
        return this.onlineFriends;
    }

    public List<ProfileData> getOfflineFriends() {
        return this.offlineFriends;
    }

    public List<ProfileData> getFriends() {

        // Init
        ArrayList<ProfileData> merged = new ArrayList<ProfileData>();

        // Merge
        if (this.onlineFriends != null) {
            merged.addAll(this.onlineFriends);
        }
        if (this.offlineFriends != null) {
            merged.addAll(this.offlineFriends);
        }

        // Return
        return merged;
    }

    public int getOnlineCount() {

        if (this.onlineFriends != null && this.onlineFriends.size() > 0) {

            return this.onlineFriends.size() - 1;

        } else {

            return 0;

        }

    }

}
