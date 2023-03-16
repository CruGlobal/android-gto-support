package org.ccci.gto.android.common.play.auth.signin

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import kotlinx.coroutines.flow.map
import org.ccci.gto.android.common.play.auth.signin.internal.getStorageChangeFlow

object GoogleSignInKtx {
    fun getLastSignedInAccountFlow(context: Context) = context.getStorageChangeFlow()
        .map { GoogleSignIn.getLastSignedInAccount(context) }
}
