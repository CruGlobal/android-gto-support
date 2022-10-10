package org.ccci.gto.android.common.content;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @deprecated Since v3.14.0, instead of broadcasting state changes utilize observable data holders instead.
 * e.g. {@link androidx.lifecycle.LiveData} or coroutines {@link kotlinx.coroutines.flow.Flow}
 */
@Deprecated
public final class IntersectingStringsBroadcastReceiver extends ForwardingBroadcastReceiver {
    public static final String EXTRA_VALUES = IntersectingStringsBroadcastReceiver.class.getName() + ".EXTRA_VALUES";

    @NonNull
    private String mExtra = EXTRA_VALUES;
    private final Set<String> mValues = new HashSet<>();

    @Override
    public void onReceive(final Context context, @NonNull final Intent intent) {
        // short-circuit if we don't have any values to match
        if (mValues.size() == 0) {
            return;
        }

        // short-circuit if there are no values in the intent
        final String[] values = intent.getStringArrayExtra(mExtra);
        if (values == null || values.length == 0) {
            return;
        }

        // look for an intersection
        for (final String value : values) {
            if (mValues.contains(value)) {
                // process the first intersection only, then return
                super.onReceive(context, intent);
                return;
            }
        }
    }

    public void setExtraName(@NonNull final String name) {
        this.mExtra = name;
    }

    public void addValues(@NonNull final String... values) {
        mValues.addAll(Arrays.asList(values));
    }

    public boolean containsValues() {
        return mValues.size() > 0;
    }

    public void removeValues(@NonNull final String... values) {
        mValues.removeAll(Arrays.asList(values));
    }
}
