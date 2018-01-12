package com.example.seokchankwon.multishotcamera.adapter.viewpager;

import android.content.Context;
import android.support.annotation.NonNull;

import com.bumptech.glide.RequestManager;

/**
 * Created by seokchan.kwon on 2018. 1. 11..
 */

public class CaptureCompleteAdapter extends CapturePreviewViewPagerAdapter {

    public CaptureCompleteAdapter(Context context, @NonNull RequestManager requestManager) {
        super(context, requestManager);
    }

    @Override
    public float getPageWidth(int position) {
        return 0.4f;
    }

}
