
package com.ninetwozero.battlelog.datatypes;

import com.ninetwozero.battlelog.R;
import java.util.List;

public class FriendListDataWrapper {

    // Attributes
    private int numRequests, numPlaying, numOnline, numOffline;
    private List<ProfileData> friends;

    // Construct
    public FriendListDataWrapper(List<ProfileData> f, int r, int np, int on, int of) {

        // Set the data
        friends = f;
        numRequests = r;
        numPlaying = np;
        numOnline = on;
        numOffline = of;
    }

    // Getters
    public List<ProfileData> getFriends() {
        return friends;
    }

    public int getNumRequests() {

        return numRequests;
    }

    public int getNumPlaying() {

        return numPlaying;
    }

    public int getNumOnline() {

        return numOnline;
    }

    public int getNumOffline() {

        return numOffline;
    }

    public int getNumTotalOnline() {

        return numPlaying + numOnline;
    }

}
