package org.ccci.gto.android.common.picasso.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Transformation;

import org.ccci.gto.android.common.model.Dimension;
import org.ccci.gto.android.common.picasso.R;
import org.ccci.gto.android.common.picasso.ScaleTransformation;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.ccci.gto.android.common.Constants.INVALID_DRAWABLE_RES;

public interface PicassoImageView {
    final class Helper {
        @NonNull
        private final ImageView mView;

        @Nullable
        private Uri mPicassoUri;
        @Nullable
        private File mPicassoFile;
        @NonNull
        private Dimension mSize = new Dimension(0, 0);
        @DrawableRes
        private int mPlaceholderResId = INVALID_DRAWABLE_RES;
        @Nullable
        private Drawable mPlaceholder = null;
        private final ArrayList<Transformation> mTransforms = new ArrayList<>();

        public Helper(@NonNull final ImageView view) {
            this(view, null, 0, 0);
        }

        public Helper(@NonNull final ImageView view, @Nullable final AttributeSet attrs, final int defStyleAttr,
                      final int defStyleRes) {
            mView = view;
            init(mView.getContext(), attrs, defStyleAttr, defStyleRes);
        }

        private void init(@NonNull final Context context, @Nullable final AttributeSet attrs, final int defStyleAttr,
                          final int defStyleRes) {
            final TypedArray a =
                    context.obtainStyledAttributes(attrs, R.styleable.PicassoImageView, defStyleAttr, defStyleRes);
            mPlaceholderResId = a.getResourceId(R.styleable.PicassoImageView_placeholder, INVALID_DRAWABLE_RES);
            a.recycle();
        }

        public void setPicassoUri(@Nullable final Uri uri) {
            mPicassoFile = null;
            mPicassoUri = uri;
            triggerUpdate();
        }

        public void setPicassoFile(@Nullable final File file) {
            mPicassoUri = null;
            mPicassoFile = file;
            triggerUpdate();
        }

        public void setPlaceholder(@DrawableRes final int placeholder) {
            mPlaceholder = null;
            mPlaceholderResId = placeholder;
            triggerUpdate();
        }

        public void setPlaceholder(@Nullable final Drawable placeholder) {
            mPlaceholderResId = INVALID_DRAWABLE_RES;
            mPlaceholder = placeholder;
            triggerUpdate();
        }

        public void addTransform(@NonNull final Transformation transformation) {
            mTransforms.add(transformation);
        }

        public void setTransforms(@Nullable final List<? extends Transformation> transformations) {
            mTransforms.clear();
            if (transformations != null) {
                mTransforms.addAll(transformations);
            }
        }

        public void onSizeChanged(int w, int h, int oldw, int oldh) {
            if (oldw != w || oldh != h) {
                mSize = new Dimension(w, h);
                triggerUpdate();
            }
        }

        public void setScaleType(final ScaleType type) {
            triggerUpdate();
        }

        private void triggerUpdate() {
            // short-circuit if we are in edit mode within a development tool
            if (mView.isInEditMode()) {
                return;
            }

            // create base request
            final Picasso picasso = Picasso.with(mView.getContext());
            final RequestCreator update;
            if (mPicassoFile != null) {
                update = picasso.load(mPicassoFile);
            } else {
                update = picasso.load(mPicassoUri);
            }

            // set placeholder & any transform options
            if (mPlaceholderResId != INVALID_DRAWABLE_RES) {
                update.placeholder(mPlaceholderResId);
            } else {
                update.placeholder(mPlaceholder);
            }
            if (mSize.width > 0 || mSize.height > 0) {
                switch (mView.getScaleType()) {
                    case CENTER_CROP:
                        update.resize(mSize.width, mSize.height);
                        update.onlyScaleDown();
                        update.centerCrop();
                        break;
                    case CENTER_INSIDE:
                        update.resize(mSize.width, mSize.height);
                        update.onlyScaleDown();
                        update.centerInside();
                        break;
                    default:
                        update.transform(new ScaleTransformation(mSize.width, mSize.height));
                        break;
                }
            }
            update.transform(mTransforms);

            // fetch or load based on the target size
            if (mSize.width > 0 || mSize.height > 0) {
                update.into(mView);
            } else {
                update.fetch();
            }
        }
    }

    void setPicassoFile(@Nullable File file);

    void setPicassoUri(@Nullable Uri uri);

    void setPlaceholder(@DrawableRes int placeholder);

    void setPlaceholder(@Nullable Drawable placeholder);

    void addTransform(@NonNull Transformation transform);

    void setTransforms(@Nullable List<? extends Transformation> transforms);

    /* Methods already present on View objects */
    Context getContext();
}
