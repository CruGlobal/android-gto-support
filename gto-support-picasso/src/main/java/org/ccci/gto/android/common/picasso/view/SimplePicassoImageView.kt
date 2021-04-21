package org.ccci.gto.android.common.picasso.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.AttributeSet
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatImageView
import com.squareup.picasso.Transformation
import java.io.File

open class SimplePicassoImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr), PicassoImageView {
    protected open val helper by lazy { PicassoImageView.Helper(this, attrs, defStyleAttr) }
    private val initialized = true

    override fun setPicassoFile(file: File?) = helper.setPicassoFile(file)
    override fun setPicassoUri(uri: Uri?) = helper.setPicassoUri(uri)
    override fun setPlaceholder(@DrawableRes placeholder: Int) = helper.setPlaceholder(placeholder)
    override fun setPlaceholder(placeholder: Drawable?) = helper.setPlaceholder(placeholder)
    override fun addTransform(transform: Transformation) = helper.addTransform(transform)
    override fun setTransforms(transforms: List<Transformation>?) = helper.setTransforms(transforms)
    override fun toggleBatchUpdates(enable: Boolean) = helper.toggleBatchUpdates(enable)

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        helper.onSizeChanged(w, h, oldw, oldh)
    }

    override fun setScaleType(scaleType: ScaleType) {
        super.setScaleType(scaleType)

        // HACK: setScaleType() could be called by a parent constructor, which can cause this statement to crash. The
        //       current logic handles several possible crashes, but doesn't handle all the possibilities subclasses
        //       could introduce.
        @Suppress("UNNECESSARY_SAFE_CALL")
        if (initialized) helper?.onSetScaleType()
    }
}
