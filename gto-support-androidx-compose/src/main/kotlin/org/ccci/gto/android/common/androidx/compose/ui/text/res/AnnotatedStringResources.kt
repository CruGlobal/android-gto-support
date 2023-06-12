package org.ccci.gto.android.common.androidx.compose.ui.text.res

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.core.os.ConfigurationCompat
import java.util.Formattable
import java.util.Formatter

@Composable
@ReadOnlyComposable
fun annotatedStringResource(@StringRes id: Int, vararg formatArgs: Any) = buildAnnotatedString {
    LocalConfiguration.current
    val args = formatArgs.map { if (it is AnnotatedString) AnnotatedStringFormattable(it) else it }
    Formatter(this, ConfigurationCompat.getLocales(LocalContext.current.resources.configuration)[0])
        .format(stringResource(id), *args.toTypedArray())
}

private class AnnotatedStringFormattable(private val text: AnnotatedString) : Formattable {
    override fun formatTo(formatter: Formatter, flags: Int, width: Int, precision: Int) {
        when (val out = formatter.out()) {
            is AnnotatedString.Builder -> out.append(text)
            else -> out.append(text)
        }
    }

    override fun toString() = text.toString()
}
