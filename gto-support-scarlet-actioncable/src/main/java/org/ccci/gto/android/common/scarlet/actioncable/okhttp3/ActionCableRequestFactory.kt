package org.ccci.gto.android.common.scarlet.actioncable.okhttp3

import com.tinder.scarlet.websocket.okhttp.request.RequestFactory
import okhttp3.Request
import org.ccci.gto.android.common.scarlet.actioncable.WEBSOCKET_PROTOCOL_ACTIONCABLE

class ActionCableRequestFactory(private val delegate: RequestFactory) : RequestFactory {
    constructor(url: String) : this(object : RequestFactory {
        override fun createRequest() = Request.Builder()
            .url(url)
            .build()
    })

    override fun createRequest() = delegate.createRequest().newBuilder()
        // Sec-WebSocket-Key is automatically added to the request by OkHttp3
        .header("Sec-WebSocket-Protocol", WEBSOCKET_PROTOCOL_ACTIONCABLE)
        .build()
}
