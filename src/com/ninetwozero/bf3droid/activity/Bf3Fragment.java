package com.ninetwozero.bf3droid.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.util.Log;

import com.ninetwozero.bf3droid.BF3Droid;
import com.ninetwozero.bf3droid.BF3Reload;
import com.ninetwozero.bf3droid.dialog.ProgressDialogFragment;
import com.ninetwozero.bf3droid.loader.CompletedTask;
import com.ninetwozero.bf3droid.loader.CompletedTask.Result;
import com.ninetwozero.bf3droid.model.User;
import com.ninetwozero.bf3droid.util.ImageLoader;
import com.novoda.imageloader.core.ImageManager;
import com.novoda.imageloader.core.LoaderSettings;
import com.novoda.imageloader.core.cache.LruBitmapCache;

import static com.ninetwozero.bf3droid.loader.CompletedTask.Result.FAILURE;
import static com.ninetwozero.bf3droid.loader.CompletedTask.Result.SUCCESS;

public class Bf3Fragment extends Fragment implements LoaderCallbacks<CompletedTask>, BF3Reload {

    private Handler handler = new Handler();
    private static final String GRAVATAR_URL = "http://www.gravatar.com/avatar/";
    private static final String BATTLELOG_SUFFIX = "?s=100&d=http%3A%2F%2Fbattlelog-cdn.battlefield.com%2Fcdnprefix%2Favatar1%2Fpublic%2Fbase%2Fshared%2Fdefault-avatar-100.png";

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
    public void reload() {
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

    protected User user(String user){
        return BF3Droid.getUserBy(user);
    }

    protected String imagePath(String gravatarId) {
        return new StringBuilder(GRAVATAR_URL).append(gravatarId).append(BATTLELOG_SUFFIX).toString();
    }

    protected ImageLoader provideImageLoader() {
        Context appContext = getGontext();
        LoaderSettings settings = new LoaderSettings.SettingsBuilder()
                .withDisconnectOnEveryCall(true)
                .withCacheManager(new LruBitmapCache(appContext))
                .build(appContext);
        return new ImageLoader(new ImageManager(appContext, settings));
    }

    private Context getGontext() {
        return getActivity().getApplicationContext();
    }
}
