package com.ninetwozero.bf3droid.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;

import com.ninetwozero.bf3droid.dialog.ProgressDialogFragment;
import com.ninetwozero.bf3droid.loader.CompletedTask;

public class Bf3FragmentActivity extends FragmentActivity implements LoaderManager.LoaderCallbacks<CompletedTask> {

    private Handler handler = new Handler();

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

    public void startLoadingDialog(String tag) {
        DialogFragment dialog = new ProgressDialogFragment();
        dialog.show(getSupportFragmentManager(), tag);
    }

    public void closeLoadingDialog(final String tag) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                FragmentManager manager = getSupportFragmentManager();
                DialogFragment fragment = (DialogFragment) manager.findFragmentByTag(tag);
                if (fragment != null) {
                    Log.i("Bf3FragmentActivity", "Closing dialog " + tag);
                    fragment.dismiss();
                } else{
                    Log.i("Bf3FragmentActivity", "Couldn't close dialog, didn't found " + tag);
                }
            }
        });
    }
}
