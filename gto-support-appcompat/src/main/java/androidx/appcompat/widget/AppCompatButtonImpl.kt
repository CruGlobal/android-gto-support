package androidx.appcompat.widget

import android.content.Context
import android.util.AttributeSet
import org.ccci.gto.android.common.appcompat.R
import org.ccci.gto.android.common.util.getDeclaredFieldOrNull

private val textHelperField by lazy { getDeclaredFieldOrNull<AppCompatButton>("mTextHelper") }

class AppCompatButtonImpl @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.buttonStyle
) : AppCompatButton(context, attrs, defStyleAttr) {
    private val compoundDrawableHelper: AppCompatCompoundDrawableHelper?

    init {
        compoundDrawableHelper = AppCompatCompoundDrawableHelper(this, textHelperField)
        compoundDrawableHelper.loadFromAttributes(attrs, defStyleAttr)
    }

    override fun drawableStateChanged() {
        super.drawableStateChanged()
        compoundDrawableHelper?.applyCompoundDrawablesTints()
    }
}
