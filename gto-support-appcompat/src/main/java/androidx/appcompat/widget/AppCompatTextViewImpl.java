package androidx.appcompat.widget;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;

import static androidx.appcompat.widget.ReflectionUtils.getDeclaredField;

public class AppCompatTextViewImpl extends AppCompatTextView {
    @NonNull
    private final AppCompatCompoundDrawableHelper mCompoundDrawableHelper;

    public AppCompatTextViewImpl(Context context) {
        this(context, null);
    }

    public AppCompatTextViewImpl(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.textViewStyle);
    }

    public AppCompatTextViewImpl(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mCompoundDrawableHelper =
                new AppCompatCompoundDrawableHelper(this, getDeclaredField(AppCompatTextView.class, "mTextHelper"));
        mCompoundDrawableHelper.loadFromAttributes(attrs, defStyleAttr);
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        mCompoundDrawableHelper.applyCompoundDrawablesTints();
    }
}
