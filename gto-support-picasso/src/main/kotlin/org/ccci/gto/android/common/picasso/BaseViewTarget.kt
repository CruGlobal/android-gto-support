package org.ccci.gto.android.common.picasso

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.View
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target

abstract class BaseViewTarget<V : View>(protected val view: V) : Target {
    final override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
        updateDrawable(placeHolderDrawable)
    }

    final override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom?) {
        updateDrawable(BitmapDrawable(view.resources, bitmap))
    }

    final override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
        updateDrawable(errorDrawable)
    }

    protected abstract fun updateDrawable(drawable: Drawable?)
}
