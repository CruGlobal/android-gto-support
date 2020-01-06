package androidx.appcompat.widget

import android.content.Context
import android.util.AttributeSet
import org.ccci.gto.android.common.util.getDeclaredFieldOrNull

private val textHelperField by lazy { getDeclaredFieldOrNull<AppCompatTextView>("mTextHelper") }

class AppCompatTextViewImpl @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = android.R.attr.textViewStyle
) : AppCompatTextView(context, attrs, defStyleAttr) {
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
