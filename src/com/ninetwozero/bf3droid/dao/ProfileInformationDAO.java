package com.ninetwozero.bf3droid.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.ninetwozero.bf3droid.Battlelog;
import com.ninetwozero.bf3droid.datatype.ProfileInformation;

public class ProfileInformationDAO {
	public static final Uri URI = Uri.parse("content://" + Battlelog.AUTHORITY + "/profile/");
	
    public static ProfileInformation getProfileInformationFromCursor(Cursor cursor) {
    	if( !cursor.moveToFirst() ) {
    		cursor.close();
    		return null;
    	}
        ProfileInformation.Builder tempProfile = new ProfileInformation.Builder(
    		cursor.getLong(cursor.getColumnIndex(Columns.USER_ID)),
    		cursor.getString(cursor.getColumnIndex(Columns.USERNAME))
		);
        
        ProfileInformation profile = tempProfile
			.name(cursor.getString(cursor.getColumnIndex(Columns.NAME)))		
	        .age(cursor.getInt(cursor.getColumnIndex(Columns.AGE)))
	        .birthday(cursor.getLong(cursor.getColumnIndex(Columns.BIRTHDAY)))
			.location(cursor.getString(cursor.getColumnIndex(Columns.LOCATION)))
			.loginDate(cursor.getLong(cursor.getColumnIndex(Columns.LOGIN_DATE)))
			.presentation(cursor.getString(cursor.getColumnIndex(Columns.PRESENTATION)))
			.statusMessage(cursor.getString(cursor.getColumnIndex(Columns.STATUS)))
			.statusDate(cursor.getLong(cursor.getColumnIndex(Columns.STATUS_DATE)))
			.personaIdString(cursor.getString(cursor.getColumnIndex(Columns.PERSONA_ID)))
			.personaNameString(cursor.getString(cursor.getColumnIndex(Columns.PERSONA_NAME)))
			.personaPlatformString(cursor.getString(cursor.getColumnIndex(Columns.PERSONA_PLATFORM)))
			.platoonIdString(cursor.getString(cursor.getColumnIndex(Columns.PLATOON_ID)))
			.platoonNameString(cursor.getString(cursor.getColumnIndex(Columns.PLATOON_NAME)))
			.timestamp(cursor.getLong(cursor.getColumnIndex(Columns.TIMESTAMP)))
			.build();
        
        profile.generateFromSerializedState();
        cursor.close();
        return profile;
    }

    /* TODO: Fix with loader */
    public static ProfileInformation getProfileInformationFromJSON(ProfileInformation pi) {
    	pi.generateSerializedState();
    	return pi;
    }

    public static ContentValues convertProfileInformationForDB(final ProfileInformation pi, long timestamp) {
        ContentValues values = new ContentValues();
        values.put(Columns.USER_ID, pi.getUserId());
        values.put(Columns.USERNAME, pi.getUsername());
        values.put(Columns.NAME, pi.getName());
        values.put(Columns.AGE, pi.getAge());
        values.put(Columns.BIRTHDAY, pi.getBirthday());
        values.put(Columns.LOCATION, pi.getLocation());
        values.put(Columns.LOGIN_DATE, pi.getLastLogin());
        values.put(Columns.PRESENTATION, pi.getPresentation());
        values.put(Columns.PERSONA_ID, pi.getSerializedPersonaIds());
        values.put(Columns.PERSONA_NAME, pi.getSerializedPersonaNames());
        values.put(Columns.PERSONA_PLATFORM, pi.getSerializedPersonaPlatforms());
        values.put(Columns.PLATOON_ID, pi.getSerializedPlatoonIds());
        values.put(Columns.PLATOON_NAME, pi.getSerializedPlatoonNames());
        values.put(Columns.STATUS, pi.getStatusMessage());
        values.put(Columns.STATUS_DATE, pi.getStatusMessageChanged());
        values.put(Columns.TIMESTAMP, timestamp);
        return values;
    }
    	
	public final class Columns {
    	public final static String USER_ID = "userId";
    	public final static String USERNAME = "username";
    	public final static String NAME = "name";
    	public final static String LOCATION = "location";
    	public final static String AGE = "age";
    	public final static String BIRTHDAY = "birthday";
    	public final static String STATUS = "status";
    	public final static String STATUS_DATE = "statusDate";
    	public final static String PRESENTATION = "presentation";
    	public final static String LOGIN_DATE = "loginDate";
    	public final static String PERSONA_ID= "personaId";
    	public final static String PERSONA_NAME = "personaName";
    	public final static String PERSONA_PLATFORM = "personaPlatform";
    	public final static String PLATOON_ID = "platoonId";
    	public final static String PLATOON_NAME = "platoonName";
    	public final static String TIMESTAMP = "timestamp";
    };
}
