package com.ninetwozero.battlelog.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.ninetwozero.battlelog.BF3Reload;
import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.loader.CompletedTask;
import com.ninetwozero.battlelog.loader.CompletedTask.Result;

import static com.ninetwozero.battlelog.loader.CompletedTask.Result.FAILURE;
import static com.ninetwozero.battlelog.loader.CompletedTask.Result.SUCCESS;

public class Bf3Fragment extends Fragment implements LoaderCallbacks<CompletedTask> {

    private BF3Reload bf3Reload;

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
        bf3Reload = (BF3Reload) this;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.reload, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.option_refresh) {
            bf3Reload.reload();
        }
        return true;
    }
}
