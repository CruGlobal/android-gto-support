package org.ccci.gto.android.common.okta.authfoundation

import android.os.Build
import androidx.annotation.DeprecatedSinceApi
import com.okta.authfoundation.AuthFoundationDefaults
import com.okta.authfoundation.client.OidcClock

@DeprecatedSinceApi(Build.VERSION_CODES.O)
fun AuthFoundationDefaults.enableClockCompat() = when {
    Build.VERSION.SDK_INT < Build.VERSION_CODES.O -> {
        clock = OidcClock { System.currentTimeMillis() / 1000 }
    }

    else -> Unit
}
