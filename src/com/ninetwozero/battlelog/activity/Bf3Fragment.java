package com.ninetwozero.battlelog.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import com.ninetwozero.battlelog.loader.CompletedTask;

import static com.ninetwozero.battlelog.loader.CompletedTask.*;
import static com.ninetwozero.battlelog.loader.CompletedTask.Result.*;

public class Bf3Fragment extends Fragment implements /*DefaultFragment,*/ LoaderCallbacks<CompletedTask> {



    @Override
    public Loader<CompletedTask> onCreateLoader(int id, Bundle bundle) {
        return createLoader(id, bundle);
    }

    protected Loader<CompletedTask> createLoader(int id, Bundle bundle){
        return null;
    }

    @Override
    public void onLoadFinished(Loader<CompletedTask> loader, CompletedTask data) {
        if(resultIsError(data)){
            showErrorDialog(data.result);
        }
    }

    private boolean resultIsError(CompletedTask data){
        return data != null && data.result != SUCCESS && data.result != FAILURE;
    }

    public void showErrorDialog(Result result){
        //TODO show toast with error code
    }

    @Override
    public void onLoaderReset(Loader<CompletedTask> completedTaskLoader) {
    }
}