package com.example.seokchankwon.multishotcamera;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.example.seokchankwon.multishotcamera.util.GlobalApplication;

import java.io.File;

/**
 * Created by seokchan.kwon on 2018. 1. 8..
 */

public class GlobalConstant {

    public static final String TAG = "GlobalConstant";

    public static final String APP_IMAGE_DIR_PATH = getAppImageDirPath();
    public static final String APP_CACHE_DIR_PATH = getAppCacheDirPath();
    public static final String APP_CACHE_FILE_DIR_PATH = getAppCacheFileDirPath();


    private static String getAppImageDirPath() {
        if (TextUtils.isEmpty(APP_IMAGE_DIR_PATH)) {

            File dcimDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);

            if (dcimDir == null) {
                throw new RuntimeException("Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) return null.");
            }

            File appDcimDir = new File(dcimDir, "MultiShotCamera");

            if (!appDcimDir.exists()) {
                Log.e(TAG, "make ImageDir = " + appDcimDir.mkdirs());
            }

            return appDcimDir.getAbsolutePath();

        }
        return APP_IMAGE_DIR_PATH;
    }

    private static String getAppCacheDirPath() {
        if (TextUtils.isEmpty(APP_CACHE_DIR_PATH)) {

            File externalCacheDir = GlobalApplication.getContext().getExternalCacheDir();

            if (externalCacheDir != null) {
                return externalCacheDir.getAbsolutePath();
            }

            File internalCacheDir = GlobalApplication.getContext().getCacheDir();
            return internalCacheDir.getAbsolutePath();

        }
        return APP_CACHE_DIR_PATH;
    }

    private static String getAppCacheFileDirPath() {
        if (TextUtils.isEmpty(APP_CACHE_FILE_DIR_PATH)) {

            File cacheFileDir = new File(getAppCacheDirPath(), "files");

            if (!cacheFileDir.exists()) {
                Log.e(TAG, "make CacheFileDir = " + cacheFileDir.mkdirs());
            }

            return cacheFileDir.getAbsolutePath();

        }
        return APP_CACHE_FILE_DIR_PATH;
    }

}
