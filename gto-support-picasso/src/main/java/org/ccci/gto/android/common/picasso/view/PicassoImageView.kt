package org.ccci.gto.android.common.picasso.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.AttributeSet
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.annotation.UiThread
import com.squareup.picasso.Transformation
import java.io.File

interface PicassoImageView {
    open class Helper : BaseHelper {
        constructor(view: ImageView) : super(view)
        constructor(view: ImageView, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) :
            super(view, attrs, defStyleAttr, defStyleRes)
    }

    /**
     * @return The ImageView this PicassoImageView represents.
     */
    fun asImageView(): ImageView

    @UiThread
    fun setPicassoFile(file: File?)

    @UiThread
    fun setPicassoUri(uri: Uri?)

    @UiThread
    fun setPlaceholder(@DrawableRes placeholder: Int)

    @UiThread
    fun setPlaceholder(placeholder: Drawable?)

    @UiThread
    fun addTransform(transform: Transformation)

    @UiThread
    fun setTransforms(transforms: List<Transformation?>?)

    @UiThread
    fun toggleBatchUpdates(enable: Boolean)

    /* Methods already present on View objects */
    fun getContext(): Context
}
