package org.ccci.gto.android.common.preference;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.preference.ListPreference;
import android.util.AttributeSet;

/**
 * @deprecated Since v3.10.0, use {@link ListPreference} directly.
 */
@Deprecated
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
}
