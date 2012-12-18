package com.ninetwozero.battlelog.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import com.ninetwozero.battlelog.dialog.ProgressDialogFragment;
import com.ninetwozero.battlelog.loader.CompletedTask;

public class Bf3FragmentActivity extends FragmentActivity implements LoaderManager.LoaderCallbacks<CompletedTask>{

    private Handler handler = new Handler();
    private ProgressDialogFragment progressDialog;

    @Override
    public Loader<CompletedTask> onCreateLoader(int i, Bundle bundle) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<CompletedTask> completedTaskLoader, CompletedTask completedTask) {
    }

    @Override
    public void onLoaderReset(Loader<CompletedTask> completedTaskLoader) {
    }


    public void startLoadingDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialogFragment();
        }
        progressDialog.show(getSupportFragmentManager(), "dialog");
    }

    public void closeProgressDialog(){
        handler.post(new Runnable() {
            @Override
            public void run() {
                if(progressDialog != null){
                    progressDialog.dismiss();
                }
            }
        });
    }
}
