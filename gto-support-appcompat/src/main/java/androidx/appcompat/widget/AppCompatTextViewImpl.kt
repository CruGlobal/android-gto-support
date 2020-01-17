package androidx.appcompat.widget

import android.content.Context
import android.util.AttributeSet

@Deprecated("Since v3.3.0, use AppCompatTextView directly")
class AppCompatTextViewImpl @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = android.R.attr.textViewStyle
) : AppCompatTextView(context, attrs, defStyleAttr)
