package org.ccci.gto.android.common.picasso.transformation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;

import com.squareup.picasso.Transformation;

import java.lang.ref.WeakReference;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

public class MaskTransformation implements Transformation {
    private static final Paint PAINT_MASK = new Paint();

    static {
        PAINT_MASK.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
    }

    @NonNull
    private final Context mApplicationContext;
    @NonNull
    private final WeakReference<Context> mContext;
    @DrawableRes
    private final int mMask;

    public MaskTransformation(@NonNull final Context context, @DrawableRes final int mask) {
        mApplicationContext = context.getApplicationContext();
        mContext = new WeakReference<>(context);
        mMask = mask;
    }

    @NonNull
    private Context getContext() {
        Context context = mContext.get();
        if (context == null) {
            context = mApplicationContext;
        }
        return context;
    }

    @Override
    public String key() {
        return "MaskTransformation(mask=" + getContext().getResources().getResourceEntryName(mMask) + ")";
    }

    @Override
    public Bitmap transform(@NonNull final Bitmap source) {
        final int w = source.getWidth();
        final int h = source.getHeight();

        final Drawable mask = ContextCompat.getDrawable(getContext(), mMask);
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
