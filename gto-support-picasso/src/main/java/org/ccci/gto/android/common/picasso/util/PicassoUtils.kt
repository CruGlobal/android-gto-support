@file:JvmName("PicassoUtils")

package org.ccci.gto.android.common.picasso.util

import android.content.Context
import com.squareup.picasso.Picasso

@Deprecated(
    "Since v3.9.0, we no longer support Android pre-lollipop, so this functionality is a no-op",
    ReplaceWith("this")
)
fun Picasso.Builder.injectVectorAwareContext(context: Context) = this
