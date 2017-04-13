package android.support.v7.widget;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;

import org.ccci.gto.android.common.appcompat.R;

import java.lang.reflect.Field;

import static android.support.v7.widget.ReflectionUtils.getDeclaredField;

@SuppressWarnings("RestrictedApi")
final class AppCompatCompoundDrawableHelper {
    private final TextView mView;
    @Nullable
    private final AppCompatTextHelper mTextHelper;

    @Nullable
    private static final Field START_TINT = getDeclaredField(AppCompatTextHelperV17.class, "mDrawableStartTint");
    @Nullable
    private static final Field LEFT_TINT = getDeclaredField(AppCompatTextHelper.class, "mDrawableLeftTint");
    @Nullable
    private static final Field TOP_TINT = getDeclaredField(AppCompatTextHelper.class, "mDrawableTopTint");
    @Nullable
    private static final Field END_TINT = getDeclaredField(AppCompatTextHelperV17.class, "mDrawableEndTint");
    @Nullable
    private static final Field RIGHT_TINT = getDeclaredField(AppCompatTextHelper.class, "mDrawableRightTint");
    @Nullable
    private static final Field BOTTOM_TINT = getDeclaredField(AppCompatTextHelper.class, "mDrawableBottomTint");
    private static final Field[] TINT_FIELDS = {START_TINT, LEFT_TINT, TOP_TINT, END_TINT, RIGHT_TINT, BOTTOM_TINT};

    AppCompatCompoundDrawableHelper(@NonNull final TextView view, @Nullable final Field textHelperField) {
        mView = view;

        AppCompatTextHelper helper = null;
        if (textHelperField != null) {
            try {
                helper = (AppCompatTextHelper) textHelperField.get(view);
            } catch (final IllegalAccessException ignored) {
            }
        }
        mTextHelper = helper;
    }

    void loadFromAttributes(@NonNull final AttributeSet attrs, final int defStyleAttr) {
        final TintTypedArray a = TintTypedArray
                .obtainStyledAttributes(mView.getContext(), attrs, R.styleable.AppCompatCompoundDrawableHelper,
                                        defStyleAttr, 0);

        final Drawable drawableStart = a.getDrawable(R.styleable.AppCompatCompoundDrawableHelper_drawableStart);
        final Drawable drawableLeft = a.getDrawable(R.styleable.AppCompatCompoundDrawableHelper_drawableLeft);
        final Drawable drawableTop = a.getDrawable(R.styleable.AppCompatCompoundDrawableHelper_drawableTop);
        final Drawable drawableEnd = a.getDrawable(R.styleable.AppCompatCompoundDrawableHelper_drawableEnd);
        final Drawable drawableRight = a.getDrawable(R.styleable.AppCompatCompoundDrawableHelper_drawableRight);
        final Drawable drawableBottom = a.getDrawable(R.styleable.AppCompatCompoundDrawableHelper_drawableBottom);

        // We can only choose absolute positioned drawables or relative positioned drawables, not both
        if (drawableStart != null || drawableEnd != null) {
            // prefer relative drawables if defined
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                mView.setCompoundDrawablesRelativeWithIntrinsicBounds(drawableStart, drawableTop, drawableEnd,
                                                                      drawableBottom);
            } else {
                // no RTL support on older devices anyways, just default to left/right for start/end
                mView.setCompoundDrawablesWithIntrinsicBounds(drawableStart, drawableTop, drawableEnd, drawableBottom);
            }
        } else if (drawableLeft != null || drawableRight != null || drawableTop != null || drawableBottom != null) {
            // fallback to absolute drawables if defined
            mView.setCompoundDrawablesWithIntrinsicBounds(drawableLeft, drawableTop, drawableRight, drawableBottom);
        }

        // handle compound drawable tint
        if (a.hasValue(R.styleable.AppCompatCompoundDrawableHelper_drawableTint)) {
            setSupportCompoundDrawableTintList(
                    a.getColorStateList(R.styleable.AppCompatCompoundDrawableHelper_drawableTint));
        }
        a.recycle();
    }

    void setSupportCompoundDrawableTintList(@Nullable final ColorStateList tintList) {
        if (mTextHelper != null) {
            for (final Field field : TINT_FIELDS) {
                if (field != null) {
                    try {
                        TintInfo tintInfo = (TintInfo) field.get(mTextHelper);
                        if (tintInfo == null) {
                            tintInfo = new TintInfo();
                            field.set(mTextHelper, tintInfo);
                        }

                        tintInfo.mTintList = tintList;
                        tintInfo.mHasTintList = true;
                    } catch (final Exception ignored) {
                    }
                }
            }

            applyCompoundDrawablesTints();
        }
    }

    void applyCompoundDrawablesTints() {
        if (mTextHelper != null) {
            mTextHelper.applyCompoundDrawablesTints();
        }
    }
}
