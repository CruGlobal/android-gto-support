package org.ccci.gto.android.common.picasso.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.AttributeSet
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.annotation.UiThread
import com.squareup.picasso.Picasso
import com.squareup.picasso.RequestCreator
import com.squareup.picasso.Transformation
import java.io.File

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
        override fun onCreateUpdate(picasso: Picasso): RequestCreator = picassoFile?.let { picasso.load(it) }
            ?: picasso.load(picassoUri)

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
