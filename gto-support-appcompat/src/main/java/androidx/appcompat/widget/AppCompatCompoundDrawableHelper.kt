package androidx.appcompat.widget

import android.content.res.ColorStateList
import android.os.Build
import android.util.AttributeSet
import android.widget.TextView
import org.ccci.gto.android.common.appcompat.R
import org.ccci.gto.android.common.util.getDeclaredFieldOrNull
import org.ccci.gto.android.common.util.getOrNull
import timber.log.Timber
import java.lang.reflect.Field

private val startTintField by lazy { getDeclaredFieldOrNull<AppCompatTextHelper>("mDrawableStartTint") }
private val leftTintField by lazy { getDeclaredFieldOrNull<AppCompatTextHelper>("mDrawableLeftTint") }
private val topTintField by lazy { getDeclaredFieldOrNull<AppCompatTextHelper>("mDrawableTopTint") }
private val endTintField by lazy { getDeclaredFieldOrNull<AppCompatTextHelper>("mDrawableEndTint") }
private val rightTintField by lazy { getDeclaredFieldOrNull<AppCompatTextHelper>("mDrawableRightTint") }
private val bottomTintField by lazy { getDeclaredFieldOrNull<AppCompatTextHelper>("mDrawableBottomTint") }
private val tintFields by lazy {
    arrayOf(startTintField, leftTintField, topTintField, endTintField, rightTintField, bottomTintField).filterNotNull()
}

@SuppressWarnings("RestrictedApi")
internal class AppCompatCompoundDrawableHelper(private val textView: TextView, textHelperField: Field? = null) {
    private val textHelper = textHelperField?.getOrNull(textView) as? AppCompatTextHelper

    fun loadFromAttributes(attrs: AttributeSet?, defStyleAttr: Int) {
        val a = TintTypedArray.obtainStyledAttributes(
            textView.context, attrs, R.styleable.AppCompatCompoundDrawableHelper, defStyleAttr, 0
        )
        val drawableStart = a.getDrawable(R.styleable.AppCompatCompoundDrawableHelper_drawableStart)
        val drawableLeft = a.getDrawable(R.styleable.AppCompatCompoundDrawableHelper_drawableLeft)
        val drawableTop = a.getDrawable(R.styleable.AppCompatCompoundDrawableHelper_drawableTop)
        val drawableEnd = a.getDrawable(R.styleable.AppCompatCompoundDrawableHelper_drawableEnd)
        val drawableRight = a.getDrawable(R.styleable.AppCompatCompoundDrawableHelper_drawableRight)
        val drawableBottom = a.getDrawable(R.styleable.AppCompatCompoundDrawableHelper_drawableBottom)

        // We can only choose absolute positioned drawables or relative positioned drawables, but not both
        if (drawableStart != null || drawableEnd != null) {
            // prefer relative drawables if defined
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                textView.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    drawableStart, drawableTop, drawableEnd, drawableBottom
                )
            } else {
                // no RTL support on older devices anyways, just default to left/right for start/end
                textView.setCompoundDrawablesWithIntrinsicBounds(
                    drawableStart, drawableTop, drawableEnd, drawableBottom
                )
            }
        } else if (drawableLeft != null || drawableRight != null || drawableTop != null || drawableBottom != null) {
            // fallback to absolute drawables if defined
            textView.setCompoundDrawablesWithIntrinsicBounds(drawableLeft, drawableTop, drawableRight, drawableBottom)
        }

        // handle compound drawable tint
        if (a.hasValue(R.styleable.AppCompatCompoundDrawableHelper_drawableTint)) {
            setSupportCompoundDrawableTintList(
                a.getColorStateList(R.styleable.AppCompatCompoundDrawableHelper_drawableTint)
            )
        }
        a.recycle()
    }

    private fun setSupportCompoundDrawableTintList(tintList: ColorStateList?) {
        if (textHelper == null) return

        tintFields.forEach { field ->
            try {
                val tintInfo = field[textHelper] as? TintInfo ?: TintInfo().also { field[textHelper] = it }
                tintInfo.mTintList = tintList
                tintInfo.mHasTintList = true
            } catch (e: Exception) {
                Timber.tag("AppCmptCmpndDrwbleHlpr").e(e, "Error updating compound drawable tint")
            }
        }
        applyCompoundDrawablesTints()
    }

    fun applyCompoundDrawablesTints() {
        textHelper?.applyCompoundDrawablesTints()
    }
}
