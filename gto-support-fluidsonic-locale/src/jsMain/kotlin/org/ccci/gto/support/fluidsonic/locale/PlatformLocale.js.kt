package org.ccci.gto.support.fluidsonic.locale

import io.fluidsonic.locale.Locale

actual typealias PlatformLocale = String
actual fun Locale.toPlatform() = toString()
actual fun PlatformLocale.toCommon() = Locale.forLanguageTag(this)
