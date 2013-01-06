package com.ninetwozero.bf3droid.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import com.ninetwozero.bf3droid.BF3Droid;
import com.ninetwozero.bf3droid.provider.table.UserProfileData;

public class UserProfileDataDAO {
    public static final Uri URI = Uri.parse("content://" + BF3Droid.AUTHORITY + "/profile/");

    public static ContentValues userProfileDataToDB(UserProfileData profileData){
        ContentValues values = new ContentValues();
        values.put(Columns.USER_ID, profileData.getUserId());
        values.put(Columns.USERNAME, profileData.getUsername());
        values.put(Columns.NAME, profileData.getName());
        values.put(Columns.AGE, profileData.getAge());
        values.put(Columns.ENLISTED, profileData.getEnlisted());
        values.put(Columns.LAST_SEEN, profileData.getLastSeen());
        values.put(Columns.PRESENTATION, profileData.getPresentation());
        values.put(Columns.COUNTRY, profileData.getCountry());
        values.put(Columns.VETERAN_STATUS, profileData.getVeteranStatus());
        values.put(Columns.STATUS_MESSAGE, profileData.getStatusMessage());
        return values;
    }

    public static UserProfileData userProfileDataFrom(Cursor cursor){
        long userId = cursor.getLong(cursor.getColumnIndex(Columns.USER_ID));
        String username = cursor.getString(cursor.getColumnIndex(Columns.USERNAME));
        String name = cursor.getString(cursor.getColumnIndex(Columns.NAME));
        String age = cursor.getString(cursor.getColumnIndex(Columns.AGE));
        String enlisted = cursor.getString(cursor.getColumnIndex(Columns.ENLISTED));
        String lastSeen = cursor.getString(cursor.getColumnIndex(Columns.LAST_SEEN));
        String presentation = cursor.getString(cursor.getColumnIndex(Columns.PRESENTATION));
        String country = cursor.getString(cursor.getColumnIndex(Columns.COUNTRY));
        int veteranStatus = cursor.getInt(cursor.getColumnIndex(Columns.VETERAN_STATUS));
        String statusMessage = cursor.getString(cursor.getColumnIndex(Columns.STATUS_MESSAGE));;
        return new UserProfileData(userId, username, name, age, enlisted, lastSeen, presentation, country
                , veteranStatus, statusMessage);
    }

    public final class Columns{
        public static final String USER_ID = "userId";
        public static final String USERNAME = "username";
        public static final String NAME = "name";
        public static final String AGE = "age";
        public static final String ENLISTED = "enlisted";
        public static final String LAST_SEEN = "lastSeen";
        public static final String PRESENTATION = "presentation";
        public static final String COUNTRY = "country";
        public static final String VETERAN_STATUS = "veteranStatus";
        public static final String STATUS_MESSAGE = "statusMessage";
    }
}
