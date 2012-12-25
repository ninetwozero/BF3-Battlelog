package com.ninetwozero.bf3droid.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.ninetwozero.bf3droid.BF3Droid;
import com.ninetwozero.bf3droid.datatype.PlatoonData;
import com.ninetwozero.bf3droid.datatype.PlatoonInformation;
import com.ninetwozero.bf3droid.datatype.SimplePlatoon;

public class PlatoonInformationDAO {
	public static final Uri URI = Uri.parse("content://" + BF3Droid.AUTHORITY + "/platoon/");
	
	public static PlatoonData getPlatoonDataFromCursor(Cursor cursor) {
    	if( !cursor.moveToFirst() ) {
    		cursor.close();
    		return new PlatoonData(0, "NO PLATOON FOUND", "N/A", 0, 0, 0, true);
    	}
    	PlatoonData platoon = new PlatoonData(
    		cursor.getLong(cursor.getColumnIndex(Columns.PLATOON_ID)),
    		cursor.getString(cursor.getColumnIndex(Columns.NAME)),
    		cursor.getString(cursor.getColumnIndex(Columns.TAG)),
    		cursor.getInt(cursor.getColumnIndex(Columns.PLATFORM_ID)),
    		cursor.getInt(cursor.getColumnIndex(Columns.NUM_FANS)),
    		cursor.getInt(cursor.getColumnIndex(Columns.NUM_MEMBERS)),
    		true
		);
		cursor.close();
		return platoon;
	}

    public static SimplePlatoon simplePlatoonFrom(Cursor cursor){
        String name = cursor.getString(cursor.getColumnIndex(Columns.NAME));
        long platoonId = cursor.getLong(cursor.getColumnIndex(Columns.PLATOON_ID));
        String image = cursor.getString(cursor.getColumnIndex(Columns.BADGE));
        String platform = cursor.getString(cursor.getColumnIndex(Columns.PLATFORM));
        return new SimplePlatoon(name, platoonId, image, platform);
    }
	
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

    public static ContentValues platoonDataForDB(final PlatoonData p) {
        ContentValues values = new ContentValues();
        values.put(Columns.PLATOON_ID, p.getId());
        values.put(Columns.NAME, p.getName());
        values.put(Columns.TAG, p.getTag());
        values.put(Columns.PLATFORM_ID, p.getPlatformId());
        values.put(Columns.NUM_FANS, p.getCountFans());
        values.put(Columns.NUM_MEMBERS, p.getCountMembers());
        values.put(Columns.TIMESTAMP, 0);
        return values;
    }
    
    public static ContentValues platoonInformationForDB(final PlatoonInformation pi, long timestamp) {
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

    public static ContentValues simplePlatoonToDatabase(SimplePlatoon platoon, long userId){
        ContentValues values = new ContentValues();
        values.put(Columns.PLATOON_ID, platoon.getPlatoonId());
        values.put(Columns.USER_ID, userId);
        values.put(Columns.NAME, platoon.getName());
        values.put(Columns.BADGE, platoon.getPlatoonBadge());
        values.put(Columns.PLATFORM, platoon.getPlatform());
        return values;
    }
    	
	public final class Columns {
    	public final static String PLATOON_ID = "platoonId";
        public final static String USER_ID = "userId";
    	public final static String NAME = "name";
    	public final static String TAG = "tag";
    	public final static String PLATFORM_ID = "platformId";
        public final static String PLATFORM = "platform";
    	public final static String GAME_ID = "gameId";
    	public final static String DATE_CREATED = "dateCreated";
    	public final static String WEBSITE = "website";
    	public final static String PRESENTATION = "presentation";
    	public final static String NUM_FANS = "numberOfFans";
    	public final static String NUM_MEMBERS = "numberOfMembers";
    	public final static String BLAZE_CLUB_ID = "blazeClubId";
    	public final static String TIMESTAMP = "timestamp";
        public static final String BADGE = "badge";
	};
	
	public static String[] getSmallerProjection() {
		return new String[] {
			"_ID",
			Columns.PLATOON_ID,	
			Columns.NAME,
			Columns.TAG,
			Columns.PLATFORM_ID,
			Columns.NUM_FANS,
			Columns.NUM_MEMBERS
		};
	}

    public static final String[] SIMPLE_PLATOON_PROJECTION = new String[]{Columns.PLATOON_ID, Columns.NAME,
            Columns.BADGE, Columns.PLATFORM};
}
