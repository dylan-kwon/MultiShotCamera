package com.example.seokchankwon.multishotcamera.activity;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.seokchankwon.multishotcamera.R;
import com.example.seokchankwon.multishotcamera.adapter.viewpager.CaptureCompleteAdapter;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_CAMERA = 100;

    private Toolbar mToolbar;

    private ViewPager mViewPager;
    private CaptureCompleteAdapter mAdapter;

    private FloatingActionButton fabCamera;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initViewPager();
        setSupportActionBar(mToolbar);

        fabCamera.setOnClickListener(v -> showCamera());
    }

    private void initView() {
        mToolbar = findViewById(R.id.tb_activity_main);
        mViewPager = findViewById(R.id.vp_activity_main);
        fabCamera = findViewById(R.id.fab_activity_main_show_camera);
    }

    private void initViewPager() {
        mAdapter = new CaptureCompleteAdapter(this, Glide.with(this));
        mViewPager.setPageMargin(getResources().getDimensionPixelSize(R.dimen.dim_10dp));
        mViewPager.setAdapter(mAdapter);
    }

    private void showCamera() {
        checkPermission(new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                // 퍼미션 요청 허가 -- > 카메라 시작
                movingCameraActivity();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                // 퍼미션 요청 거절
                Toast.makeText(MainActivity.this,
                        getString(R.string.permission_reject_toast),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void movingCameraActivity() {
        Intent intent = new Intent(this, CameraActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(CameraActivity.EXTRA_LIMIT_CAPTURE_COUNT, 10);
        startActivityForResult(intent, REQUEST_CODE_CAMERA);
    }

    private void checkPermission(PermissionListener listener) {
        new TedPermission(this)

                // 퍼미션 요청을 거절한 경우 보이는 메세지
                .setDeniedMessage(getString(R.string.permission_denied_message))
                .setDeniedCloseButtonText(R.string.common_close)

                // 퍼미션을 거절한 경우 설정 화면으로 바로 이동 버튼 띄우기
                .setGotoSettingButton(true)
                .setGotoSettingButtonText(R.string.common_setting)

                // 체크할 퍼미션들
                .setPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)

                // 퍼미션 요청 콜백
                .setPermissionListener(listener)

                // 퍼미션 요청 시작
                .check();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) {
            return;
        }

        ArrayList<Uri> captureUris = data.getParcelableArrayListExtra(CameraActivity.REQUEST_EXTRA_CAPTURE_URIS);
        if (captureUris == null) {
            return;
        }

        for (Uri uri : captureUris) {
            Log.e("TAG", "uri: " + uri);
        }

        Toast.makeText(this, "captureCount = " + captureUris.size(), Toast.LENGTH_SHORT).show();

        mAdapter.setItems(captureUris);
    }

}
