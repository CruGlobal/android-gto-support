package org.ccci.gto.android.common.picasso.view;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Transformation;

import org.ccci.gto.android.common.base.model.Dimension;

import java.util.ArrayList;
import java.util.List;

import static org.ccci.gto.android.common.base.Constants.INVALID_DRAWABLE_RES;

public abstract class BaseHelper {
    @NonNull
    protected final ImageView mView;

    @NonNull
    Dimension mSize = new Dimension(0, 0);
    @DrawableRes
    int mPlaceholderResId = INVALID_DRAWABLE_RES;
    @Nullable
    Drawable mPlaceholder = null;
    private final ArrayList<Transformation> mTransforms = new ArrayList<>();

    int mBatching = 0;
    boolean mNeedsUpdate = false;

    public BaseHelper(@NonNull final ImageView view) {
        mView = view;
    }

    @UiThread
    public final void addTransform(@NonNull final Transformation transformation) {
        mTransforms.add(transformation);
        triggerUpdate();
    }

    @UiThread
    public final void setTransforms(@Nullable final List<? extends Transformation> transformations) {
        mTransforms.clear();
        if (transformations != null) {
            mTransforms.addAll(transformations);
        }
        triggerUpdate();
    }

    void postTriggerUpdate() {
        mNeedsUpdate = true;
        mView.post(() -> {
            if (mNeedsUpdate) {
                triggerUpdate();
            }
        });
    }

    @UiThread
    protected final void triggerUpdate() {
        // short-circuit if we are in edit mode within a development tool
        if (mView.isInEditMode()) {
            return;
        }

        // if we are batching updates, track that we need an update, but don't trigger the update now
        if (mBatching > 0) {
            mNeedsUpdate = true;
            return;
        }

        // if we are currently in a layout pass, trigger an update once layout is complete
        if (mView.isInLayout()) {
            postTriggerUpdate();
            return;
        }

        // clear the needs update flag
        mNeedsUpdate = false;

        // create base request
        final RequestCreator update = onCreateUpdate(Picasso.get());

        // set placeholder & any transform options
        if (mPlaceholderResId != INVALID_DRAWABLE_RES) {
            update.placeholder(mPlaceholderResId);
        } else if (mPlaceholder != null) {
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
    @UiThread
    protected abstract RequestCreator onCreateUpdate(@NonNull final Picasso picasso);

    @UiThread
    protected abstract void onSetUpdateScale(@NonNull final RequestCreator update, @NonNull final Dimension size);
}
