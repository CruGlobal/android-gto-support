package android.support.v7.widget;

import android.content.res.ColorStateList;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;

import org.ccci.gto.android.common.appcompat.R;

import java.lang.reflect.Field;

import static android.support.v7.widget.ReflectionUtils.getDeclaredField;

@SuppressWarnings("RestrictedApi")
final class AppCompatTextCompoundDrawableHelper {
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

    AppCompatTextCompoundDrawableHelper(@NonNull final TextView view, @Nullable final Field textHelperField) {
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
                .obtainStyledAttributes(mView.getContext(), attrs, R.styleable.TextViewCompoundDrawableHelper,
                                        defStyleAttr, 0);
        if (a.hasValue(R.styleable.TextViewCompoundDrawableHelper_drawableTint)) {
            setSupportCompoundDrawableTintList(
                    a.getColorStateList(R.styleable.TextViewCompoundDrawableHelper_drawableTint));
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
