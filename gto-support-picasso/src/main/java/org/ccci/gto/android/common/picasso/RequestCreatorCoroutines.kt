package org.ccci.gto.android.common.picasso

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import com.squareup.picasso.Picasso
import com.squareup.picasso.RequestCreator
import com.squareup.picasso.Target
import com.squareup.picasso.picasso
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext

suspend fun RequestCreator.getBitmap(): Bitmap = withContext(Dispatchers.Main) {
    suspendCancellableCoroutine { cont ->
        val target = BitmapContinuationTarget(cont)
        into(target)
        cont.invokeOnCancellation {
            @OptIn(DelicateCoroutinesApi::class)
            GlobalScope.launch(Dispatchers.Main.immediate) { picasso?.cancelRequest(target) }
        }
    }
}

private class BitmapContinuationTarget(private val cont: CancellableContinuation<Bitmap>) : Target {
    override fun onPrepareLoad(placeHolderDrawable: Drawable?) = Unit
    override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom?) = cont.resume(bitmap)
    override fun onBitmapFailed(e: Exception, errorDrawable: Drawable?) = cont.resumeWithException(e)
}
