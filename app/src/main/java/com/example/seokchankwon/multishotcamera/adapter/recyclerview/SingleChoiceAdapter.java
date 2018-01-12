package com.example.seokchankwon.multishotcamera.adapter.recyclerview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;

/**
 * Created by chan on 2017. 4. 2..
 */

public abstract class SingleChoiceAdapter<T> extends BaseRecyclerViewAdapter<T> {

    public static final int NO_CHECKED_POSITION = -1;

    private int mDefaultCheckedPosition;
    private int mCheckedPosition = mDefaultCheckedPosition;

    public SingleChoiceAdapter(Context context) {
        this(context, 0);
    }

    public SingleChoiceAdapter(Context context, int defaultCheckedPosition) {
        this(context, null, defaultCheckedPosition);

    }

    public SingleChoiceAdapter(Context context, @Nullable ArrayList<T> list) {
        this(context, list, 0);
    }

    public SingleChoiceAdapter(Context context, @Nullable ArrayList<T> list, int defaultCheckedPosition) {
        super(context, list);
        mCheckedPosition = defaultCheckedPosition;
        mDefaultCheckedPosition = defaultCheckedPosition;
    }

    @Override
    public void setItem(@Nullable T item) {
        mCheckedPosition = mDefaultCheckedPosition;
        super.setItem(item);
    }

    @Override
    public void setItems(@Nullable ArrayList<T> items) {
        if (items != null) {
            int newItemCount = items.size();
            int oldItemCount = getItemCount();

            if (oldItemCount > 0) {

                if (mCheckedPosition > newItemCount - 1) {
                    mCheckedPosition = newItemCount - 1;
                }

            } else {
                mCheckedPosition = mDefaultCheckedPosition;
            }

        } else {
            mCheckedPosition = mDefaultCheckedPosition;
        }
        super.setItems(items);
    }

    public void setItems(@Nullable ArrayList<T> items, int checkedPosition) {
        mCheckedPosition = checkedPosition;
        super.setItems(items);
    }

    public void setChecked(int position) {
        if ((position != NO_CHECKED_POSITION) && (mCheckedPosition != position)) {

            int tempPosition = mCheckedPosition;
            mCheckedPosition = position;

            notifyItemChanged(position);
            notifyItemChanged(tempPosition);
        }
    }

    public void setChecked(@NonNull T item) {
        int position = getItemIndex(item);

        if ((position != NO_CHECKED_POSITION) && (mCheckedPosition != position)) {

            int tempPosition = mCheckedPosition;
            mCheckedPosition = position;

            notifyItemChanged(position);
            notifyItemChanged(tempPosition);
        }
    }

    @Override
    public void removeItem(@NonNull T item) {
        int position = getItems().indexOf(item);
        removeItem(position);
    }

    @Override
    public void removeItem(int position) {
        super.removeItem(position);
        if (getItems().size() > position) {
            notifyItemChanged(position);
            mCheckedPosition = position;
        } else {
            notifyItemChanged(position - 1);
            mCheckedPosition = position - 1;
        }
    }

    public int getCheckedPosition() {
        return mCheckedPosition;
    }

    @Nullable
    public T getCheckedItem() {
        if (mCheckedPosition == NO_CHECKED_POSITION || mCheckedPosition == mDefaultCheckedPosition) {
            return null;
        }
        return getItem(mCheckedPosition);
    }

    public boolean isCheckedPosition(int position) {
        return mCheckedPosition == position;
    }

    public boolean isCheckedItem(T item) {
        return mCheckedPosition == getItemIndex(item);
    }

    public int getDefaultCheckedPosition() {
        return mDefaultCheckedPosition;
    }

    public void setDefaultCheckedPosition(int defaultCheckedPosition) {
        mDefaultCheckedPosition = defaultCheckedPosition;
    }

    public void setCheckedClear() {
        int tempCheckedPosition = mCheckedPosition;
        mCheckedPosition = mDefaultCheckedPosition;
        notifyItemChanged(tempCheckedPosition);
    }

}
