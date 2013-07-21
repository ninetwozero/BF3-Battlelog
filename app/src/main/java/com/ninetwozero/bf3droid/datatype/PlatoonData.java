package com.ninetwozero.bf3droid.datatype;

import android.os.Parcel;
import android.os.Parcelable;

public class PlatoonData implements Parcelable {

    // Attributes
    private long mId;
    private String mName;
    private String mTag;
    private String mImage;
    private int mPlatformId;
    private int mCountFans;
    private int mCountMembers;
    private boolean mVisible;
    
    public PlatoonData(Parcel in) {
        readFromParcel(in);
    }

    public PlatoonData(long i, String n) {
        mId = i;
        mName = n;
        mTag = "";
        mImage = String.valueOf(mId) + ".jpeg";
        mCountFans = 0;
        mCountMembers = 0;
        mPlatformId = 0;
        mVisible = true;
    }

    public PlatoonData(long i, String n, String t, int pId, int cF, int cM, boolean v) {
        mId = i;
        mName = n;
        mTag = t;
        mPlatformId = pId;
        mCountFans = cF;
        mCountMembers = cM;
        mImage = String.valueOf(mId) + ".jpeg";
        mVisible = v;
    }

    // Getters
    public long getId() {
        return mId;
    }

    public int getCountFans() {
        return mCountFans;
    }

    public int getCountMembers() {
        return mCountMembers;
    }

    public int getPlatformId() {
        return mPlatformId;
    }

    public String getName() {
        return mName;
    }

    public String getTag() {
        return mTag;
    }

    public String getImage() {
        return mImage;
    }

    public boolean isVisible() {
        return mVisible;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        // Everything else
        dest.writeLong(mId);
        dest.writeInt(mCountFans);
        dest.writeInt(mCountMembers);
        dest.writeInt(mPlatformId);
        dest.writeString(mName);
        dest.writeString(mTag);
        dest.writeString(mImage);
        dest.writeInt((mVisible) ? 1 : 0);

    }

    private void readFromParcel(Parcel in) {

        // Let's retrieve them, same order as above
        mId = in.readLong();
        mCountFans = in.readInt();
        mCountFans = in.readInt();
        mPlatformId = in.readInt();
        mName = in.readString();
        mTag = in.readString();
        mImage = in.readString();
        mVisible = (in.readInt() == 1);

    }

    public static final Parcelable.Creator<PlatoonData> CREATOR = new Parcelable.Creator<PlatoonData>() {

        public PlatoonData createFromParcel(Parcel in) {
            return new PlatoonData(in);
        }

        public PlatoonData[] newArray(int size) {
            return new PlatoonData[size];
        }

    };

    @Override
    public final String toString() {
        return mId + ":" + mName + ":" + mTag;
    }
}
