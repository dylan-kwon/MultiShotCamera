package com.example.seokchankwon.multishotcamera.adapter.recyclerview;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
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

public class CapturePreviewRecyclerViewAdapter extends SingleChoiceAdapter<String> {

    private RequestManager mRequestManager;

    public CapturePreviewRecyclerViewAdapter(Context context, @NonNull RequestManager requestManager) {
        super(context);
        mRequestManager = requestManager;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = getInflater().inflate(R.layout.listview_capture_preview, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int adapterPosition = holder.getAdapterPosition();
        if (adapterPosition == RecyclerView.NO_POSITION) {
            return;
        }

        String capturePath = getItem(adapterPosition);

        if (holder instanceof Holder) {
            Holder itemHolder = (Holder) holder;
            itemHolder.bind(capturePath, isCheckedPosition(adapterPosition));
        }
    }

    public class Holder extends BaseRecyclerViewHolder {

        private ImageView ivCapture;

        public Holder(View itemView) {
            super(itemView);
            initView(itemView);
        }

        private void initView(View itemView) {
            ivCapture = itemView.findViewById(R.id.iv_listview_capture_preview);
        }

        public void bind(@Nullable String capturePath, boolean isChecked) {
            if (TextUtils.isEmpty(capturePath)) {
                return;
            }

            mRequestManager
                    .load(capturePath)
                    .apply(new RequestOptions()
                            .centerCrop())
                    .into(ivCapture);

            if (isChecked) {
                ivCapture.setColorFilter(
                        ContextCompat.getColor(getContext(), R.color.color_alpha_primary),
                        PorterDuff.Mode.SRC_OVER);
            } else {
                ivCapture.clearColorFilter();
            }

        }

    }
}
