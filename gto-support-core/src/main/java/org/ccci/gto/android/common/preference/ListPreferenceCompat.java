package org.ccci.gto.android.common.preference;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.preference.ListPreference;
import android.text.TextUtils;
import android.util.AttributeSet;

public class ListPreferenceCompat extends ListPreference {
    public ListPreferenceCompat(final Context context) {
        super(context);
    }

    public ListPreferenceCompat(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ListPreferenceCompat(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ListPreferenceCompat(final Context context, final AttributeSet attrs, final int defStyleAttr,
                                final int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    /**
     * Sets the value of the key. This should be one of the entries in
     * {@link #getEntryValues()}.
     *
     * @param value The value to set for the key.
     * @see <a href="http://stackoverflow.com/a/21642401/4721910">http://stackoverflow.com/a/21642401/4721910</a>
     */
    @Override
    public void setValue(final String value) {
        // Android pre-KitKat didn't let the preference know that data had changed, which caused stale Summaries.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            String oldValue = getValue();
            super.setValue(value);
            if (!TextUtils.equals(value, oldValue)) {
                notifyChanged();
            }
        } else {
            super.setValue(value);
        }
    }
}
