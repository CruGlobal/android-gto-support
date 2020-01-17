package androidx.appcompat.widget

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import androidx.annotation.RequiresApi
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.widget.TintableCompoundDrawablesView
import org.ccci.gto.android.common.appcompat.R
import org.ccci.gto.android.common.util.getDeclaredFieldOrNull

private val textHelperField by lazy { getDeclaredFieldOrNull<AppCompatButton>("mTextHelper") }

class AppCompatButtonImpl @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.buttonStyle
) : AppCompatButton(context, attrs, defStyleAttr), TintableCompoundDrawablesView {
    private val textHelper: AppCompatTextHelper? get() = textHelperField?.get(this) as? AppCompatTextHelper

    // region TintableCompoundDrawablesView
    override fun setCompoundDrawables(left: Drawable?, top: Drawable?, right: Drawable?, bottom: Drawable?) {
        super.setCompoundDrawables(left, top, right, bottom)
        textHelper?.onSetCompoundDrawables()
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun setCompoundDrawablesRelative(start: Drawable?, top: Drawable?, end: Drawable?, bottom: Drawable?) {
        super.setCompoundDrawablesRelative(start, top, end, bottom)
        textHelper?.onSetCompoundDrawables()
    }

    override fun setCompoundDrawablesWithIntrinsicBounds(
        left: Drawable?,
        top: Drawable?,
        right: Drawable?,
        bottom: Drawable?
    ) {
        super.setCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom)
        textHelper?.onSetCompoundDrawables()
    }

    override fun setCompoundDrawablesWithIntrinsicBounds(left: Int, top: Int, right: Int, bottom: Int) {
        val context = context
        setCompoundDrawablesWithIntrinsicBounds(
            if (left != 0) AppCompatResources.getDrawable(context, left) else null,
            if (top != 0) AppCompatResources.getDrawable(context, top) else null,
            if (right != 0) AppCompatResources.getDrawable(context, right) else null,
            if (bottom != 0) AppCompatResources.getDrawable(context, bottom) else null
        )
        textHelper?.onSetCompoundDrawables()
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun setCompoundDrawablesRelativeWithIntrinsicBounds(
        start: Drawable?,
        top: Drawable?,
        end: Drawable?,
        bottom: Drawable?
    ) {
        super.setCompoundDrawablesRelativeWithIntrinsicBounds(start, top, end, bottom)
        textHelper?.onSetCompoundDrawables()
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun setCompoundDrawablesRelativeWithIntrinsicBounds(start: Int, top: Int, end: Int, bottom: Int) {
        val context = context
        setCompoundDrawablesRelativeWithIntrinsicBounds(
            if (start != 0) AppCompatResources.getDrawable(context, start) else null,
            if (top != 0) AppCompatResources.getDrawable(context, top) else null,
            if (end != 0) AppCompatResources.getDrawable(context, end) else null,
            if (bottom != 0) AppCompatResources.getDrawable(context, bottom) else null
        )
        textHelper?.onSetCompoundDrawables()
    }

    override fun getSupportCompoundDrawablesTintMode() = textHelper?.compoundDrawableTintMode
    override fun getSupportCompoundDrawablesTintList() = textHelper?.compoundDrawableTintList

    override fun setSupportCompoundDrawablesTintList(tintList: ColorStateList?) {
        textHelper?.apply {
            compoundDrawableTintList = tintList
            applyCompoundDrawablesTints()
        }
    }

    override fun setSupportCompoundDrawablesTintMode(tintMode: PorterDuff.Mode?) {
        textHelper?.apply {
            compoundDrawableTintMode = tintMode
            applyCompoundDrawablesTints()
        }
    }
    // endregion TintableCompoundDrawablesView
}
