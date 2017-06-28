package org.ccci.gto.android.common.util;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.os.ParcelableCompat;
import android.support.v4.os.ParcelableCompatCreatorCallbacks;
import android.support.v4.util.LongSparseArray;

public class ParcelableLongSparseArray<T extends Parcelable> extends LongSparseArray<T> implements Parcelable {
    public ParcelableLongSparseArray() {}

    public ParcelableLongSparseArray(final int initialCapacity) {
        super(initialCapacity);
    }

    ParcelableLongSparseArray(@NonNull final Parcel in, @Nullable ClassLoader loader) {
        if (loader == null) {
            loader = getClass().getClassLoader();
        }

        // load data from the Parcel
        final int size = in.readInt();
        final long[] keys = new long[size];
        in.readLongArray(keys);
        final Parcelable[] values = in.readParcelableArray(loader);

        // create & return the sparse array
        for (int i = 0; i < size; i++) {
            //noinspection unchecked
            put(keys[i], (T) values[i]);
        }
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
        final Parcelable[] values = new Parcelable[size];
        for (int i = 0; i < size; i++) {
            keys[i] = keyAt(i);
            values[i] = valueAt(i);
        }

        // write data to the Parcel
        dest.writeInt(size);
        dest.writeLongArray(keys);
        dest.writeParcelableArray(values, 0);
    }

    public static final Parcelable.Creator<ParcelableLongSparseArray> CREATOR =
            ParcelableCompat.newCreator(new ParcelableCompatCreatorCallbacks<ParcelableLongSparseArray>() {
                @Override
                public ParcelableLongSparseArray createFromParcel(@NonNull final Parcel in,
                                                                  @Nullable final ClassLoader loader) {
                    return new ParcelableLongSparseArray(in, loader);
                }

                @Override
                public ParcelableLongSparseArray[] newArray(final int size) {
                    return new ParcelableLongSparseArray[size];
                }
            });
}
