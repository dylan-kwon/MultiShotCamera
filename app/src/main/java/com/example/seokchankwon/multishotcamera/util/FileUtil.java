package com.example.seokchankwon.multishotcamera.util;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.Px;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;

import com.example.seokchankwon.multishotcamera.GlobalConstant;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by chan on 2017. 2. 1..
 */

public class FileUtil {

    public static final String FILE_PROVIDER_AUTHORITY =
            GlobalApplication.getContext().getPackageName() + ".provider";

    public static final int RESIZE_PIXEL_SIZE = 720;

    private FileUtil() {
        // Singleton
    }


    /**
     * uri를 File로 변환
     *
     * @param uri 파일 uri
     * @return 파일
     */
    @NonNull
    public static File uriToFile(Uri uri) {
        return uriToFile(uri, false);
    }


    /**
     * Uri를 File로 변환
     *
     * @param uri      파일 uri
     * @param isResize true: 캐시 파일 폴더에 축소된 이미지 파일을 생성함.
     * @return 파일
     */
    @Nullable
    public static File uriToFile(@NonNull final Uri uri, boolean isResize) {
        if (isResize) {
            Bitmap bitmap = uriToBitmap(uri);

            if (bitmap == null) {
                return null;
            }

            Bitmap resized = resizeBitmap(bitmap);
            String fileName = "resized_" + System.currentTimeMillis() + ".jpg";

            File file = bitmapToFile(GlobalConstant.APP_CACHE_FILE_DIR_PATH, fileName, resized);

            bitmap.recycle();
            resized.recycle();

            return file;

        } else {
            return new File(uri.getPath());
        }
    }


    /**
     * Url을 File로 변환
     *
     * @param url      파일 url
     * @param isResize 축소 여부
     * @return 축소된 비트맵 이미지
     */
    @Nullable
    public static File urlToFile(@NonNull String url, boolean isResize) {
        Bitmap bitmap = urlToBitmap(url);

        if (bitmap == null) {
            return null;
        }

        File file;
        String fileName = +System.currentTimeMillis() + ".jpg";

        if (isResize) {
            fileName = "resized_" + fileName;
            Bitmap resized = resizeBitmap(bitmap);

            file = bitmapToFile(fileName, GlobalConstant.APP_CACHE_FILE_DIR_PATH, resized);
            resized.recycle();

        } else {
            file = bitmapToFile(fileName, GlobalConstant.APP_CACHE_FILE_DIR_PATH, bitmap);
        }

        bitmap.recycle();

        return file;
    }


    /**
     * uri를 bitmap으로 변환
     *
     * @param uri 변환할 uri
     * @return 변환된 bitmap
     */
    @Nullable
    public static Bitmap uriToBitmap(@NonNull Uri uri) {
        try {
            ContentResolver contentResolver = GlobalApplication.getContext().getContentResolver();
            return MediaStore.Images.Media.getBitmap(contentResolver, uri);

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * url을 bitmap으로 변환
     */
    @Nullable
    public static Bitmap urlToBitmap(@NonNull String url) {
        Bitmap bitmap;
        try {
            URL fileUrl = new URL(url);
            URLConnection conn = fileUrl.openConnection();
            conn.connect();
            BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
            bitmap = BitmapFactory.decodeStream(bis);
            bis.close();

            return bitmap;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * Bitmap을 비율(사이즈) 축소
     *
     * @param bitmap 이미지
     * @return 축소된 비트앱 이미지
     */
    @NonNull
    public static Bitmap resizeBitmap(@NonNull Bitmap bitmap) {
        return resizeBitmap(bitmap, RESIZE_PIXEL_SIZE);
    }


    /**
     * Bitmap을 비율(사이즈) 축소
     *
     * @param bitmap  이미지
     * @param scalePx 비트맵의 가로, 세로 중 더 작은곳이 축소될 크기
     * @return 축소된 비트앱 이미지
     */
    @NonNull
    public static Bitmap resizeBitmap(@NonNull Bitmap bitmap, int scalePx) {
        int height = bitmap.getHeight();
        int width = bitmap.getWidth();

        if (width > height) {
            return resizeBitmap(bitmap, (width * scalePx) / height, scalePx);

        } else {
            return resizeBitmap(bitmap, scalePx, (height * scalePx) / width);
        }
    }


    /**
     * Bitmap 리사이즈
     *
     * @param bitmap 이미지
     * @param width  가로
     * @param height 세로
     * @return 축소된 비트앱 이미지
     */
    @NonNull
    public static Bitmap resizeBitmap(@NonNull Bitmap bitmap, @Px int width, @Px int height) {
        return Bitmap.createScaledBitmap(
                bitmap, width, height, true);
    }


    /**
     * 파일을 리사이즈
     *
     * @param file 리사이즈 할 파일
     * @return 리사이즈된 파일
     */
    @Nullable
    public static File resizeFile(@NonNull File file) {
        return resizeFile(GlobalConstant.APP_CACHE_FILE_DIR_PATH, file);
    }


    /**
     * 파일을 리사이즈
     *
     * @param dirPath 리사이즈된 파일을 생성할 경로
     * @param file    리사이즈 할 파일
     * @return 리사이즈된 파일
     */
    @Nullable
    public static File resizeFile(String dirPath, @NonNull File file) {
        Uri uri = fileToUri(file);

        Bitmap bitmap = uriToBitmap(uri);

        if (bitmap == null) {
            return null;
        }

        String fileName = "resized_" + System.currentTimeMillis() + ".jpg";
        Bitmap resizedBitmap = resizeBitmap(bitmap);

        File resizedFile = bitmapToFile(dirPath, fileName, resizedBitmap);

        bitmap.recycle();
        resizedBitmap.recycle();

        return resizedFile;
    }


    /**
     * Bitmap을 File로 변환함.
     * 파일의 생성 경로는 /cache/files/
     *
     * @param fileName 생성될 파일의 이름
     * @param bitmap   이미지
     * @return 이미지 파일
     */
    @Nullable
    public static File bitmapToFile(@NonNull String fileName, @NonNull Bitmap bitmap) {
        return bitmapToFile(GlobalConstant.APP_CACHE_FILE_DIR_PATH, fileName, bitmap);
    }


    /**
     * Bitmap을 File로 변환함
     *
     * @param dirPath  생성될 파일의 경로
     * @param fileName 생성될 파일의 이름
     * @param bitmap   이미지
     * @return 이미지 파일
     */
    @Nullable
    public static File bitmapToFile(String dirPath, @NonNull String fileName, @NonNull Bitmap bitmap) {
        File file = new File(getDir(dirPath), fileName);

        try {
            boolean isCreateNewFile = file.createNewFile();
            Log.e(GlobalConstant.TAG, fileName + ": isCreate = " + isCreateNewFile);

            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return file;
    }


    /**
     * 해당 경로의 폴더를 리턴함
     * 폴더가 존재하지 않으면 만듬
     *
     * @param dirPath 폴더 경로
     * @return 폴더 리턴
     */
    @NonNull
    public static File getDir(@NonNull String dirPath) {
        File dir = new File(dirPath);
        if (!dir.exists()) {
            boolean result = dir.mkdirs();
            Log.e(GlobalConstant.TAG, "mkDirs result = " + result);
        }
        return dir;
    }


    /**
     * file을 외부 앱에 전달할 경우 사용할 uri를 제공함.
     * Android N(24)부터 외부에 file://uri를 직접적으로 노출하면 exception이 발생하기 때문에 사용.
     *
     * @param file 외부 앱에 전달할 파일.
     */
    @Nullable
    public static Uri provideUriFromFile(@Nullable File file) {
        if (file == null) {
            return null;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return FileProvider.getUriForFile(
                    GlobalApplication.getContext(), FILE_PROVIDER_AUTHORITY, file);
        } else {
            return fileToUri(file);
        }
    }


    /**
     * file의 uri를 리턴함
     *
     * @param file file
     * @return file uri
     */
    @NonNull
    public static Uri fileToUri(@Nullable File file) {
        return Uri.fromFile(file);
    }


    /**
     * 파일 삭제
     *
     * @param uri 삭제할 파일의 Uri
     */

    public static void deleteFile(@Nullable Uri uri) {
        if (uri != null) {
            File file = uriToFile(uri);
            deleteFile(file);
        }
    }


    /**
     * 파일 삭제
     *
     * @param path 삭제할 파일 경로
     */
    public static void deleteFile(@Nullable String path) {
        if (!TextUtils.isEmpty(path)) {
            deleteFile(new File(path));
        }
    }


    /**
     * 파일 삭제
     *
     * @param file 삭제할 파일
     */
    public static void deleteFile(@Nullable File file) {
        if (file != null && file.exists()) {
            String fileName = file.getName();
            boolean isFileDelete = file.delete();
            Log.e(GlobalConstant.TAG, fileName + ": isDelete = " + isFileDelete);
        }
    }
}