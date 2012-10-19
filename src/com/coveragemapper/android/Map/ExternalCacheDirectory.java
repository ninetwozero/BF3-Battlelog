package com.coveragemapper.android.Map;

/*
 * 	Author: Scott Kennedy
 * 	License: He doesn't really care. ;-)
 *  Additional information: He's a canadian cow on IRC.
 */

import android.content.Context;
import android.os.Build;
import android.os.Environment;

import java.io.File;

public abstract class ExternalCacheDirectory {
    protected final Context mContext;

    public static ExternalCacheDirectory getInstance(final Context context) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ECLAIR_MR1) {
            return new ExternalCacheDirectory8(context);
        }

        return new ExternalCacheDirectory1(context);
    }

    protected ExternalCacheDirectory(final Context context) {
        super();
        mContext = context;
    }

    /**
     * Get a File pointing to the external cache directory.
     *
     * @return a File reference to the external cache directory
     */
    public abstract File getExternalCacheDirectory();

    private static class ExternalCacheDirectory1 extends ExternalCacheDirectory {

        public ExternalCacheDirectory1(final Context context) {
            super(context);
        }

        @Override
        public File getExternalCacheDirectory() {
            final String cachePath = Environment.getExternalStorageDirectory()
                    .getAbsolutePath()
                    + "/Android/data/"
                    + mContext.getPackageName() + "/cache/";

            final File file = new File(cachePath);
            file.mkdirs();

            return file;
        }
    }

    private static class ExternalCacheDirectory8 extends ExternalCacheDirectory {
        public ExternalCacheDirectory8(final Context context) {
            super(context);
        }

        @Override
        public File getExternalCacheDirectory() {
            return mContext.getExternalCacheDir();
        }
    }
}
