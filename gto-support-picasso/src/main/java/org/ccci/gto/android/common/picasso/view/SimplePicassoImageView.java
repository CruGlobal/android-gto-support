package org.ccci.gto.android.common.picasso.view;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.squareup.picasso.Transformation;

import java.io.File;
import java.util.List;

@SuppressLint("AppCompatCustomView")
public class SimplePicassoImageView extends ImageView implements PicassoImageView {
    private boolean mInit = false;
    @NonNull
    private final Helper mHelper;

    public SimplePicassoImageView(@NonNull final Context context) {
        this(context, null);
    }

    public SimplePicassoImageView(@NonNull final Context context, @Nullable final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SimplePicassoImageView(@NonNull final Context context, @Nullable final AttributeSet attrs,
                                  final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mHelper = new Helper(this, attrs, defStyleAttr, 0);
        mInit = true;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SimplePicassoImageView(@NonNull final Context context, @Nullable final AttributeSet attrs,
                                  final int defStyleAttr, final int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mHelper = new Helper(this, attrs, defStyleAttr, defStyleRes);
        mInit = true;
    }

    @Override
    public final void setPicassoFile(@Nullable final File file) {
        mHelper.setPicassoFile(file);
    }

    @Override
    public final void setPicassoUri(@Nullable final Uri uri) {
        mHelper.setPicassoUri(uri);
    }

    @Override
    public final void setPlaceholder(@DrawableRes final int placeholder) {
        mHelper.setPlaceholder(placeholder);
    }

    @Override
    public void setPlaceholder(@Nullable final Drawable placeholder) {
        mHelper.setPlaceholder(placeholder);
    }

    @Override
    public void addTransform(@NonNull final Transformation transform) {
        mHelper.addTransform(transform);
    }

    @Override
    public void setTransforms(@Nullable List<? extends Transformation> transforms) {
        mHelper.setTransforms(transforms);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mHelper.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    public void setScaleType(ScaleType scaleType) {
        super.setScaleType(scaleType);
        if (mInit) {
            mHelper.setScaleType(scaleType);
        }
    }
}
