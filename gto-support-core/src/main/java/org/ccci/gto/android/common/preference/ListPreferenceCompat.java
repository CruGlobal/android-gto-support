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
     * Returns the summary of this ListPreference. If the summary
     * has a {@linkplain java.lang.String#format String formatting}
     * marker in it (i.e. "%s" or "%1$s"), then the current entry
     * value will be substituted in its place.
     *
     * @return the summary with appropriate string substitution
     */
    @Override
    public CharSequence getSummary() {
        // Android pre-Honeycomb didn't support String formatting for the Summary, so we replicate that behavior here
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) {
            final CharSequence summary = super.getSummary();
            final CharSequence entry = getEntry();
            if (summary == null || entry == null) {
                return summary;
            } else {
                return String.format(summary.toString(), entry);
            }
        } else {
            return super.getSummary();
        }
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
