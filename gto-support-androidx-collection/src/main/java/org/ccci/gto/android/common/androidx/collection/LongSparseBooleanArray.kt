package org.ccci.gto.android.common.androidx.collection

import android.os.Parcel
import android.os.Parcelable
import androidx.collection.LongSparseArray
import kotlinx.android.parcel.Parceler
import kotlinx.android.parcel.Parcelize

@Parcelize
class LongSparseBooleanArray : LongSparseArray<Boolean>(), Parcelable {
    override fun get(key: Long): Boolean = get(key, false)

    internal companion object : Parceler<LongSparseBooleanArray> {
        override fun LongSparseBooleanArray.write(parcel: Parcel, flags: Int) {
            val size = size()
            val keys = LongArray(size)
            val values = BooleanArray(size)
            for (i in 0 until size) {
                keys[i] = keyAt(i)
                values[i] = valueAt(i)
            }

            parcel.writeInt(size)
            parcel.writeLongArray(keys)
            parcel.writeBooleanArray(values)
        }

        override fun create(parcel: Parcel): LongSparseBooleanArray {
            val size = parcel.readInt()
            val keys = LongArray(size).also { parcel.readLongArray(it) }
            val values = BooleanArray(size).also { parcel.readBooleanArray(it) }
            return LongSparseBooleanArray()
                .apply { for (i in 0 until size) put(keys[i], values[i]) }
        }
    }
}
