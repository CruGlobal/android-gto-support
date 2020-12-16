package org.ccci.gto.android.common.picasso

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import com.squareup.picasso.Picasso
import com.squareup.picasso.RequestCreator
import com.squareup.picasso.Target
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun RequestCreator.getBitmap(): Bitmap =
    withContext(Dispatchers.Main) { suspendCoroutine { into(BitmapContinuationTarget(it)) } }

private class BitmapContinuationTarget(private val cont: Continuation<Bitmap>) : Target {
    override fun onPrepareLoad(placeHolderDrawable: Drawable?) = Unit
    override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom?) = cont.resume(bitmap)
    override fun onBitmapFailed(e: Exception, errorDrawable: Drawable?) = cont.resumeWithException(e)
}
