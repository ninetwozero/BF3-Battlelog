package com.ninetwozero.bf3droid.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.util.Log;
import android.widget.Toast;
import com.ninetwozero.bf3droid.Battlelog;
import com.ninetwozero.bf3droid.MainActivity;
import com.ninetwozero.bf3droid.loader.Bf3Loader;
import com.ninetwozero.bf3droid.loader.CompletedTask;
import com.ninetwozero.bf3droid.provider.UriFactory;
import com.ninetwozero.bf3droid.server.Bf3ServerCall;
import com.ninetwozero.bf3droid.util.HtmlParsing;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends Bf3FragmentActivity{

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

    private void startLoginLoader(){
        startLoadingDialog();
        getSupportLoaderManager().initLoader(LOGIN_ACTION, bundle, this);
    }


    private List<NameValuePair> formData(){
        List<NameValuePair> formData = new ArrayList<NameValuePair>();
        formData.add(new BasicNameValuePair("email", getIntent().getExtras().getString(EMAIL)));
        formData.add(new BasicNameValuePair("password", getIntent().getExtras().getString(PASSWORD)));
        formData.add(new BasicNameValuePair("redirect", ""));
        formData.add(new BasicNameValuePair("submit", "Sign+in"));
        return formData;
    }

    @Override
    public Loader<CompletedTask> onCreateLoader(int i, Bundle bundle) {
        if(i == LOGIN_ACTION){
        return new Bf3Loader(getContext(), loginHttpData());
        } else{
             return new Bf3Loader(getContext(), userHttpData());
        }
    }

    private Bf3ServerCall.HttpData loginHttpData() {
        return new Bf3ServerCall.HttpData(UriFactory.getLogginUri(), formData(), HttpPost.METHOD_NAME, false);
    }

    private Bf3ServerCall.HttpData userHttpData() {
        return new Bf3ServerCall.HttpData(UriFactory.getProfileInformationUri(Battlelog.getUser()), HttpGet.METHOD_NAME, false);
    }

    @Override
    public void onLoadFinished(Loader<CompletedTask> completedTaskLoader, CompletedTask completedTask) {
        if(isTaskSuccess(completedTask.result) && completedTaskLoader.getId() == LOGIN_ACTION){
            processLoginResult(completedTask.response);
        } else if(isTaskSuccess(completedTask.result) && completedTaskLoader.getId() == USER_DATA_ACTION){
            processUserDataResult(completedTask.response);
        }
        else {
            Log.e("MainActivity", "Login failed \n" + completedTask.response);
        }
    }

    private boolean isTaskSuccess(CompletedTask.Result result) {
        return result == CompletedTask.Result.SUCCESS;
    }

    private void processLoginResult(String response){
        String processResult = new HtmlParsing().extractUserDetails(response);
        if(processResult.equals("")){
            getSupportLoaderManager().initLoader(USER_DATA_ACTION, bundle, this);
        } else{
            Toast.makeText(getContext(), processResult, Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getContext(), MainActivity.class));
        }
    }

    private void processUserDataResult(String response){
        String processResponse = new HtmlParsing().extractUserData(response);
        if(processResponse.equals("")){
            startActivity(new Intent(getContext(), DashboardActivity.class));
            closeProgressDialog();
            finish();
        } else {

        }
    }

    private Context getContext() {
        return getApplicationContext();
    }

    @Override
    public void onLoaderReset(Loader<CompletedTask> completedTaskLoader) {
    }
}
