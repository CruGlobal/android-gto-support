package org.ccci.gto.android.common.compat.os;

import android.os.Parcel;
import androidx.annotation.NonNull;

/**
 * @deprecated Since v3.2.0, use ParcelUtils from gto-support-utils instead.
 */
@Deprecated
public class ParcelCompat {
    /**
     * @deprecated Since v3.2.0, use ParcelUtils.writeBoolean from gto-support-utils instead.
     */
    @Deprecated
    public static void writeBoolean(@NonNull final Parcel parcel, final boolean value) {
        parcel.writeInt(value ? 1 : 0);
    }

    /**
     * @deprecated Since v3.2.0, use ParcelUtils.readBoolean from gto-support-utils instead.
     */
    @Deprecated
    public static boolean readBoolean(@NonNull final Parcel parcel) {
        return parcel.readInt() == 1;
    }
}
