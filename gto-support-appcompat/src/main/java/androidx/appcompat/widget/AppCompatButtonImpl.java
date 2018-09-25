package androidx.appcompat.widget;

import android.content.Context;
import android.util.AttributeSet;

import org.ccci.gto.android.common.appcompat.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static androidx.appcompat.widget.ReflectionUtils.getDeclaredField;

public class AppCompatButtonImpl extends AppCompatButton {
    private final AppCompatCompoundDrawableHelper mCompoundDrawableHelper;

    public AppCompatButtonImpl(@NonNull final Context context) {
        this(context, null);
    }

    public AppCompatButtonImpl(@NonNull final Context context, @Nullable final AttributeSet attrs) {
        this(context, attrs, R.attr.buttonStyle);
    }

    public AppCompatButtonImpl(@NonNull final Context context, @Nullable final AttributeSet attrs,
                               final int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mCompoundDrawableHelper =
                new AppCompatCompoundDrawableHelper(this, getDeclaredField(AppCompatButton.class, "mTextHelper"));
        mCompoundDrawableHelper.loadFromAttributes(attrs, defStyleAttr);
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        if (mCompoundDrawableHelper != null) {
            mCompoundDrawableHelper.applyCompoundDrawablesTints();
        }
    }
}
