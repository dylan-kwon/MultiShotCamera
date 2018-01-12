package com.example.seokchankwon.multishotcamera.adapter.viewpager;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.example.seokchankwon.multishotcamera.R;

/**
 * Created by seokchan.kwon on 2018. 1. 11..
 */

public class CapturePreviewViewPagerAdapter extends BasePagerAdapter<String> {

    private RequestManager mRequestManager;

    public CapturePreviewViewPagerAdapter(Context context, @NonNull RequestManager requestManager) {
        super(context);
        mRequestManager = requestManager;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = getInflater().inflate(R.layout.viewpager_capture_preview, container, false);

        CapturePreviewViewPagerHolder holder = new CapturePreviewViewPagerHolder(view);
        holder.bind(position, getItem(position));

        view.setTag(holder);

        container.addView(view);

        return view;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        if (!(object instanceof View)) {
            return POSITION_UNCHANGED;
        }

        View view = (View) object;
        if (!(view.getTag() instanceof CapturePreviewViewPagerHolder)) {
            return POSITION_UNCHANGED;
        }

        CapturePreviewViewPagerHolder holder = (CapturePreviewViewPagerHolder) view.getTag();

        try {
            int adapterPosition = holder.getAdapterPosition();

            String oldItem = holder.getCapturePath();
            String newItem = getItem(adapterPosition);

            if (!newItem.equals(oldItem)) {
                return POSITION_NONE;
            }

        } catch (NullPointerException | IndexOutOfBoundsException e) {
            return POSITION_NONE;
        }
        return POSITION_UNCHANGED;
    }

    public class CapturePreviewViewPagerHolder {

        private String mCapturePath;

        private int mAdapterPosition;

        private ImageView ivCapture;

        public CapturePreviewViewPagerHolder(View itemView) {
            initView(itemView);
        }

        private void initView(View itemView) {
            ivCapture = itemView.findViewById(R.id.iv_viewpager_capture_preview);
        }

        public void bind(int position, String capturePath) {
            if (TextUtils.isEmpty(capturePath)) {
                return;
            }
            mRequestManager
                    .load(capturePath)
                    .apply(new RequestOptions()
                            .centerInside())
                    .into(ivCapture);

            mCapturePath = capturePath;
            mAdapterPosition = position;
        }

        public int getAdapterPosition() {
            return mAdapterPosition;
        }

        @Nullable
        public String getCapturePath() {
            return mCapturePath;
        }

    }

}
