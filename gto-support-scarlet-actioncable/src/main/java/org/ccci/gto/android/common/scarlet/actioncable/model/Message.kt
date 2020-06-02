package org.ccci.gto.android.common.scarlet.actioncable.model

import org.ccci.gto.android.common.moshi.adapter.Stringify

class Message<T>(@Stringify val identifier: Identifier, val data: T) {
    constructor(channel: String, data: T) : this(Identifier(channel), data)
}
