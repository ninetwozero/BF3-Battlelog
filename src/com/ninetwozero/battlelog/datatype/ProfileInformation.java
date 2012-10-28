/*
	This file is part of BF3 Battlelog

    BF3 Battlelog is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    BF3 Battlelog is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
 */

package com.ninetwozero.battlelog.datatype;

import android.content.Context;
import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.misc.PublicUtils;

import java.util.Arrays;
import java.util.List;

public class ProfileInformation {

    // Attributes
    private int mAge;
    private long mUserId;
    private long mDateOfBirth;
    private long mLastlogin;
    private long mStatusMessageChanged;
    private PersonaData[] mPersona;
    private String mName;
    private String mUsername;
    private String mPresentation;
    private String mLocation;
    private String mStatusMessage;
    private String mCurrentServer;
    private boolean mAllowFriendRequests;
    private boolean mOnline;
    private boolean mPlaying;
    private boolean mFriendStatus;
    private List<PlatoonData> mPlatoons;

    // Other
    private StringBuilder mPersonaString = new StringBuilder();
    private StringBuilder mPersonaIdString = new StringBuilder();
    private StringBuilder mPersonaPlatformString = new StringBuilder();
    private StringBuilder mPlatoonString = new StringBuilder();
    private StringBuilder mPlatoonIdString = new StringBuilder();

    public ProfileInformation(
        int a, long uid, long dob, long l, long sc, PersonaData[] pe, String n,
        String u, String p, String loc, String s, String c, boolean af,
        boolean o, boolean pl, boolean fs, List<PlatoonData> pd
    ) {
        mAge = a;
        mUserId = uid;
        mDateOfBirth = dob;
        mLastlogin = l;
        mStatusMessageChanged = sc;
        mPersona = pe.clone();
        mName = n;
        mUsername = u;
        mPresentation = p;
        mLocation = loc;
        mStatusMessage = s;
        mCurrentServer = c;
        mAllowFriendRequests = af;
        mOnline = o;
        mPlaying = pl;
        mFriendStatus = fs;
        mPlatoons = pd;
    }

    // Getters
    public int getAge() {
        return mAge;
    }

    public long getUserId() {
        return mUserId;
    }

    public long getDOB() {
        return mDateOfBirth;
    }

    public String getLastLogin(Context c) {
        return PublicUtils.getRelativeDate(c, mLastlogin,
                R.string.info_lastlogin);
    }

    public long getStatusMessageChanged() {
        return mStatusMessageChanged;
    }

    public PersonaData getPersona(int position) {
        return ((mPersona.length < position) ? mPersona[position] : mPersona[0]);
    }

    public PersonaData[] getAllPersonas() {
        return mPersona;
    }

    public String getName() {
        return mName;
    }

    public String getUsername() {
        return mUsername;
    }

    public String getPresentation() {
        return mPresentation;
    }

    public String getLocation() {
        return mLocation;
    }

    public String getStatusMessage() {
        return mStatusMessage;
    }

    public String getCurrentServer() {
        return mCurrentServer;
    }

    public boolean isAllowingFriendRequests() {
        return mAllowFriendRequests;
    }

    public boolean isOnline() {
        return mOnline;
    }

    public boolean isPlaying() {
        return mPlaying;
    }

    public boolean isFriend() {
        return mFriendStatus;
    }

    public long getPlatoonId(int position) {
        return ((mPlatoons.size() < position) ? mPlatoons.get(position).getId() : mPlatoons.get(0).getId());
    }

    public PlatoonData getPlatoon(int position) {
        return mPlatoons.get(position);
    }

    public int getNumPersonas() {
        return mPersona.length;

    }

    public int getNumPlatoons() {
        return mPlatoons.size();

    }

    public List<PlatoonData> getPlatoons() {
        return mPlatoons;
    }

    public void generate() {
        mPersonaIdString.setLength(0);
        mPersonaString.setLength(0);
        mPersonaPlatformString.setLength(0);
        mPlatoonIdString.setLength(0);

        for (PersonaData p : mPersona) {
            mPersonaIdString.append(p.getId() + ":");
            mPersonaString.append(p.getName() + ":");
            mPersonaPlatformString.append(p.getPlatformId() + ":");
        }

        for (PlatoonData p : mPlatoons) {
            mPlatoonString.append(p.getName() + ":");
            mPlatoonIdString.append(p.getId() + ":");
        }
    }

    public final Object[] toArray() {
        if (mPersonaIdString == null) {
            generate();
        }

        return new Object[]{
            mAge,
            mUserId,
            mDateOfBirth,
            mLastlogin,
            mStatusMessageChanged, mName,
            mUsername,
            (mPresentation == null) ? "" : mPresentation,
            (mLocation == null) ? "" : mLocation,
            (mStatusMessage == null) ? "" : mStatusMessage,
            (mCurrentServer == null) ? "" : mCurrentServer,
            mPersonaIdString.toString(),
            mPersonaString.toString(),
            mPersonaPlatformString.toString(),
            mPlatoonIdString.toString(),
            mPlatoonString.toString(),
            mAllowFriendRequests ? "1" : "0",
            mOnline ? "1" : "0",
            mPlaying ? "1" : "0",
            mFriendStatus ? "1" : "0"
        };

    }

    @Override
    public String toString() {
        return "ProfileInformation{" +
                "mAge=" + mAge +
                ", mUserId=" + mUserId +
                ", mDateOfBirth=" + mDateOfBirth +
                ", mLastlogin=" + mLastlogin +
                ", mStatusMessageChanged=" + mStatusMessageChanged +
                ", mPersona=" + (mPersona == null ? null : Arrays.asList(mPersona)) +
                ", mName='" + mName + '\'' +
                ", mUsername='" + mUsername + '\'' +
                ", mPresentation='" + mPresentation + '\'' +
                ", mLocation='" + mLocation + '\'' +
                ", mStatusMessage='" + mStatusMessage + '\'' +
                ", mCurrentServer='" + mCurrentServer + '\'' +
                ", mAllowFriendRequests=" + mAllowFriendRequests +
                ", mOnline=" + mOnline +
                ", mPlaying=" + mPlaying +
                ", mFriendStatus=" + mFriendStatus +
                ", mPlatoons=" + mPlatoons +
                ", mPersonaString=" + mPersonaString +
                ", mPersonaIdString=" + mPersonaIdString +
                ", mPersonaPlatformString=" + mPersonaPlatformString +
                ", mPlatoonString=" + mPlatoonString +
                ", mPlatoonIdString=" + mPlatoonIdString +
                '}';
    }
}
