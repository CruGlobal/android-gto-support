package org.ccci.gto.android.common.scarlet.actioncable.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Identifier(val channel: String)
