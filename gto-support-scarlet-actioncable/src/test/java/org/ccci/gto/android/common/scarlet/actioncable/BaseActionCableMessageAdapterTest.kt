package org.ccci.gto.android.common.scarlet.actioncable

import com.tinder.scarlet.MessageAdapter
import org.ccci.gto.android.common.scarlet.actioncable.model.Message
import org.junit.Before
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

abstract class BaseActionCableMessageAdapterTest {
    protected lateinit var factory: MessageAdapter.Factory

    protected lateinit var dataMessageAdapterFactory: MessageAdapter.Factory
    protected lateinit var dataMessageAdapter: MessageAdapter<Any>

    protected val actionCableAnnotation by lazy {
        Annotations::class.java.getDeclaredMethod("actionCable").annotations.actionCableMessage!!
    }
    protected val dataAsJson by lazy {
        Annotations::class.java.getDeclaredMethod("dataAsJson").annotations.actionCableSerializeDataAsJson!!
    }

    @Before
    fun setupFactory() {
        dataMessageAdapter = mock()
        dataMessageAdapterFactory = mock()
        whenever(dataMessageAdapterFactory.create(any(), any())).thenReturn(dataMessageAdapter)

        factory = ActionCableMessageAdapterFactory.Builder()
            .addMessageAdapterFactory(dataMessageAdapterFactory)
            .build()
    }

    protected interface MessageParameterizedTypes {
        fun notParameterized(): Message<*>
        fun charSequenceParameterized(): Message<CharSequence>
    }

    protected inline fun <reified T : Any> genericReturnTypeOf(method: String) =
        T::class.java.getDeclaredMethod(method).genericReturnType
}

private interface Annotations {
    @ActionCableMessage("valid")
    fun actionCable()

    @ActionCableSerializeDataAsJson
    fun dataAsJson()
}
