package org.ccci.gto.android.common.picasso.transformation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;

import com.squareup.picasso.Transformation;

public class MaskTransformation implements Transformation {
    private static final Paint PAINT_MASK = new Paint();

    static {
        PAINT_MASK.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
    }

    @NonNull
    private final Context mContext;
    @DrawableRes
    private final int mMask;

    public MaskTransformation(@NonNull final Context context, @DrawableRes final int mask) {
        mContext = context;
        mMask = mask;
    }

    @Override
    public String key() {
        return "MaskTransformation(mask=" + mContext.getResources().getResourceEntryName(mMask) + ")";
    }

    @Override
    public Bitmap transform(@NonNull final Bitmap source) {
        final int w = source.getWidth();
        final int h = source.getHeight();

        final Drawable mask = ContextCompat.getDrawable(mContext, mMask);
        if (mask == null) {
            throw new IllegalArgumentException("Unable to load mask");
        }

        final Bitmap result = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(result);
        mask.setBounds(0, 0, w, h);
        mask.draw(canvas);
        canvas.drawBitmap(source, 0, 0, PAINT_MASK);

        source.recycle();

        return result;
    }
}
