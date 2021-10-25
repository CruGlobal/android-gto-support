package org.ccci.gto.android.common.util.widget

import android.graphics.Typeface
import android.widget.TextView

val TextView.typefaceStyle get() = typeface?.style ?: Typeface.NORMAL
