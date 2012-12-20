/*
	This file is part of BF3 Droid

    BF3 Droid is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    BF3 Droid is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
 */

package com.ninetwozero.bf3droid.datatype;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;

import com.ninetwozero.bf3droid.R;
import com.ninetwozero.bf3droid.misc.PublicUtils;

public class ProfileInformation {

    // Attributes
    private long mUserId;
    private String mUsername;
    private String mName;
    private int mAge;
    private long mBirthday;
    private String mLocation;
    private String mCurrentServer;
    private String mStatusMessage;
    private String mPresentation;
    private long mLastlogin;
    private long mStatusMessageChanged;
    private PersonaData[] mPersona;
    private boolean mAllowFriendRequests;
    private boolean mOnline;
    private boolean mPlaying;
    private boolean mFriendStatus;
    private List<PlatoonData> mPlatoon;
    private long mTimestamp;

    // Other
    private StringBuilder mPersonaNameString = new StringBuilder();
    private StringBuilder mPersonaIdString = new StringBuilder();
    private StringBuilder mPersonaPlatformString = new StringBuilder();
    private StringBuilder mPlatoonNameString = new StringBuilder();
    private StringBuilder mPlatoonIdString = new StringBuilder();

    public ProfileInformation(final Builder builder) {
        mUserId = builder.id;
        mUsername = builder.username;
        mName = builder.name;
        mAge = builder.age;
        mLocation = builder.location;
        mBirthday = builder.birthday;
        mLastlogin = builder.loginDate;
        mStatusMessageChanged = builder.statusDate;
        mPersona = builder.personas.clone();
        mPresentation = builder.presentation;
        mStatusMessage = builder.statusMessage;
        mCurrentServer = builder.currentServer;
        mAllowFriendRequests = builder.allowFriendRequests;
        mOnline = builder.online;
        mPlaying = builder.playing;
        mFriendStatus = builder.friend;
        mPlatoon = builder.platoons;
        mTimestamp = builder.timestamp;
        
        mPersonaIdString.append(builder.personaIdString);
        mPersonaNameString.append(builder.personaNameString);
        mPersonaPlatformString.append(builder.personaPlatformString);
        mPlatoonIdString.append(builder.platoonIdString);
        mPlatoonNameString.append(builder.platoonNameString);
    }

    public int getAge() {
        return mAge;
    }

    public long getUserId() {
        return mUserId;
    }

    public long getBirthday() {
        return mBirthday;
    }

    public long getLastLogin() {
    	return mLastlogin;
    }
    public String getLastLoginString(Context c) {
        return PublicUtils.getRelativeDate(c, mLastlogin,R.string.info_lastlogin);
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
        return ((mPlatoon.size() < position) ? mPlatoon.get(position).getId() : mPlatoon.get(0).getId());
    }

    public PlatoonData getPlatoon(int position) {
        return mPlatoon.get(position);
    }

    public int getNumPersonas() {
        return mPersona.length;

    }

    public int getNumPlatoons() {
        return mPlatoon.size();

    }

    public List<PlatoonData> getPlatoons() {
        return mPlatoon;
    }
    
    public long getTimestamp() {
    	return mTimestamp;
    }
    
    public void setPlatoons(List<PlatoonData> p) {
    	mPlatoon = p;
    }
    
    public void setSerializedPersonaIds(String s) {
    	mPersonaIdString.setLength(0);
    	mPersonaIdString.append(s);
    }
    
    public void setSerializedPersonaNames(String s) {
    	mPersonaNameString.setLength(0);
    	mPersonaNameString.append(s);
    }
    
    public void setSerializedPersonaPlatforms(String s) {
    	mPersonaPlatformString.setLength(0);
    	mPersonaPlatformString.append(s);
    }
    
    public void setSerializedPlatoonIds(String s) {
    	mPlatoonIdString.setLength(0);
    	mPlatoonIdString.append(s);
    }

    public void setSerializedPlatoonNames(String s) {
    	mPlatoonNameString.setLength(0);
    	mPlatoonNameString.append(s);
    }
    
    public String getSerializedPersonaIds() {
    	return mPersonaIdString.toString();
    }
    
    public String getSerializedPersonaNames() {
    	return mPersonaNameString.toString();
    }

    public String getSerializedPersonaPlatforms() {
    	return mPersonaPlatformString.toString();
    }

    public String getSerializedPlatoonIds() {
    	return mPlatoonIdString.toString();
    }
    
    public String getSerializedPlatoonNames() {
    	return mPlatoonNameString.toString();
    }
    
    public void generateFromSerializedState() {
    	String[] personaIdArray = mPersonaIdString.toString().split(":");
    	String[] personaNameArray = mPersonaNameString.toString().split(":");
    	String[] personaPlatoonArray = mPersonaPlatformString.toString().split(":");
    	String[] platoonIdArray = mPlatoonIdString.toString().split(":");
    	String[] platoonNameArray = mPlatoonNameString.toString().split(":");
    	
    	int numPersonas = personaIdArray.length;
    	int numPlatoons = platoonIdArray.length;
    	
    	mPersona = new PersonaData[numPersonas];
    	for (int i = 0; i < numPersonas; i++) {
    		mPersona[i] = new PersonaData(
    			Long.parseLong(personaIdArray[i]),
    			personaNameArray[i],
    			Integer.parseInt(personaPlatoonArray[i]),
    			""
			);
    	}    	
    	
    	mPlatoon = new ArrayList<PlatoonData>();
    	for (int i = 0; i < numPlatoons; i++) {
    		if( platoonIdArray[i].equals("") ) continue;
    		mPlatoon.add(
				new PlatoonData(
	    			Long.parseLong(platoonIdArray[i]),
	    			platoonNameArray[i]
				)
			);
    	}
    }
    
    public void generateSerializedState() {
        mPersonaIdString.setLength(0);
        mPersonaNameString.setLength(0);
        mPersonaPlatformString.setLength(0);
        mPlatoonIdString.setLength(0);
        mPlatoonNameString.setLength(0);
        
        for (PersonaData p : mPersona) {
            mPersonaIdString.append(p.getId() + ":");
            mPersonaNameString.append(p.getName() + ":");
            mPersonaPlatformString.append(p.getPlatformId() + ":");
        }

        for (PlatoonData p : mPlatoon) {
            mPlatoonIdString.append(p.getId() + ":");
            mPlatoonNameString.append(p.getName() + ":");
        }
    }
    
    public static class Builder {
    	private final long id;
        private final String username;

        // Optional params
        private int age = 0;
        private long birthday = 0;
        private long loginDate = 0;
        private long statusDate = 0;
        private PersonaData[] personas = new PersonaData[0];
        private String name = "";
        private String presentation = "";
        private String location = "";
        private String statusMessage = "";
        private String currentServer = "";
        private boolean allowFriendRequests = false;
        private boolean online = false;
        private boolean playing = false;
        private boolean friend = false;
        private List<PlatoonData> platoons = new ArrayList<PlatoonData>();
        private long timestamp;
        
        private String personaNameString = "";
        private String personaIdString = "";
        private String personaPlatformString = "";
        private String platoonNameString = "";
        private String platoonIdString = "";
        
        public Builder(long i, String u) {
            id = i;
            username = u;
        }
        
        public Builder age(int n) {
        	age = n;
        	return this;
        }

        public Builder birthday(long n) {
        	birthday = n;
        	return this;
        }
        
        public Builder loginDate(long n) {
        	loginDate = n;
        	return this;
        }
        
        public Builder statusDate(long n) {
        	statusDate = n;
        	return this;
        }

        public Builder name(String s) {
        	name = s;
        	return this;
        }
        
        public Builder presentation(String s) {
        	presentation = s;
        	return this;
        }
        
        public Builder location(String s) {
        	location = s;
        	return this;
        }
        
        public Builder statusMessage(String s) {
        	statusMessage = s;
        	return this;
        }
        
        public Builder server(String s) {
        	currentServer = s;
        	return this;
        }
        
        public Builder allowFriendRequests(boolean b) {
        	allowFriendRequests = b;
        	return this;
        }
        
        public Builder isOnline(boolean b) {
            online = b;
            return this;
        }

        public Builder isPlaying(boolean b) {
            playing = b;
            return this;
        }

        public Builder isFriend(boolean b) {
            friend = b;
            return this;
        }

        public Builder persona(PersonaData... p) {
            personas = p;
            return this;
        }

        public Builder platoons(List<PlatoonData> p) {
        	platoons = p;
        	return this;
        }
        
        public Builder personaIdString(String s) {
        	personaIdString = s;
        	return this;
        }
        
        public Builder personaNameString(String s) {
        	personaNameString = s;
        	return this;
        }
        
        public Builder personaPlatformString(String s) {
        	personaPlatformString = s;
        	return this;
        }
        
        public Builder platoonIdString(String s) {
        	platoonIdString = s;
        	return this;
        }
        
        public Builder platoonNameString(String s) {
        	platoonNameString = s;
        	return this;
        }


    	public Builder timestamp(long n) {
    		timestamp = n;
    		return this;
    	}
        
        public ProfileInformation build() {
            return new ProfileInformation(this);
        }
    }

    @Override
    public String toString() {
        return "ProfileInformation{" +
                "mAge=" + mAge +
                ", mUserId=" + mUserId +
                ", mDateOfBirth=" + mBirthday +
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
                ", mPlatoons=" + mPlatoon +
                ", mPersonaNameString=" + mPersonaNameString +
                ", mPersonaIdString=" + mPersonaIdString +
                ", mPersonaPlatformString=" + mPersonaPlatformString +
                ", mPlatoonNameString=" + mPlatoonNameString +
                ", mPlatoonIdString=" + mPlatoonIdString +
                '}';
    }
}
