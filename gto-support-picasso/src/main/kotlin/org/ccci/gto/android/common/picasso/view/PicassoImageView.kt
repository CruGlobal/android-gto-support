package org.ccci.gto.android.common.picasso.view

import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.AttributeSet
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageView
import android.widget.ImageView.ScaleType
import androidx.annotation.DrawableRes
import androidx.annotation.UiThread
import androidx.annotation.VisibleForTesting
import androidx.core.content.withStyledAttributes
import com.squareup.picasso.Picasso
import com.squareup.picasso.RequestCreator
import com.squareup.picasso.Transformation
import java.io.File
import org.ccci.gto.android.common.base.Constants.INVALID_DRAWABLE_RES
import org.ccci.gto.android.common.base.model.Dimension
import org.ccci.gto.android.common.picasso.R
import org.ccci.gto.android.common.picasso.transformation.ScaleTransformation

interface PicassoImageView {
    open class Helper(
        protected val imageView: ImageView,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
        defStyleRes: Int = 0,
        picasso: Picasso? = null,
    ) {
        private val picasso by lazy { picasso ?: Picasso.get() }

        // region Image Source
        private var picassoFile: File? = null
        private var picassoUri: Uri? = null

        fun setPicassoFile(file: File?) {
            val changing = file != picassoFile
            if (file != null) picassoUri = null
            picassoFile = file
            if (changing) triggerUpdate()
        }

        fun setPicassoUri(uri: Uri?) {
            val changing = uri != picassoUri
            if (uri != null) picassoFile = null
            picassoUri = uri
            if (changing) triggerUpdate()
        }
        // endregion Image Source

        // region Placeholder Image
        @DrawableRes
        @VisibleForTesting
        internal var placeholderResId = INVALID_DRAWABLE_RES
        @VisibleForTesting
        internal var placeholder: Drawable? = null

        init {
            imageView.context.withStyledAttributes(attrs, R.styleable.PicassoImageView, defStyleAttr, defStyleRes) {
                placeholderResId = getResourceId(R.styleable.PicassoImageView_placeholder, placeholderResId)

                val file = getString(R.styleable.PicassoImageView_picassoFile)
                val uri = getString(R.styleable.PicassoImageView_picassoUri)
                check(file == null || uri == null) { "Cannot have both app:picassoFile and app:picassoUri defined" }
                picassoFile = file?.let { File(file) }
                picassoUri = uri?.let { Uri.parse(uri) }
            }
        }

        @UiThread
        fun setPlaceholder(@DrawableRes resId: Int) {
            val changing = resId != placeholderResId || placeholder != null
            placeholder = null
            placeholderResId = resId
            if (changing) triggerUpdate()
        }

        @UiThread
        fun setPlaceholder(image: Drawable?) {
            val changing = image !== placeholder || placeholderResId != INVALID_DRAWABLE_RES
            placeholderResId = INVALID_DRAWABLE_RES
            placeholder = image
            if (changing) triggerUpdate()
        }
        // endregion Placeholder Image

        // region Transformations
        private val transforms = mutableListOf<Transformation>()

        @UiThread
        fun addTransform(transformation: Transformation) {
            transforms.add(transformation)
            triggerUpdate()
        }

        @UiThread
        fun setTransforms(transformations: List<Transformation>?) {
            // short-circuit if we aren't actually changing any transformations
            if (transforms.isEmpty() && transformations.isNullOrEmpty()) return

            transforms.clear()
            if (transformations != null) transforms.addAll(transformations)
            triggerUpdate()
        }
        // endregion Transformations

        @UiThread
        fun onSetScaleType() = triggerUpdate()

        private var size = Dimension(0, 0)
        @UiThread
        fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
            if (oldw != w || oldh != h) {
                size = Dimension(w, h)
                // onSizeChanged() is called during layout, so we need to defer until after layout is complete
                postTriggerUpdate()
            }
        }

        // region triggerUpdate() logic
        private var needsUpdate = false
        private var batching = 0

        @UiThread
        fun toggleBatchUpdates(enable: Boolean) {
            if (enable) {
                batching++
            } else {
                batching--
                if (batching <= 0) {
                    batching = 0
                    if (needsUpdate) triggerUpdate()
                }
            }
        }

        @UiThread
        protected fun triggerUpdate() {
            // short-circuit if we are in edit mode within a development tool
            if (imageView.isInEditMode) return

            // if we are batching updates, track that we need an update, but don't trigger the update now
            if (batching > 0) {
                needsUpdate = true
                return
            }

            // if we are currently in a layout pass, trigger an update once layout is complete
            if (imageView.isInLayout) {
                postTriggerUpdate()
                return
            }

            // clear the needs update flag
            needsUpdate = false

            // build Picasso request
            val update = onCreateUpdate(picasso)
            placeholderResId.takeIf { it != INVALID_DRAWABLE_RES }?.let { update.placeholder(it) }
            placeholder?.let { update.placeholder(it) }
            if (size.width > 0 || size.height > 0) onSetUpdateScale(update, size)
            update.transform(transforms)

            // fetch or load based on the target size
            if (size.width > 0 || size.height > 0) update.into(imageView) else update.fetch()
        }

        @UiThread
        protected open fun onCreateUpdate(picasso: Picasso): RequestCreator =
            picassoFile?.let { picasso.load(it) } ?: picasso.load(picassoUri)

        @UiThread
        protected open fun onSetUpdateScale(update: RequestCreator, size: Dimension) {
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

            when (imageView.scaleType) {
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

        private fun postTriggerUpdate() {
            needsUpdate = true
            imageView.post { if (needsUpdate) triggerUpdate() }
        }
        // endregion triggerUpdate() logic
    }

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
    fun setTransforms(transforms: List<Transformation>?)

    @UiThread
    fun toggleBatchUpdates(enable: Boolean)

    /**
     * @return The ImageView this PicassoImageView represents.
     */
    fun asImageView() = this as ImageView
}
