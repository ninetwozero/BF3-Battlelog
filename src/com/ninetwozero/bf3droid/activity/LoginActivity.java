package com.ninetwozero.bf3droid.activity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.util.Log;
import android.widget.Toast;

import com.ninetwozero.bf3droid.BF3Droid;
import com.ninetwozero.bf3droid.MainActivity;
import com.ninetwozero.bf3droid.dao.PlatoonInformationDAO;
import com.ninetwozero.bf3droid.dao.UserProfileDataDAO;
import com.ninetwozero.bf3droid.datatype.LoginResult;
import com.ninetwozero.bf3droid.datatype.SimplePersona;
import com.ninetwozero.bf3droid.datatype.SimplePlatoon;
import com.ninetwozero.bf3droid.datatype.UserInfo;
import com.ninetwozero.bf3droid.loader.Bf3Loader;
import com.ninetwozero.bf3droid.loader.CompletedTask;
import com.ninetwozero.bf3droid.provider.UriFactory;
import com.ninetwozero.bf3droid.provider.table.Personas;
import com.ninetwozero.bf3droid.provider.table.UserProfileData;
import com.ninetwozero.bf3droid.server.Bf3ServerCall;
import com.ninetwozero.bf3droid.util.HtmlParsing;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import static com.ninetwozero.bf3droid.dao.PersonasDAO.simplePersonaFrom;
import static com.ninetwozero.bf3droid.dao.PersonasDAO.simplePersonaToDB;
import static com.ninetwozero.bf3droid.dao.PlatoonInformationDAO.simplePlatoonFrom;
import static com.ninetwozero.bf3droid.dao.PlatoonInformationDAO.simplePlatoonToDatabase;
import static com.ninetwozero.bf3droid.dao.UserProfileDataDAO.userProfileDataToDB;

public class LoginActivity extends Bf3FragmentActivity {

    public static final String EMAIL = "email";
    public static final String PASSWORD = "password";

    private Bundle bundle;
    private final int LOGIN_ACTION = 1;
    private final int USER_DATA_ACTION = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.bundle = savedInstanceState;
    }

    @Override
    protected void onResume() {
        super.onResume();
        startLoginLoader();
    }

    private void startLoginLoader() {
        startLoadingDialog(LoginActivity.class.getSimpleName());
        getSupportLoaderManager().initLoader(LOGIN_ACTION, bundle, this);
    }


    private List<NameValuePair> formData() {
        List<NameValuePair> formData = new ArrayList<NameValuePair>();
        formData.add(new BasicNameValuePair("email", getIntent().getExtras().getString(EMAIL)));
        formData.add(new BasicNameValuePair("password", getIntent().getExtras().getString(PASSWORD)));
        formData.add(new BasicNameValuePair("redirect", ""));
        formData.add(new BasicNameValuePair("submit", "Sign+in"));
        return formData;
    }

    @Override
    public Loader<CompletedTask> onCreateLoader(int i, Bundle bundle) {
        if (i == LOGIN_ACTION) {
            return new Bf3Loader(getContext(), loginHttpData());
        } else {
            return new Bf3Loader(getContext(), userHttpData());
        }
    }

    private Bf3ServerCall.HttpData loginHttpData() {
        return new Bf3ServerCall.HttpData(UriFactory.getLogginUri(), formData(), HttpPost.METHOD_NAME, false);
    }

    private Bf3ServerCall.HttpData userHttpData() {     //Replace BF3Droid.getUser() with a username to check app on different profile
        return new Bf3ServerCall.HttpData(UriFactory.getProfileInformationUri(BF3Droid.getUser()), HttpGet.METHOD_NAME, false);
    }

    @Override
    public void onLoadFinished(Loader<CompletedTask> completedTaskLoader, CompletedTask completedTask) {
        if (isTaskSuccess(completedTask.result) && completedTaskLoader.getId() == LOGIN_ACTION) {
            processLoginResult(completedTask.response);
        } else if (isTaskSuccess(completedTask.result) && completedTaskLoader.getId() == USER_DATA_ACTION) {
            processUserDataResult(completedTask.response);
        } else {
            Log.e("MainActivity", "Login failed \n" + completedTask.response);
        }
    }

    private boolean isTaskSuccess(CompletedTask.Result result) {
        return result == CompletedTask.Result.SUCCESS;
    }

    private void processLoginResult(String response) {
        LoginResult result = new HtmlParsing().extractUserDetails(response);
        if (result.getError().equals("")) {
            savePostLoginData(result);
            fetchPersonaAndPlatoonData();
        } else {
            Toast.makeText(getContext(), result.getError(), Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getContext(), MainActivity.class));
        }
    }

    private void savePostLoginData(LoginResult result) {
        BF3Droid.setUser(result.getUserName());
        BF3Droid.setUserId(result.getUserId());
        BF3Droid.setCheckSum(result.getCheckSum());
    }

    private void saveForApplication(List<SimplePersona> personas, List<SimplePlatoon> platoons) {
        BF3Droid.setUserPersonas(personas);
        BF3Droid.setUserPlatoons(platoons);
    }

    private void fetchPersonaAndPlatoonData() {
        if (hasPersonas() && hasPlatoons()) {
            redirect();
        } else {
            getSupportLoaderManager().initLoader(USER_DATA_ACTION, bundle, this);
        }
    }

    private void redirect() {
        closeProgressDialog(LoginActivity.class.getSimpleName());
        if (BF3Droid.getUserPersonas().size() > 0) {
            startActivity(new Intent(getContext(), DashboardActivity.class));
        } else {
            startActivity(new Intent(getContext(), MainActivity.class));
        }
        finish();
    }

    private void processUserDataResult(String response) {
        HtmlParsing parser = new HtmlParsing();
        UserInfo userInfo = parser.extractUserInfo(response);
        saveForApplication(userInfo.getPersonas(), userInfo.getPlatoons());
        personasToDatabase(userInfo.getPersonas());
        platoonsToDatabase(userInfo.getPlatoons());
        userProfileDataToDatabase(userInfo.getUserProfileData());
        redirect();
    }

    private void personasToDatabase(List<SimplePersona> personas) {
        for (SimplePersona persona : personas) {
            ContentValues contentValues = simplePersonaToDB(persona, BF3Droid.getUserId());
            getContext().getContentResolver().insert(Personas.URI, contentValues);
        }
    }

    private void platoonsToDatabase(List<SimplePlatoon> platoons) {
        for (SimplePlatoon platoon : platoons) {
            ContentValues contentValues = simplePlatoonToDatabase(platoon, BF3Droid.getUserId());
            getContext().getContentResolver().insert(PlatoonInformationDAO.URI, contentValues);
        }
    }

    private void userProfileDataToDatabase(UserProfileData profileData) {
        ContentValues contentValues = userProfileDataToDB(profileData);
        getContext().getContentResolver().insert(UserProfileDataDAO.URI, contentValues);
    }

    private boolean hasPersonas() {
        Cursor cursor = personasQuery();

        if (cursor.getCount() > 0) {
            List<SimplePersona> personas = new ArrayList<SimplePersona>();
            cursor.moveToFirst();
            while (!cursor.isAfterLast()){
                personas.add(simplePersonaFrom(cursor));
                cursor.moveToNext();
            }
            cursor.close();
            BF3Droid.setUserPersonas(personas);
            return true;
        }
        cursor.close();
        return false;
    }

    private Cursor personasQuery() {
        return getContext().getContentResolver().query(
                Personas.URI,
                Personas.PERSONAS_PROJECTION,
                Personas.Columns.USER_ID + "=?",
                new String[]{String.valueOf(BF3Droid.getUserId())},
                null
        );
    }

    private boolean hasPlatoons() {
        Cursor cursor = platoonsQuery();

        if (cursor.getCount() > 0) {
            List<SimplePlatoon> platoons = new ArrayList<SimplePlatoon>();
            cursor.moveToFirst();
            while(!cursor.isAfterLast()) {
                platoons.add(simplePlatoonFrom(cursor));
                cursor.moveToNext();
            }
            cursor.close();
            BF3Droid.setUserPlatoons(platoons);
            return true;
        }
        cursor.close();
        return false;
    }

    private Cursor platoonsQuery() {
        return getContext().getContentResolver().query(
                PlatoonInformationDAO.URI,
                PlatoonInformationDAO.SIMPLE_PLATOON_PROJECTION,
                PlatoonInformationDAO.Columns.USER_ID + "=?",
                new String[]{String.valueOf(BF3Droid.getUserId())},
                null
        );
    }

    private Context getContext() {
        return getApplicationContext();
    }

    @Override
    public void onLoaderReset(Loader<CompletedTask> completedTaskLoader) {
    }
}
