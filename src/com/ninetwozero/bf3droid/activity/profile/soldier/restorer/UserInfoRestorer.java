package com.ninetwozero.bf3droid.activity.profile.soldier.restorer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.ninetwozero.bf3droid.BF3Droid;
import com.ninetwozero.bf3droid.dao.PlatoonInformationDAO;
import com.ninetwozero.bf3droid.dao.UserProfileDataDAO;
import com.ninetwozero.bf3droid.datatype.SimplePersona;
import com.ninetwozero.bf3droid.datatype.SimplePlatoon;
import com.ninetwozero.bf3droid.datatype.UserInfo;
import com.ninetwozero.bf3droid.model.User;
import com.ninetwozero.bf3droid.provider.table.Personas;
import com.ninetwozero.bf3droid.provider.table.UserProfileData;
import com.ninetwozero.bf3droid.service.Restorer;

import java.util.ArrayList;
import java.util.List;

import static com.ninetwozero.bf3droid.dao.PersonasDAO.simplePersonaFrom;
import static com.ninetwozero.bf3droid.dao.PersonasDAO.simplePersonaToDB;
import static com.ninetwozero.bf3droid.dao.PlatoonInformationDAO.simplePlatoonFrom;
import static com.ninetwozero.bf3droid.dao.PlatoonInformationDAO.simplePlatoonToDatabase;
import static com.ninetwozero.bf3droid.dao.UserProfileDataDAO.userProfileDataToDB;

public class UserInfoRestorer extends Restorer<UserInfo> {

    private final Context context;
    private final String user;

    public UserInfoRestorer(Context context, String user) {
        this.context = context;
        this.user = user;
    }

    @Override
    public UserInfo fetch() {
        List<SimplePersona> personas = getPersonas();
        List<SimplePlatoon> platoons = getPlatoons();
        UserProfileData userProfile = getUserProfile();
        return new UserInfo(personas, platoons, userProfile);
    }

    @Override
    public void save(UserInfo userInfo) {
        personasToDatabase(userInfo.getPersonas());
        platoonsToDatabase(userInfo.getPlatoons());
        userProfileDataToDatabase(userInfo.getUserProfileData());
    }

    private List<SimplePersona> getPersonas() {
        Cursor cursor = personasQuery();

        if (cursor.getCount() > 0) {
            List<SimplePersona> personas = new ArrayList<SimplePersona>();
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                personas.add(simplePersonaFrom(cursor));
                cursor.moveToNext();
            }
            cursor.close();
            return personas;
        }
        cursor.close();
        return new ArrayList<SimplePersona>();
    }

    private Cursor personasQuery() {
        return context.getContentResolver().query(
                Personas.URI,
                Personas.PERSONAS_PROJECTION,
                Personas.Columns.USER_ID + "=?",
                new String[]{String.valueOf(getUserId())},
                null
        );
    }

    private List<SimplePlatoon> getPlatoons() {
        Cursor cursor = platoonsQuery();

        if (cursor.getCount() > 0) {
            List<SimplePlatoon> platoons = new ArrayList<SimplePlatoon>();
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                platoons.add(simplePlatoonFrom(cursor));
                cursor.moveToNext();
            }
            cursor.close();
            return platoons;
        }
        cursor.close();
        return new ArrayList<SimplePlatoon>();
    }

    private Cursor platoonsQuery() {
        return context.getContentResolver().query(
                PlatoonInformationDAO.URI,
                PlatoonInformationDAO.SIMPLE_PLATOON_PROJECTION,
                PlatoonInformationDAO.Columns.USER_ID + "=?",
                new String[]{String.valueOf(getUserId())},
                null
        );
    }

    private UserProfileData getUserProfile() {
        Cursor cursor = context.getContentResolver().query(
                UserProfileDataDAO.URI,
                UserProfileDataDAO.PROJECTION,
                UserProfileDataDAO.Columns.USER_ID + "=?",
                new String[]{String.valueOf(user().getId())},
                null
        );
        if (cursor.getCount() > 0) {
            UserProfileData userProfileData = UserProfileDataDAO.userProfileDataFrom(cursor);
            cursor.close();
            return userProfileData;
        } else {
            return null;
        }
    }

    private void personasToDatabase(List<SimplePersona> personas) {
        for (SimplePersona persona : personas) {
            ContentValues contentValues = simplePersonaToDB(persona, getUserId());
            context.getContentResolver().insert(Personas.URI, contentValues);
        }
    }

    private void platoonsToDatabase(List<SimplePlatoon> platoons) {
        for (SimplePlatoon platoon : platoons) {
            ContentValues contentValues = simplePlatoonToDatabase(platoon, getUserId());
            context.getContentResolver().insert(PlatoonInformationDAO.URI, contentValues);
        }
    }

    private void userProfileDataToDatabase(UserProfileData profileData) {
        ContentValues contentValues = userProfileDataToDB(profileData);
        context.getContentResolver().insert(UserProfileDataDAO.URI, contentValues);
    }

    private long getUserId() {
        return user().getId();
    }

    private User user() {
        if (user.equals(User.USER)) {
            return BF3Droid.getUser();
        } else {
            return BF3Droid.getGuest();
        }
    }
}
