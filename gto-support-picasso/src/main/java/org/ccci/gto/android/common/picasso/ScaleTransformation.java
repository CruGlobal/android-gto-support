package org.ccci.gto.android.common.picasso;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.squareup.picasso.Transformation;

public class ScaleTransformation implements Transformation {
    private final int mMinWidth;
    private final int mMinHeight;
    private final boolean mOnlyScaleDown;

    @Nullable
    private transient String mKey;

    public ScaleTransformation(final int minWidth, final int minHeight) {
        mMinWidth = minWidth;
        mMinHeight = minHeight;
        mOnlyScaleDown = true;
    }

    @NonNull
    @Override
    public Bitmap transform(@NonNull final Bitmap source) {
        final int inWidth = source.getWidth();
        final int inHeight = source.getHeight();
        if (inWidth <= 0 || inHeight <= 0) {
            return source;
        }

        // calculate target size enforcing minWidth & minHeight
        float scale = ((float) mMinWidth) / inWidth;
        if (scale * inHeight < mMinHeight) {
            scale = ((float) mMinHeight) / inHeight;
        }

        // only scale if necessary
        if (!mOnlyScaleDown || scale < 1) {
            Matrix matrix = new Matrix();
            matrix.postScale(scale, scale);
            final Bitmap target = Bitmap.createBitmap(source, 0, 0, inWidth, inHeight, matrix, false);

            // recycle source if we have a different target
            if (target != source) {
                source.recycle();
            }

            return target;
        }

        // return the source if we aren't scaling
        return source;
    }

    @Override
    public String key() {
        if (mKey == null) {
            mKey = "scale(minWidth=" + mMinWidth + ",minHeight=" + mMinHeight + ",onlyScaleDown=" + mOnlyScaleDown +
                    ")";
        }

        return mKey;
    }
}
