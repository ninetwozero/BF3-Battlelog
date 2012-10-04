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

    public PlatoonData(long i) {

        this.id = i;
        this.countFans = 0;
        this.countMembers = 0;
        this.platformId = 0;
        this.visible = true;

    }

    public PlatoonData(long i, int cF, int cM, int pId, String n, String t,
                       String img, boolean v) {

        this.id = i;
        this.countFans = cF;
        this.countMembers = cM;
        this.platformId = pId;
        this.name = n;
        this.tag = t;
        this.image = img;
        this.visible = v;

    }

    // Getters
    public long getId() {
        return this.id;
    }

    public int getCountFans() {
        return this.countFans;
    }

    public int getCountMembers() {
        return this.countMembers;
    }

    public int getPlatformId() {
        return this.platformId;
    }

    public String getName() {
        return this.name;
    }

    public String getTag() {
        return this.tag;
    }

    public String getImage() {
        return image;
    }

    public boolean isVisible() {
        return this.visible;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        // Everything else
        dest.writeLong(this.id);
        dest.writeInt(this.countFans);
        dest.writeInt(this.countMembers);
        dest.writeInt(this.platformId);
        dest.writeString(this.name);
        dest.writeString(this.tag);
        dest.writeString(this.image);
        dest.writeInt((this.visible) ? 1 : 0);

    }

    private void readFromParcel(Parcel in) {

        // Let's retrieve them, same order as above
        this.id = in.readLong();
        this.countFans = in.readInt();
        this.countFans = in.readInt();
        this.platformId = in.readInt();
        this.name = in.readString();
        this.tag = in.readString();
        this.image = in.readString();
        this.visible = (in.readInt() == 1);

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
        return this.id + ":" + this.name + ":" + this.tag;
    }
}
