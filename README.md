What is MultiShotCamera?
========================

This example implements a camera that takes several pictures at once and returns an ArrayList(Uri).<br/>It does not provide many custom functions because it was created to protect code reuse and tone and manners within a project.

Preview
-------

<p><img src="http://drive.google.com/uc?export=view&id=1Hoy5cf3v8aqabPueGeClwQHdslxwop0R" width="250" height="435"><p/>

<br/>

How to use
----------

### 1. Check Permission

> ```xml
> <!-- AndroidManifest.xml -->
> <uses-permission android:name="android.permission.CAMERA" />
> <uses-permission android:name="android.permission.RECORD_AUDIO" />
> <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
> <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
> ```

<br/>

### 2. CameraActivity

"CameraActivity" is used to take pictures.

> #### Start CameraActivity
>
> ```java
> private void intentCameraActivity() {
>        Intent intent = new Intent(this, CameraActivity.class);
>        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
>        startActivityForResult(intent, REQUEST_CODE_CAMERA);
>    }
> ```
>
> #### Intent Extra Option
>
> -	`intent.putExtra(CameraActivity.EXTRA_JPEG_QUALITY, int jpegQuality);`
> -	`intent.putExtra(CameraActivity.EXTRA_CAPTURE_MIN_WIDTH, int minWidth);`
> -	`intent.putExtra(CameraActivity.EXTRA_CAPTURE_MAX_WIDTH, int maxWidth);`
> -	`intent.putExtra(CameraActivity.EXTRA_CAPTURE_MIN_HEIGHT, int minHeight);`
> -	`intent.putExtra(CameraActivity.EXTRA_CAPTURE_MAX_HEIGHT, int maxHeight);`
> -	`intent.putExtra(CameraActivity.EXTRA_LIMIT_CAPTURE_COUNT, int maxCount);`
>
> #### Result Capture Uris
>
> ```java
> @Override
>     protected void onActivityResult(int requestCode, int resultCode, Intent data) {
>         super.onActivityResult(requestCode, resultCode, data);
>         switch (requestCode) {
>             case REQUEST_CODE_CAMERA:
>                 if (resultCode != RESULT_OK) {
>                     return;
>                 }
>                 ArrayList<Uri> captureUris = data.getParcelableArrayListExtra(CameraActivity.REQUEST_EXTRA_CAPTURE_PATHS);
>                 if (captureUris == null) {
>                     return;
>                 }
>                 for (Uri uri : captureUris) {
>                     Log.e("TAG", "uri: " + uri);
>                 }
>                 Toast.makeText(this, "captureCount = " + captureUris.size(), Toast.LENGTH_SHORT).show();
>                 break;
>         }
>     }
> ```

<br/>

### 3. CameraPreviewActivity

"CameraPreviewActivity" is used to check or delete pictures taken by CameraActivity.

> #### Start CameraPreviewActivity
>
> ```java
> private void intentCapturePreviewActivity() {
>         if (mCaptureUris.size() > 0) {
>             Intent intent = new Intent(this, CameraPreviewActivity.class);
>             intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
>             intent.putParcelableArrayListExtra(CameraPreviewActivity.EXTRA_CAPTURE_PATHS, mCaptureUris);
>             startActivityForResult(intent, REQUEST_CODE_CAMERA_PREVIEW_ACTIVITY);
>         }
>     }
> ```
>
> #### Result Capture Uris
>
> ```java
> @Override
>    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
>        super.onActivityResult(requestCode, resultCode, data);
>        switch (requestCode) {
>            case REQUEST_CODE_CAMERA_PREVIEW_ACTIVITY:
>                if (resultCode != RESULT_OK) {
>                    return;
>                }
>                
>                ArrayList<Uri> newCapturePaths =
>                        data.getParcelableArrayListExtra(CameraPreviewActivity.REQUEST_EXTRA_CAPTURE_PATHS);
>
>                if (mCaptureUris.size() != newCapturePaths.size()) {
>                    mCaptureUris.clear();
>                    mCaptureUris.addAll(newCapturePaths);
>                }
>                break;
>        }
>    }
> ```
