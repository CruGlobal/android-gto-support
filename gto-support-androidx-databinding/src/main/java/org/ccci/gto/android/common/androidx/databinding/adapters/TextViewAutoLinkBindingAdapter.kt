package org.ccci.gto.android.common.androidx.databinding.adapters

import android.annotation.SuppressLint
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.URLSpan
import android.view.View
import android.widget.TextView
import androidx.core.text.util.LinkifyCompat
import androidx.databinding.BindingAdapter
import androidx.databinding.adapters.TextViewBindingAdapter

// XXX: This doesn't currently work due to this bug:
//      https://issuetracker.google.com/issues/150626920
@SuppressLint("RestrictedApi")
@BindingAdapter("android:text", "android:autoLink", "onAutoLinkClicked")
fun TextView.setAutoLinkText(text: CharSequence?, autoLinkMask: Int?, autoLinkCallback: AutoLinkClickedListener?) {
    val output = SpannableString(text ?: "")
    val hasLinks = LinkifyCompat.addLinks(output, autoLinkMask ?: 0)

    if (hasLinks) {
        // upgrade all URLSpans to InterceptingUrlSpans if we have a callback
        if (autoLinkCallback != null) {
            output.getSpans(0, output.length, URLSpan::class.java).forEach { old ->
                val span = InterceptingUrlSpan(old, autoLinkCallback)
                output.setSpan(span, output.getSpanStart(old), output.getSpanEnd(old), output.getSpanFlags(old))
                output.removeSpan(old)
            }
        }
    }

    TextViewBindingAdapter.setText(this, output)
    if (hasLinks && linksClickable) movementMethod = LinkMovementMethod.getInstance()
}

private class InterceptingUrlSpan(span: URLSpan, private val autoLinkCallback: AutoLinkClickedListener) :
    URLSpan(span.url) {
    override fun onClick(widget: View) {
        autoLinkCallback.onAutoLinkClicked(widget, url)
        super.onClick(widget)
    }
}

interface AutoLinkClickedListener {
    fun onAutoLinkClicked(view: View, url: String)
}

// HACK: Workaround databinding bug with requireAll=true.
//       https://issuetracker.google.com/issues/150626920
@BindingAdapter("autoLinkText", "autoLinkMask", "onAutoLinkClicked", requireAll = false)
fun TextView.setAutoLinkTextHack(text: CharSequence?, autoLinkMask: Int?, autoLinkCallback: AutoLinkClickedListener?) =
    setAutoLinkText(text, autoLinkMask, autoLinkCallback)
