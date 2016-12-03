package org.ccci.gto.android.common.compat.os;

import android.os.Parcel;
import android.support.annotation.NonNull;

public class ParcelCompat {
    public static void writeBoolean(@NonNull final Parcel parcel, final boolean value) {
        parcel.writeInt(value ? 1 : 0);
    }

    public static boolean readBoolean(@NonNull final Parcel parcel) {
        return parcel.readInt() == 1;
    }
}
