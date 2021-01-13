@file:JvmName("PicassoUtils")

package org.ccci.gto.android.common.picasso.util

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import androidx.appcompat.widget.TintContextWrapper
import com.squareup.picasso.Picasso
import org.ccci.gto.android.common.util.getDeclaredFieldOrNull
import timber.log.Timber

private val contextField by lazy { getDeclaredFieldOrNull<Picasso.Builder>("context") }

@SuppressLint("RestrictedApi")
fun Picasso.Builder.injectVectorAwareContext(context: Context): Picasso.Builder {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
        try {
            // forcibly inject a TintContextWrapper context to support loading Support VectorDrawables
            contextField?.set(this, TintContextWrapper.wrap(context.applicationContext))
        } catch (e: Exception) {
            Timber.tag("PicassoUtils").e(e, "Error injecting a vector aware context object into Picasso")
        }
    }
    return this
}
