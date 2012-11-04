package com.ninetwozero.battlelog.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.ninetwozero.battlelog.Battlelog;
import com.ninetwozero.battlelog.datatype.PlatoonInformation;

public class PlatoonInformationDAO {
	public static final Uri URI = Uri.parse("content://" + Battlelog.AUTHORITY + "/platoon/");
	
    public static PlatoonInformation getPlatoonInformationFromCursor(Cursor cursor) {
    	if( !cursor.moveToFirst() ) {
    		cursor.close();
    		return null;
    	}
        PlatoonInformation.Builder platoon = new PlatoonInformation.Builder(
    		cursor.getLong(cursor.getColumnIndex(Columns.PLATOON_ID)),
    		cursor.getString(cursor.getColumnIndex(Columns.NAME)),
    		cursor.getString(cursor.getColumnIndex(Columns.TAG))
		);
        platoon = platoon
        	.platformId(cursor.getInt(cursor.getColumnIndex(Columns.PLATFORM_ID)))
        	.gameId(cursor.getInt(cursor.getColumnIndex(Columns.GAME_ID)))
        	.date(cursor.getLong(cursor.getColumnIndex(Columns.DATE_CREATED)))
    		.website(cursor.getString(cursor.getColumnIndex(Columns.WEBSITE)))
        	.presentation(cursor.getString(cursor.getColumnIndex(Columns.PRESENTATION)))
        	.numFans(cursor.getInt(cursor.getColumnIndex(Columns.NUM_FANS)))
    		.numMembers(cursor.getInt(cursor.getColumnIndex(Columns.NUM_MEMBERS)))
    		.blazeClubId(cursor.getLong(cursor.getColumnIndex(Columns.BLAZE_CLUB_ID)))
    		.timestamp(cursor.getLong(cursor.getColumnIndex(Columns.TIMESTAMP)));
        cursor.close();
        return platoon.build();
    }

    public static PlatoonInformation getPlatoonInformationFromJSON(PlatoonInformation pi) {
    	return pi;
    }

    public static ContentValues getPlatoonInformationForDB(final PlatoonInformation pi, long timestamp) {
        ContentValues values = new ContentValues();
        values.put(Columns.PLATOON_ID, pi.getId());
        values.put(Columns.NAME, pi.getName());
        values.put(Columns.TAG, pi.getTag());
        values.put(Columns.PLATFORM_ID, pi.getPlatformId());
        values.put(Columns.GAME_ID, pi.getGameId());
        values.put(Columns.DATE_CREATED, pi.getDateCreated());
        values.put(Columns.WEBSITE, pi.getWebsite());
        values.put(Columns.PRESENTATION, pi.getPresentation());
        values.put(Columns.NUM_FANS, pi.getNumFans());
        values.put(Columns.NUM_MEMBERS, pi.getNumMembers());
        values.put(Columns.BLAZE_CLUB_ID, pi.getBlazeClubId());
        values.put(Columns.TIMESTAMP, timestamp);
        return values;
    }

    private static String getValueFromCursor(Cursor cursor, String name) {
        return cursor.getString(cursor.getColumnIndexOrThrow(name));
    }
    	
	public final class Columns {
    	public final static String PLATOON_ID = "platoonId";
    	public final static String NAME = "name";
    	public final static String TAG = "tag";
    	public final static String PLATFORM_ID = "platformId";
    	public final static String GAME_ID = "gameId";
    	public final static String DATE_CREATED = "dateCreated";
    	public final static String WEBSITE = "website";
    	public final static String PRESENTATION = "presentation";
    	public final static String NUM_FANS = "numberOfFans";
    	public final static String NUM_MEMBERS = "numberOfMembers";
    	public final static String BLAZE_CLUB_ID = "blazeClubId";
    	public final static String TIMESTAMP = "timestamp";
    };
}
