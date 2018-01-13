package com.example.seokchankwon.multishotcamera.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.seokchankwon.multishotcamera.R;
import com.example.seokchankwon.multishotcamera.adapter.recyclerview.CapturePreviewRecyclerViewAdapter;
import com.example.seokchankwon.multishotcamera.adapter.viewpager.CapturePreviewViewPagerAdapter;
import com.example.seokchankwon.multishotcamera.util.FileUtil;

import java.util.ArrayList;

/**
 * Created by seokchan.kwon on 2018. 1. 11..
 */

public class CameraPreviewActivity extends AppCompatActivity {

    public static final String EXTRA_CAPTURE_PATHS = "extra.capture_paths";
    public static final String REQUEST_EXTRA_CAPTURE_PATHS = "request_extra.capture_paths";

    private Toolbar mToolbar;

    private ViewPager vpPreview;
    private CapturePreviewViewPagerAdapter mViewPagerAdapter;

    private RecyclerView rvPreview;
    private CapturePreviewRecyclerViewAdapter mRecyclerViewAdapter;

    private Button btCaptureDelete;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_preview);

        initView();
        initToolbar();
        initViewPager();
        initRecyclerView();
        setupInstanceState(savedInstanceState);

        btCaptureDelete.setOnClickListener(v -> deleteCapture());

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(EXTRA_CAPTURE_PATHS, mRecyclerViewAdapter.getItems());
    }

    private void setupInstanceState(@Nullable Bundle savedInstanceState) {
        ArrayList<Uri> capturePaths;

        if (savedInstanceState != null) {
            capturePaths = savedInstanceState.getParcelableArrayList(EXTRA_CAPTURE_PATHS);
        } else {
            capturePaths = getIntent().getParcelableArrayListExtra(EXTRA_CAPTURE_PATHS);
        }

        mViewPagerAdapter.setItems(capturePaths);
        mRecyclerViewAdapter.setItems(capturePaths);
    }

    private void initView() {
        mToolbar = findViewById(R.id.tb_activity_capture_preview);
        vpPreview = findViewById(R.id.vp_activity_capture_preview);
        rvPreview = findViewById(R.id.rv_activity_capture_preview);
        btCaptureDelete = findViewById(R.id.bt_activity_capture_preview_delete);
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initViewPager() {
        mViewPagerAdapter = new CapturePreviewViewPagerAdapter(this, Glide.with(this));
        vpPreview.setOffscreenPageLimit(1);
        vpPreview.setAdapter(mViewPagerAdapter);
        vpPreview.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                mRecyclerViewAdapter.setChecked(position);
                rvPreview.smoothScrollToPosition(position);
            }
        });
    }

    private void initRecyclerView() {
        LinearLayoutManager linearLayoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        mRecyclerViewAdapter =
                new CapturePreviewRecyclerViewAdapter(this, Glide.with(this));

        mRecyclerViewAdapter.setOnItemClickListener((adapterPosition, item) ->
                vpPreview.setCurrentItem(adapterPosition, true));

        rvPreview.setHasFixedSize(true);
        rvPreview.setAdapter(mRecyclerViewAdapter);
        rvPreview.setLayoutManager(linearLayoutManager);
    }

    private void deleteCapture() {
        deleteCapture(mRecyclerViewAdapter.getCheckedPosition());
    }

    private void deleteCapture(int position) {
        Uri deleteUri = mRecyclerViewAdapter.getItem(position);

        new Thread(() -> FileUtil.deleteFile(deleteUri)).start();

        mViewPagerAdapter.removeItem(position);
        mRecyclerViewAdapter.removeItem(position);

        if (mRecyclerViewAdapter.getItemCount() <= 0) {
            Toast.makeText(this, getString(R.string.camera_preview_none_delete_image), Toast.LENGTH_SHORT).show();
            finish();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void finish() {
        Intent intent = new Intent();
        intent.putParcelableArrayListExtra(REQUEST_EXTRA_CAPTURE_PATHS, mRecyclerViewAdapter.getItems());
        setResult(RESULT_OK, intent);
        super.finish();
    }
}
