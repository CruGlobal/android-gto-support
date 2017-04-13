package android.support.v7.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.appcompat.R;
import android.util.AttributeSet;

import static android.support.v7.widget.ReflectionUtils.getDeclaredField;

public class AppCompatButtonImpl extends AppCompatButton {
    @NonNull
    private final AppCompatTextCompoundDrawableHelper mCompoundDrawableHelper;

    public AppCompatButtonImpl(Context context) {
        this(context, null);
    }

    public AppCompatButtonImpl(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.buttonStyle);
    }

    public AppCompatButtonImpl(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mCompoundDrawableHelper =
                new AppCompatTextCompoundDrawableHelper(this, getDeclaredField(AppCompatButton.class, "mTextHelper"));
        mCompoundDrawableHelper.loadFromAttributes(attrs, defStyleAttr);
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        mCompoundDrawableHelper.applyCompoundDrawablesTints();
    }
}
