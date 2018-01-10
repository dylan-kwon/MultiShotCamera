package com.example.seokchankwon.multishotcamera;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;

/**
 * Created by seokchan.kwon on 2018. 1. 8..
 */

public class GlobalConstant {

    public static final String TAG = "GlobalConstant";

    public static final String APP_DCIM_DIR_PATH = getAppDcimDirPath();


    private static String getAppDcimDirPath() {
        if (TextUtils.isEmpty(APP_DCIM_DIR_PATH)) {

            File dcimDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
            if (dcimDir == null) {
                throw new RuntimeException("Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) return null.");
            }

            File appDcimDir = new File(dcimDir, "MultiShotCamera/");
            if (!appDcimDir.exists()) {
                boolean result = appDcimDir.mkdirs();
                Log.e(TAG, "mkDirs result = " + result);
            }

            return appDcimDir.getAbsolutePath();
        }
        return APP_DCIM_DIR_PATH;
    }

}
