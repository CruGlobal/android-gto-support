package org.ccci.gto.android.common.androidx.core.app

import android.app.LocaleConfig
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.content.res.XmlResourceParser
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.os.LocaleListCompat
import java.io.IOException
import java.util.Locale
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import timber.log.Timber

private const val TAG = "LocaleConfigCompat"

private const val METADATA_LOCALE_CONFIG = "org.ccci.gto.android.common.androidx.core.LocaleConfig"

object LocaleConfigCompat {
    private val COMPAT = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> TiramisuLocaleConfigCompatMethods()
        else -> BaseLocaleConfigCompatMethods()
    }

    fun getSupportedLocales(context: Context) = COMPAT.getSupportedLocales(context)
}

private sealed interface LocaleConfigCompatMethods {
    fun getSupportedLocales(context: Context): LocaleListCompat?
}

private open class BaseLocaleConfigCompatMethods : LocaleConfigCompatMethods {
    private companion object {
        const val NS_ANDROID = "http://schemas.android.com/apk/res/android"
        const val TAG_LOCALE_CONFIG = "locale-config"
        const val TAG_LOCALE = "locale"
    }

    override fun getSupportedLocales(context: Context) = runCatching {
        val metaData =
            context.packageManager.getApplicationInfo(context.packageName, PackageManager.GET_META_DATA).metaData
        check(metaData.containsKey(METADATA_LOCALE_CONFIG)) { "meta-data '$METADATA_LOCALE_CONFIG' was not found." }
        val parser = context.resources.getXml(metaData.getInt(METADATA_LOCALE_CONFIG))
        parseLocaleConfig(parser)
    }.getOrElse {
        when (it) {
            is Resources.NotFoundException,
            is XmlPullParserException,
            is IOException -> {
                Timber.tag(TAG).e(it, "Error loading locales specified by '$METADATA_LOCALE_CONFIG' meta-data")
                null
            }
            else -> throw it
        }
    }

    private fun parseLocaleConfig(parser: XmlResourceParser): LocaleListCompat {
        while (parser.next() != XmlPullParser.START_TAG) {}
        parser.require(XmlPullParser.START_TAG, null, TAG_LOCALE_CONFIG)
        val locales = mutableListOf<Locale>()
        val depth = parser.depth
        while (parser.depth >= depth) {
            if (parser.next() == XmlPullParser.START_TAG && parser.name == TAG_LOCALE && parser.depth == depth + 1) {
                locales += Locale.forLanguageTag(parser.getAttributeValue(NS_ANDROID, "name"))
            }
        }
        return LocaleListCompat.create(*locales.toTypedArray())
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
private class TiramisuLocaleConfigCompatMethods : BaseLocaleConfigCompatMethods() {
    override fun getSupportedLocales(context: Context) =
        LocaleConfig(context).supportedLocales?.let { LocaleListCompat.wrap(it) }
}
