package androidx.appcompat.widget

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.R

@Deprecated("Since v3.11.0, Use AppCompatButton directly")
class AppCompatButtonImpl @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.buttonStyle,
) : AppCompatButton(context, attrs, defStyleAttr)
