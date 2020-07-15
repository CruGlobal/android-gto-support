package androidx.appcompat.widget;

import android.content.Context;
import android.util.AttributeSet;

/**
 * @deprecated Since v3.6.1, use AppCompatRadioButton directly.
 */
@Deprecated
@SuppressWarnings("RestrictedApi")
public class AppCompatRadioButtonImpl extends AppCompatRadioButton {
    public AppCompatRadioButtonImpl(final Context context) {
        this(context, null);
    }

    public AppCompatRadioButtonImpl(final Context context, final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AppCompatRadioButtonImpl(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
