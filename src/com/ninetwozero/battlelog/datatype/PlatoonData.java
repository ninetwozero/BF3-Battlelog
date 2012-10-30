package com.ninetwozero.battlelog.datatype;

import android.os.Parcel;
import android.os.Parcelable;

public class PlatoonData implements Parcelable {

    // Attributes
    private long id;
    private int countFans, countMembers, platformId;
    private String name, tag, image;
    private boolean visible;

    // Constructs

    public PlatoonData(Parcel in) {
        readFromParcel(in);
    }

    public PlatoonData(long i, String n) {
        id = i;
        name = n;
        tag = "";
        image = String.valueOf(id) + ".jpeg";
        countFans = 0;
        countMembers = 0;
        platformId = 0;
        visible = true;
    }

    public PlatoonData(long i, int cF, int cM, int pId, String n, String t, String img, boolean v) {
        id = i;
        countFans = cF;
        countMembers = cM;
        platformId = pId;
        name = n;
        tag = t;
        image = img;
        visible = v;
    }

    // Getters
    public long getId() {
        return id;
    }

    public int getCountFans() {
        return countFans;
    }

    public int getCountMembers() {
        return countMembers;
    }

    public int getPlatformId() {
        return platformId;
    }

    public String getName() {
        return name;
    }

    public String getTag() {
        return tag;
    }

    public String getImage() {
        return image;
    }

    public boolean isVisible() {
        return visible;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        // Everything else
        dest.writeLong(id);
        dest.writeInt(countFans);
        dest.writeInt(countMembers);
        dest.writeInt(platformId);
        dest.writeString(name);
        dest.writeString(tag);
        dest.writeString(image);
        dest.writeInt((visible) ? 1 : 0);

    }

    private void readFromParcel(Parcel in) {

        // Let's retrieve them, same order as above
        id = in.readLong();
        countFans = in.readInt();
        countFans = in.readInt();
        platformId = in.readInt();
        name = in.readString();
        tag = in.readString();
        image = in.readString();
        visible = (in.readInt() == 1);

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
        return id + ":" + name + ":" + tag;
    }
}
