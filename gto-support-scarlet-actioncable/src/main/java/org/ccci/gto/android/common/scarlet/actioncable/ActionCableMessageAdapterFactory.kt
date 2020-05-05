package org.ccci.gto.android.common.scarlet.actioncable

import com.squareup.moshi.Moshi
import com.tinder.scarlet.MessageAdapter
import com.tinder.scarlet.messageadapter.moshi.MoshiMessageAdapter
import com.tinder.scarlet.utils.getRawType
import org.ccci.gto.android.common.moshi.adapter.StringifyJsonAdapterFactory
import org.ccci.gto.android.common.scarlet.actioncable.model.Subscribe
import java.lang.reflect.Type

class ActionCableMessageAdapterFactory : MessageAdapter.Factory {
    private val moshi = Moshi.Builder().add(StringifyJsonAdapterFactory).build()
    private val moshiMessageAdapterFactory = MoshiMessageAdapter.Factory(moshi)

    override fun create(type: Type, annotations: Array<Annotation>): MessageAdapter<*> = when (type.getRawType()) {
        Subscribe::class.java -> moshiMessageAdapterFactory.create(type, annotations)
        else -> throw IllegalArgumentException("Type is not supported by this MessageAdapterFactory: $type")
    }
}
