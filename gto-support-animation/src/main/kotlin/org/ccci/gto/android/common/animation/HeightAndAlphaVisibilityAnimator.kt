package org.ccci.gto.android.common.animation

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.TimeInterpolator
import android.util.Property
import android.view.View
import androidx.annotation.UiThread
import androidx.core.animation.addListener

class HeightAndAlphaVisibilityAnimator private constructor(private val view: View, private var isVisible: Boolean) {
    init {
        view.setTag(R.id.gto_height_alpha_animator, this)
    }

    var interpolator: TimeInterpolator? = null
    var duration: Long? = null

    private var current: Animator? = null

    @Suppress("ktlint:standard:property-naming") // https://github.com/pinterest/ktlint/issues/2448
    private var _originalHeight: Int? = null
    private val originalHeight get() = _originalHeight ?: HEIGHT.get(view)

    @Suppress("ktlint:standard:property-naming") // https://github.com/pinterest/ktlint/issues/2448
    private var _originalAlpha: Float? = null
    private val originalAlpha get() = _originalAlpha ?: View.ALPHA.get(view)

    @UiThread
    fun show() {
        if (isVisible) return
        startAnimator(createShowAnimator())
        isVisible = true
    }

    @UiThread
    fun hide() {
        if (!isVisible) return
        startAnimator(createHideAnimator())
        isVisible = false
    }

    private fun startAnimator(animator: Animator) {
        duration?.let { animator.duration = it }
        interpolator?.let { animator.interpolator = it }
        animator.addListener(
            onStart = {
                captureOriginalState()
                current = animator
            },
            onEnd = {
                restoreOriginalState()
                current = null
            }
        )
        current?.cancel()
        animator.start()
    }

    private fun createShowAnimator() = ObjectAnimator.ofPropertyValuesHolder(
        view,
        PropertyValuesHolder.ofInt(HEIGHT, view.height, view.calculateFullHeight()),
        PropertyValuesHolder.ofFloat(View.ALPHA, if (current != null) view.alpha else 0F, originalAlpha)
    ).apply { addListener(onStart = { view.visibility = View.VISIBLE }) }

    private fun createHideAnimator() = ObjectAnimator.ofPropertyValuesHolder(
        view,
        PropertyValuesHolder.ofInt(HEIGHT, view.height, 0),
        PropertyValuesHolder.ofFloat(View.ALPHA, view.alpha, 0F)
    ).apply { addListener(onEnd = { view.visibility = View.GONE }) }

    private fun captureOriginalState() {
        if (current == null) {
            _originalHeight = HEIGHT.get(view)
            _originalAlpha = View.ALPHA.get(view)
        }
    }

    private fun restoreOriginalState() {
        View.ALPHA.set(view, originalAlpha)
        HEIGHT.set(view, originalHeight)
    }

    private fun View.calculateFullHeight(): Int {
        val width = (parent as View).let { it.width - it.paddingLeft - it.paddingRight }
        val widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY)
        val heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        measure(widthMeasureSpec, heightMeasureSpec)
        return measuredHeight
    }

    companion object {
        fun of(
            view: View,
            isVisible: Boolean = view.visibility != View.GONE,
            onInit: (HeightAndAlphaVisibilityAnimator.() -> Unit)? = null,
        ) = view.getTag(R.id.gto_height_alpha_animator) as? HeightAndAlphaVisibilityAnimator
            ?: HeightAndAlphaVisibilityAnimator(view, isVisible).also { onInit?.invoke(it) }

        private val HEIGHT = object : Property<View, Int>(Int::class.java, "height") {
            override fun get(view: View) = view.layoutParams.height
            override fun set(view: View, value: Int) {
                view.layoutParams = view.layoutParams.apply { height = value }
            }
        }
    }
}
