package com.example.seokchankwon.multishotcamera.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.Px;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.seokchankwon.multishotcamera.GlobalConstant;
import com.example.seokchankwon.multishotcamera.R;
import com.github.florent37.camerafragment.CameraFragment;
import com.github.florent37.camerafragment.configuration.Configuration;
import com.github.florent37.camerafragment.listeners.CameraFragmentResultListener;
import com.github.florent37.camerafragment.listeners.CameraFragmentStateListener;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by seokchan.kwon on 2018. 1. 8..
 */

public class CameraActivity extends AppCompatActivity implements CameraFragmentResultListener, CameraFragmentStateListener {

    public static final String SAVED_PICTURE_URIS = "saved.picture_uris";
    public static final String EXTRA_LIMIT_PICTURE_COUNT = "extra.limit_picture_count";

    public static final String REQUEST_EXTRA_PICTURE_URIS = "request_extra.picture_uris";

    public static final String FRAGMENT_TAG_CAMERA = "fragment_tag.camera";

    private int mDisplayDegrees;
    private int mLimitPictureCount;

    private FrameLayout flPictureContainer;

    private ImageView ivPicture;

    private ImageButton ibtPictureSend;
    private ImageButton ibtCameraFinish;

    private Button btCameraRecord;

    private ArrayList<String> mPictureUris;

    public Thread mRecordButtonEnableThread;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        initView();
        moveViewTranslateY();

        setupInstanceState(savedInstanceState);

        replaceCameraFragment();
        updateLatestPictureImageView();

        // 찍은 사진의 개수만큼 표시
        btCameraRecord.setText(String.valueOf(mPictureUris.size()));

        // 촬영버튼 클릭
        btCameraRecord.setOnClickListener(v -> takePicture());

        // 찍은 사진 보기
        flPictureContainer.setOnClickListener(v -> movingCameraImageActivity());

        // 찍은 사진 보내기
        ibtPictureSend.setOnClickListener(v -> sendPicture());

        // 카메라 종료
        ibtCameraFinish.setOnClickListener(v -> finish());

    }

    private void setupInstanceState(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mPictureUris = savedInstanceState.getStringArrayList(SAVED_PICTURE_URIS);
            mLimitPictureCount = savedInstanceState.getInt(EXTRA_LIMIT_PICTURE_COUNT, 10);

        } else {
            mPictureUris = new ArrayList<>();
            mLimitPictureCount = getIntent().getIntExtra(EXTRA_LIMIT_PICTURE_COUNT, 10);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mRecordButtonEnableThread != null && !mRecordButtonEnableThread.isInterrupted()) {
            mRecordButtonEnableThread.interrupt();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(EXTRA_LIMIT_PICTURE_COUNT, mLimitPictureCount);
        outState.putStringArrayList(SAVED_PICTURE_URIS, mPictureUris);
    }

    private void initView() {
        btCameraRecord = findViewById(R.id.bt_activity_camera_record);
        ivPicture = findViewById(R.id.iv_activity_camera_picture);
        ibtPictureSend = findViewById(R.id.ibt_activity_camera_picture_send);
        ibtCameraFinish = findViewById(R.id.ibt_activity_camera_close);
        flPictureContainer = findViewById(R.id.fl_activity_camera_picture_container);
    }

    private void moveViewTranslateY() {
        // CameraFragment 버그(?) 스테이터스바 영역까지 침범하기 때문에, 그만큼 아래로 내림.

        ibtPictureSend.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                ibtPictureSend.getViewTreeObserver().removeOnPreDrawListener(this);
                ibtPictureSend.setTranslationY(getStatusBarSize());
                return false;
            }
        });

        ibtCameraFinish.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                ibtCameraFinish.getViewTreeObserver().removeOnPreDrawListener(this);
                ibtCameraFinish.setTranslationY(getStatusBarSize());
                return false;
            }
        });

    }

    private void replaceCameraFragment() {
        // 카메라 프래그먼트 replace.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, getString(R.string.permission_denied_message), Toast.LENGTH_SHORT).show();
            return;
        }

//        CameraFragment fragment = getCameraFragment();
//        if (fragment != null && fragment.isAdded()) {
//            detachCameraFragment();
//        }

        CameraFragment cameraFragment = CameraFragment.newInstance(
                new Configuration.Builder()
                        .setCamera(Configuration.CAMERA_FACE_REAR)
                        .setFlashMode(Configuration.FLASH_MODE_OFF)
                        .setMediaAction(Configuration.MEDIA_ACTION_PHOTO)
                        .setMediaQuality(Configuration.MEDIA_QUALITY_HIGH)
                        .build());

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fl_activity_camera_container, cameraFragment, FRAGMENT_TAG_CAMERA)
                .commit();

        cameraFragment.setStateListener(this);
    }

    private void detachCameraFragment() {
        // 카메라 프래그먼트 remove.
        CameraFragment cameraFragment = getCameraFragment();
        if (cameraFragment == null || !cameraFragment.isAdded()) {
            return;
        }

        getSupportFragmentManager().beginTransaction()
                .remove(cameraFragment)
                .commit();
    }

    private void takePicture() {
        if (mLimitPictureCount <= mPictureUris.size()) {
            String toastMsg = getString(R.string.take_picture_max)
                    + String.valueOf(mLimitPictureCount)
                    + getString(R.string.take_picture_max_select);
            Toast.makeText(this, toastMsg, Toast.LENGTH_SHORT).show();
            return;
        }

        // 사진 촬영
        CameraFragment cameraFragment = getCameraFragment();
        if (cameraFragment == null) {
            return;
        }

        cameraFragment.takePhotoOrCaptureVideo(
                this, GlobalConstant.APP_DCIM_DIR_PATH, "" + System.currentTimeMillis());

        // cameraFragment 버그(?) 가끔 사진 찍기에 실패하는 경우가 있음
        // 콜백에는 실패에 대한 메소드가 없기 때문에 위에서 Disable 처리한 버튼을 Enable 상태로 변경해 줘야함.
        startRecordButtonEnableThread();
    }

    private void startRecordButtonEnableThread() {
        mRecordButtonEnableThread = new Thread(() -> {

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (!isFinishing()) {
                if (!btCameraRecord.isEnabled()) {
                    runOnUiThread(() -> btCameraRecord.setEnabled(true));
                }
            }
        });

        mRecordButtonEnableThread.start();
    }

    private void sendPicture() {
        Intent intent = new Intent();
        intent.putStringArrayListExtra(REQUEST_EXTRA_PICTURE_URIS, mPictureUris);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void movingCameraImageActivity() {

    }

    @Override
    public void onVideoRecorded(String filePath) {
        // 동영상 촬영 완료 Callback
        // 동영상 구현x
    }

    @Override
    public void onPhotoTaken(byte[] bytes, String filePath) {
        // 카메라 촬영 완료 Callback
        mPictureUris.add(filePath);

        btCameraRecord.setEnabled(true);
        btCameraRecord.setText(String.valueOf(mPictureUris.size()));

        replaceCameraFragment();
        updateLatestPictureImageView();
    }

    @Override
    public void onCurrentCameraBack() {
        // 카메라가 전면 모드일 때
    }

    @Override
    public void onCurrentCameraFront() {
        // 카메라가 후면 모드일 때
    }

    @Override
    public void onFlashAuto() {
        // 플래시가 자동일 때
    }

    @Override
    public void onFlashOn() {
        // 플래시가 켜짐일 때
    }

    @Override
    public void onFlashOff() {
        // 플래시가 꺼짐일 때
    }

    @Override
    public void onCameraSetupForPhoto() {
        // 카메라가 사진 모드일 때
    }

    @Override
    public void onCameraSetupForVideo() {
        // 카메라가 동영상 모드일 때
    }

    @Override
    public void onRecordStateVideoReadyForRecord() {
        // 카메라 레코드 버튼을 눌렀을 때 (동영상 모드)
    }

    @Override
    public void onRecordStateVideoInProgress() {
        // 카메라 상태가 동영상 촬영중일 때
    }

    @Override
    public void onRecordStatePhoto() {
        // 카메라 레코드 버튼을 눌렀을 때 (사진 모드)
        btCameraRecord.setEnabled(false);
    }

    @Override
    public void shouldRotateControls(int degrees) {
        // 카메라가 회전되었을 때 (0~270을 90단위로 노티)
        if (mDisplayDegrees == degrees) {
            return;
        }
        mDisplayDegrees = degrees;
        startRotateAnimation(degrees);
    }

    @Override
    public void onStartVideoRecord(File outputFile) {
        // 동영상 촬영 시작
    }

    @Override
    public void onStopVideoRecord() {
        // 동영상 쵤영 정지
    }

    private void updateLatestPictureImageView() {
        // 마지막으로 찍은 사지으로 이미지뷰 업데이트
        int pictureSize = mPictureUris.size();

        if (pictureSize <= 0) {
            return;
        }

        String picturePath = mPictureUris.get(pictureSize - 1);

        Glide.with(this)
                .load(picturePath)
                .apply(new RequestOptions()
                        .circleCrop())
                .into(ivPicture);
    }

    private void startRotateAnimation(int degrees) {

        if (degrees == 270) {
            degrees = -90;
        }

        btCameraRecord.animate()
                .rotation(degrees)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setDuration(500)
                .start();

        flPictureContainer.animate()
                .rotation(degrees)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setDuration(500)
                .start();

        ibtPictureSend.animate()
                .rotation(degrees)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setDuration(500)
                .start();

        ibtCameraFinish.animate()
                .rotation(degrees)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setDuration(500)
                .start();

    }

    private CameraFragment getCameraFragment() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG_CAMERA);
        return (fragment instanceof CameraFragment) ? ((CameraFragment) fragment) : null;
    }

    @Px
    public int getStatusBarSize() {
        Resources resources = getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        return resources.getDimensionPixelSize(resourceId);
    }

}
