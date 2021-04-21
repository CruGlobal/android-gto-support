package org.ccci.gto.android.common.picasso.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;

import com.squareup.picasso.Transformation;

import java.io.File;
import java.util.List;

public interface PicassoImageView {
    class Helper extends BaseHelper {
        public Helper(@NonNull ImageView view) {
            super(view);
        }

        public Helper(@NonNull ImageView view, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(view, attrs, defStyleAttr, defStyleRes);
        }
    }

    /**
     * @return The ImageView this PicassoImageView represents.
     */
    @NonNull
    ImageView asImageView();

    @UiThread
    void setPicassoFile(@Nullable File file);

    @UiThread
    void setPicassoUri(@Nullable Uri uri);

    @UiThread
    void setPlaceholder(@DrawableRes int placeholder);

    @UiThread
    void setPlaceholder(@Nullable Drawable placeholder);

    @UiThread
    void addTransform(@NonNull Transformation transform);

    @UiThread
    void setTransforms(@Nullable List<? extends Transformation> transforms);

    @UiThread
    void toggleBatchUpdates(boolean enable);

    /* Methods already present on View objects */
    @NonNull
    Context getContext();
}
