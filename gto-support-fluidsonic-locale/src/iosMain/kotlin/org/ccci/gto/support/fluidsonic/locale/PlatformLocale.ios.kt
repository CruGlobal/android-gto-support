package org.ccci.gto.support.fluidsonic.locale

import io.fluidsonic.locale.Locale
import io.fluidsonic.locale.toCommon
import io.fluidsonic.locale.toPlatform
import platform.Foundation.NSLocale

actual typealias PlatformLocale = NSLocale
actual fun Locale.toPlatform() = toPlatform()
actual fun PlatformLocale.toCommon() = toCommon()
