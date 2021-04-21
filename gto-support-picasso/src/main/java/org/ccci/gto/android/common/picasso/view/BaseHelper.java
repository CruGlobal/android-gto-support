package org.ccci.gto.android.common.picasso.view;

import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;

import com.squareup.picasso.Transformation;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseHelper {
    @NonNull
    protected final ImageView mView;

    final ArrayList<Transformation> mTransforms = new ArrayList<>();

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

    protected abstract void triggerUpdate();
}
