package org.ccci.gto.android.common.androidx.collection

import android.os.Parcel
import android.os.Parcelable
import androidx.collection.LongSparseArray
import kotlinx.android.parcel.Parceler
import kotlinx.android.parcel.Parcelize

@Parcelize
class LongSparseParcelableArray<T : Parcelable?> : LongSparseArray<T>(), Parcelable {
    private companion object : Parceler<LongSparseParcelableArray<Parcelable?>> {
        override fun LongSparseParcelableArray<Parcelable?>.write(parcel: Parcel, flags: Int) {
            val size = size()
            val keys = LongArray(size)
            val values = arrayOfNulls<Parcelable>(size)
            for (i in 0 until size) {
                keys[i] = keyAt(i)
                values[i] = valueAt(i)
            }

            parcel.writeInt(size)
            parcel.writeLongArray(keys)
            parcel.writeParcelableArray(values, 0)
        }

        override fun create(parcel: Parcel): LongSparseParcelableArray<Parcelable?> {
            val size = parcel.readInt()
            val keys = LongArray(size).also { parcel.readLongArray(it) }
            val values = parcel.readParcelableArray(LongSparseParcelableArray::class.java.classLoader)
            return LongSparseParcelableArray<Parcelable?>()
                .apply { for (i in 0 until size) put(keys[i], values?.get(i)) }
        }
    }
}
