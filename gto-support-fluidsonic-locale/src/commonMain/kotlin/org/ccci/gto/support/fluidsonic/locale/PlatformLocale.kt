package org.ccci.gto.support.fluidsonic.locale

import io.fluidsonic.locale.Locale

expect class PlatformLocale
expect fun PlatformLocale.toCommon(): Locale
expect fun Locale.toPlatform(): PlatformLocale
