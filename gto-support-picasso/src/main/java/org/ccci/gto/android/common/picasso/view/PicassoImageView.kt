package org.ccci.gto.android.common.picasso.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.AttributeSet
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageView
import android.widget.ImageView.ScaleType
import androidx.annotation.DrawableRes
import androidx.annotation.UiThread
import com.squareup.picasso.Picasso
import com.squareup.picasso.RequestCreator
import com.squareup.picasso.Transformation
import java.io.File
import org.ccci.gto.android.common.base.model.Dimension
import org.ccci.gto.android.common.picasso.transformation.ScaleTransformation

interface PicassoImageView {
    open class Helper(view: ImageView, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0) :
        BaseHelper(view, attrs, defStyleAttr, defStyleRes) {
        protected val imageView: ImageView = view

        private var _picassoUri: Uri? = null
        internal var picassoUri: Uri?
            get() = _picassoUri
            set(value) {
                val changing = _picassoFile != null || value != _picassoUri
                if(value != null) _picassoFile = null
                _picassoUri = value
                if (changing) triggerUpdate()
            }

        private var _picassoFile: File? = null
        internal var picassoFile: File?
            get() = _picassoFile
            set(value) {
                val changing = value != _picassoFile
                if (value != null) _picassoUri = null
                _picassoFile = value
                if (changing) triggerUpdate()
            }

        @UiThread
        override fun onCreateUpdate(picasso: Picasso): RequestCreator =
            picassoFile?.let { picasso.load(it) } ?: picasso.load(picassoUri)

        @UiThread
        override fun onSetUpdateScale(update: RequestCreator, size: Dimension) {
            // TODO: add some android integration tests for this behavior

            // is the view layout set to wrap content? if so we should just resize and not crop the image
            val lp = imageView.layoutParams
            if (lp.width == WRAP_CONTENT || lp.height == WRAP_CONTENT) {
                if (lp.width == WRAP_CONTENT && lp.height == WRAP_CONTENT) {
                    // Don't resize, let the view determine the size from the original image
                } else if (lp.width == WRAP_CONTENT && size.height > 0) {
                    update.resize(0, size.height).onlyScaleDown()
                } else if (lp.height == WRAP_CONTENT && size.width > 0) {
                    update.resize(size.width, 0).onlyScaleDown()
                }
                return
            }

            when (mView.scaleType) {
                ScaleType.CENTER_CROP ->
                    // centerCrop crops the image to the exact size specified by resize. So, if a dimension is 0 we
                    // can't crop the image anyways.
                    if (size.width > 0 && size.height > 0) {
                        update.resize(size.width, size.height)
                        update.onlyScaleDown()
                        update.centerCrop()
                    }
                ScaleType.CENTER_INSIDE, ScaleType.FIT_CENTER, ScaleType.FIT_START, ScaleType.FIT_END -> {
                    update.resize(size.width, size.height)
                    update.onlyScaleDown()
                    update.centerInside()
                }
                else -> update.transform(ScaleTransformation(size.width, size.height))
            }
        }

        fun asImageView() = imageView
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
