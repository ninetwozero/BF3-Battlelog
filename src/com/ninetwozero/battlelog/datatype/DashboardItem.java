
package com.ninetwozero.battlelog.datatype;


public class DashboardItem {

    // Attributes
    private long mId;
    private String mTitle;

    // Construct
    public DashboardItem(long i, String t) {

        mId = i;
        mTitle = t;

    }

    // Getters
    public long getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

}
