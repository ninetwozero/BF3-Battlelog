package com.ninetwozero.bf3droid.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;

import com.ninetwozero.bf3droid.dialog.ProgressDialogFragment;
import com.ninetwozero.bf3droid.loader.CompletedTask;

import static com.ninetwozero.bf3droid.loader.CompletedTask.Result.FAILURE;
import static com.ninetwozero.bf3droid.loader.CompletedTask.Result.SUCCESS;

public class Bf3ListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<CompletedTask> {
    private Handler handler = new Handler();

    @Override
    public Loader<CompletedTask> onCreateLoader(int id, Bundle bundle) {
        return createLoader(id, bundle);
    }

    protected Loader<CompletedTask> createLoader(int id, Bundle bundle) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<CompletedTask> loader, CompletedTask data) {
        if (resultIsError(data)) {
            showErrorDialog(data.result);
        }
        loadFinished(loader, data);
    }

    public void loadFinished(Loader<CompletedTask> loader, CompletedTask data) {
    }

    private boolean resultIsError(CompletedTask data) {
        return data != null && data.result != SUCCESS && data.result != FAILURE;
    }

    public void showErrorDialog(CompletedTask.Result result) {
        // TODO show toast with error code
    }

    @Override
    public void onLoaderReset(Loader<CompletedTask> completedTaskLoader) {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
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
                } else {
                    Log.i("Bf3FragmentActivity", "Couldn't close dialog, didn't found " + tag);
                }
            }
        });
    }

    private FragmentManager getSupportFragmentManager() {
        return getActivity().getSupportFragmentManager();
    }
}
