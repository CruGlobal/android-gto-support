package org.ccci.gto.android.common.content;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.util.LongSparseArray;

public final class IntersectingLongsBroadcastReceiver extends ForwardingBroadcastReceiver {
    private static final long[] NO_VALUES = new long[0];

    public static final String EXTRA_VALUES = IntersectingLongsBroadcastReceiver.class.getName() + ".EXTRA_VALUES";

    @NonNull
    private String mExtra = EXTRA_VALUES;
    private final LongSparseArray<Boolean> mValues = new LongSparseArray<>();

    @Override
    public void onReceive(final Context context, @NonNull final Intent intent) {
        // short-circuit if we don't have any values to match
        if (mValues.size() == 0) {
            return;
        }

        // short-circuit if there are no values in the intent
        final long[] values = intent.getLongArrayExtra(mExtra);
        if (values == null || values.length == 0) {
            return;
        }

        // look for an intersection
        for (final long value : values) {
            if (mValues.indexOfKey(value) >= 0) {
                // process the first intersection only, then return
                super.onReceive(context, intent);
                return;
            }
        }
    }

    public void setExtraName(@NonNull final String name) {
        this.mExtra = name;
    }

    public void addValues(@NonNull final long... values) {
        for (final long value : values) {
            mValues.put(value, Boolean.TRUE);
        }
    }

    public boolean containsValues() {
        return mValues.size() > 0;
    }

    public void removeValues(@NonNull final long... values) {
        for (final long value : values) {
            mValues.delete(value);
        }
    }
}
