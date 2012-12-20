package com.ninetwozero.bf3droid.activity;

import static com.ninetwozero.bf3droid.loader.CompletedTask.Result.FAILURE;
import static com.ninetwozero.bf3droid.loader.CompletedTask.Result.SUCCESS;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;

import com.ninetwozero.bf3droid.BF3Reload;
import com.ninetwozero.bf3droid.loader.CompletedTask;
import com.ninetwozero.bf3droid.loader.CompletedTask.Result;

public class Bf3Fragment extends Fragment implements LoaderCallbacks<CompletedTask> , BF3Reload{

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

    public void showErrorDialog(Result result) {
        // TODO show toast with error code
    }

    @Override
    public void onLoaderReset(Loader<CompletedTask> completedTaskLoader) {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void reload() { }
}
