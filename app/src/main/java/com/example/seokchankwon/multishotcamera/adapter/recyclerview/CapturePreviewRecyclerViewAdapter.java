package com.example.seokchankwon.multishotcamera.adapter.recyclerview;

import android.content.Context;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.example.seokchankwon.multishotcamera.R;

/**
 * Created by seokchan.kwon on 2018. 1. 11..
 */

public class CapturePreviewRecyclerViewAdapter extends SingleChoiceAdapter<Uri> {

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

        Uri captureUri = getItem(adapterPosition);

        if (holder instanceof Holder) {
            Holder itemHolder = (Holder) holder;
            itemHolder.bind(captureUri, isCheckedPosition(adapterPosition));
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

        public void bind(@Nullable Uri captureUri, boolean isChecked) {
            if (captureUri == null) {
                return;
            }

            mRequestManager
                    .load(captureUri)
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
