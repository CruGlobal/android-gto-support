package com.tinder.scarlet

import org.ccci.gto.android.common.util.getDeclaredFieldOrNull
import org.ccci.gto.android.common.util.getOrNull

private val platformField = getDeclaredFieldOrNull<Scarlet.Builder>("platform")

internal var Scarlet.Builder.platform: Any?
    get() = platformField?.getOrNull(this)
    set(value) {
        platformField?.set(this, requireNotNull(value))
    }
