package org.ccci.gto.android.common.picasso.view

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.util.AttributeSet
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.squareup.picasso.Transformation
import java.io.File

@SuppressLint("AppCompatCustomView")
open class SimplePicassoImageView : ImageView, PicassoImageView {
    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
    ) : super(context, attrs, defStyleAttr) {
        _helper = PicassoImageView.Helper(this, attrs, defStyleAttr)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        _helper = PicassoImageView.Helper(this, attrs, defStyleAttr, defStyleRes)
    }

    // TODO: convert to lazy initialization once our minimum supported android version is Lollipop
    private val _helper: PicassoImageView.Helper
    protected open val helper: PicassoImageView.Helper get() = _helper

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
        @Suppress("UNNECESSARY_SAFE_CALL")
        helper?.onSetScaleType()
    }
}
