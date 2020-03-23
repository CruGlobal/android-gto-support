package org.ccci.gto.android.common.util;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.collection.LongSparseArray;

/**
 * @deprecated Since v3.5.0, use the LongSparseBooleanArray in gto-support-androidx-collection instead
 */
@Deprecated
public class LongSparseBooleanArray extends LongSparseArray<Boolean> implements Parcelable {
    public static final Parcelable.Creator<LongSparseBooleanArray> CREATOR =
            new Parcelable.Creator<LongSparseBooleanArray>() {
                @Override
                public LongSparseBooleanArray createFromParcel(@NonNull final Parcel source) {
                    // load data from the Parcel
                    final int size = source.readInt();
                    final long[] keys = new long[size];
                    source.readLongArray(keys);
                    final boolean[] values = new boolean[size];
                    source.readBooleanArray(values);

                    // create & return the sparse array
                    final LongSparseBooleanArray array = new LongSparseBooleanArray();
                    for (int i = 0; i < size; i++) {
                        array.put(keys[i], values[i]);
                    }
                    return array;
                }

                @Override
                public LongSparseBooleanArray[] newArray(final int size) {
                    return new LongSparseBooleanArray[size];
                }
            };

    public LongSparseBooleanArray() {}

    public LongSparseBooleanArray(final int initialCapacity) {
        super(initialCapacity);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull final Parcel dest, final int flags) {
        // read the data into a parcelable format
        final int size = size();
        final long[] keys = new long[size];
        final boolean[] values = new boolean[size];
        for (int i = 0; i < size; i++) {
            keys[i] = keyAt(i);
            values[i] = valueAt(i);
        }

        // write data to the Parcel
        dest.writeInt(size);
        dest.writeLongArray(keys);
        dest.writeBooleanArray(values);
    }
}
