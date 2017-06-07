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

import org.ccci.gto.android.common.base.model.Dimension;
import org.ccci.gto.android.common.picasso.R;
import org.ccci.gto.android.common.picasso.transformation.ScaleTransformation;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.ccci.gto.android.common.base.Constants.INVALID_DRAWABLE_RES;

public interface PicassoImageView {
    class Helper {
        @NonNull
        protected final ImageView mView;

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

        @NonNull
        public final ImageView asImageView() {
            return mView;
        }

        public final void setPicassoUri(@Nullable final Uri uri) {
            mPicassoFile = null;
            mPicassoUri = uri;
            triggerUpdate();
        }

        public final void setPicassoFile(@Nullable final File file) {
            mPicassoUri = null;
            mPicassoFile = file;
            triggerUpdate();
        }

        public final void setPlaceholder(@DrawableRes final int placeholder) {
            mPlaceholder = null;
            mPlaceholderResId = placeholder;
            triggerUpdate();
        }

        public final void setPlaceholder(@Nullable final Drawable placeholder) {
            mPlaceholderResId = INVALID_DRAWABLE_RES;
            mPlaceholder = placeholder;
            triggerUpdate();
        }

        public final void addTransform(@NonNull final Transformation transformation) {
            mTransforms.add(transformation);
        }

        public final void setTransforms(@Nullable final List<? extends Transformation> transformations) {
            mTransforms.clear();
            if (transformations != null) {
                mTransforms.addAll(transformations);
            }
        }

        public final void onSizeChanged(int w, int h, int oldw, int oldh) {
            if (oldw != w || oldh != h) {
                mSize = new Dimension(w, h);
                triggerUpdate();
            }
        }

        public final void setScaleType(@NonNull final ScaleType type) {
            triggerUpdate();
        }

        private void triggerUpdate() {
            // short-circuit if we are in edit mode within a development tool
            if (mView.isInEditMode()) {
                return;
            }

            // create base request
            final RequestCreator update = onCreateUpdate(Picasso.with(mView.getContext()));

            // set placeholder & any transform options
            if (mPlaceholderResId != INVALID_DRAWABLE_RES) {
                update.placeholder(mPlaceholderResId);
            } else {
                update.placeholder(mPlaceholder);
            }

            if (mSize.width > 0 || mSize.height > 0) {
                onSetUpdateScale(update, mSize);
            }

            update.transform(mTransforms);

            // fetch or load based on the target size
            if (mSize.width > 0 || mSize.height > 0) {
                update.into(mView);
            } else {
                update.fetch();
            }
        }

        @NonNull
        protected RequestCreator onCreateUpdate(@NonNull final Picasso picasso) {
            if (mPicassoFile != null) {
                return picasso.load(mPicassoFile);
            } else {
                return picasso.load(mPicassoUri);
            }
        }

        protected void onSetUpdateScale(@NonNull final RequestCreator update, @NonNull final Dimension size) {
            switch (mView.getScaleType()) {
                case CENTER_CROP:
                    update.resize(size.width, size.height);
                    update.onlyScaleDown();
                    update.centerCrop();
                    break;
                case CENTER_INSIDE:
                case FIT_CENTER:
                case FIT_START:
                case FIT_END:
                    update.resize(size.width, size.height);
                    update.onlyScaleDown();
                    update.centerInside();
                    break;
                default:
                    update.transform(new ScaleTransformation(size.width, size.height));
                    break;
            }
        }
    }

    /**
     * @return The ImageView this PicassoImageView represents.
     */
    @NonNull
    ImageView asImageView();

    void setPicassoFile(@Nullable File file);

    void setPicassoUri(@Nullable Uri uri);

    void setPlaceholder(@DrawableRes int placeholder);

    void setPlaceholder(@Nullable Drawable placeholder);

    void addTransform(@NonNull Transformation transform);

    void setTransforms(@Nullable List<? extends Transformation> transforms);

    /* Methods already present on View objects */
    @NonNull
    Context getContext();
}
