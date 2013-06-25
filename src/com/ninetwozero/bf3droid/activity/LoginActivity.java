package com.ninetwozero.bf3droid.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.util.Log;
import android.widget.Toast;

import com.ninetwozero.bf3droid.BF3Droid;
import com.ninetwozero.bf3droid.MainActivity;
import com.ninetwozero.bf3droid.activity.profile.soldier.restorer.UserInfoRestorer;
import com.ninetwozero.bf3droid.datatype.LoginResult;
import com.ninetwozero.bf3droid.datatype.SimplePersona;
import com.ninetwozero.bf3droid.datatype.SimplePlatoon;
import com.ninetwozero.bf3droid.datatype.UserInfo;
import com.ninetwozero.bf3droid.loader.Bf3Loader;
import com.ninetwozero.bf3droid.loader.CompletedTask;
import com.ninetwozero.bf3droid.model.User;
import com.ninetwozero.bf3droid.provider.UriFactory;
import com.ninetwozero.bf3droid.server.Bf3ServerCall;
import com.ninetwozero.bf3droid.util.HtmlParsing;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

public class LoginActivity extends Bf3FragmentActivity {

    public static final String EMAIL = "email";
    public static final String PASSWORD = "password";

    private Bundle bundle;
    private final int LOGIN_ACTION = 1;
    private final int USER_DATA_ACTION = 2;
    private String loggedUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.bundle = savedInstanceState;
        startLoginLoader();
    }

    @Override
    protected void onResume() {
        super.onResume();
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
        return new Bf3ServerCall.HttpData(UriFactory.getLogginUri(), formData(), HttpPost.METHOD_NAME, false, new Header[]{});
    }

    private Bf3ServerCall.HttpData userHttpData() {     //Replace BF3Droid.getUser() with a username to check app on different profile
        return new Bf3ServerCall.HttpData(UriFactory.getProfileInformationUri(loggedUserName), HttpGet.METHOD_NAME, false);
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
        LoginResult result = new HtmlParsing(response).extractUserDetails();
        if (result.getError().equals("")) {
            savePostLoginData(result);
            fetchPersonaAndPlatoonData();
        } else {
            Toast.makeText(getContext(), result.getError(), Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getContext(), MainActivity.class));
        }
    }

    private void savePostLoginData(LoginResult result) {
        loggedUserName = result.getUserName();
        BF3Droid.setCheckSum(result.getCheckSum());
    }

    private void saveForApplication(List<SimplePersona> personas, List<SimplePlatoon> platoons) {
        user().setPersonas(personas);
        user().setPlatoons(platoons);
    }

    private void fetchPersonaAndPlatoonData() {
        getSupportLoaderManager().initLoader(USER_DATA_ACTION, bundle, this);
    }

    private void redirect() {
        closeLoadingDialog(LoginActivity.class.getSimpleName());
        if (user().getPersonas().size() > 0) {
            startActivity(new Intent(getContext(), DashboardActivity.class));
        } else {
            startActivity(new Intent(getContext(), MainActivity.class));
        }
        finish();
    }

    private void processUserDataResult(String response) {
        HtmlParsing parser = new HtmlParsing(response);
        BF3Droid.setUser(new User(loggedUserName, parser.logedUserId()));
        UserInfo userInfo = parser.extractUserInfo();
        saveForApplication(userInfo.getPersonas(), userInfo.getPlatoons());
        new UserInfoRestorer(getContext(), User.USER).save(userInfo);
        redirect();
    }

    private Context getContext() {
        return getApplicationContext();
    }

    @Override
    public void onLoaderReset(Loader<CompletedTask> completedTaskLoader) {
    }

    private User user(){
        return BF3Droid.getUser();
    }
}
