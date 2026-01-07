package org.ccci.gto.android.common.okta.oidc.net

import android.net.Uri
import com.okta.oidc.net.ConnectionParameters
import com.okta.oidc.net.OktaHttpClient
import java.io.InputStream
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

class OkHttpOktaHttpClient(okhttp: OkHttpClient = OkHttpClient()) : OktaHttpClient {
    private val okhttp = okhttp.newBuilder()
        .followRedirects(false)
        .followSslRedirects(false)
        .build()

    private var currentResponse: Response? = null

    override fun connect(uri: Uri, param: ConnectionParameters): InputStream? {
        val request = Request.Builder()
            .url(uri.toString())
            .apply { param.requestProperties().forEach { addHeader(it.key, it.value) } }
            .apply {
                when (param.requestMethod()) {
                    ConnectionParameters.RequestMethod.GET -> get()

                    ConnectionParameters.RequestMethod.POST -> {
                        val body = FormBody.Builder()
                            .apply { param.postParameters().orEmpty().forEach { add(it.key, it.value) } }
                            .build()
                        post(body)
                    }

                    null -> error("requestMethod should never be null")
                }
            }
            .build()
        currentResponse = okhttp.newCall(request).execute()
        return currentResponse?.body?.byteStream()
    }

    override fun getResponseCode() = currentResponse?.code ?: -1
    override fun getResponseMessage() = currentResponse?.message
    override fun getHeaderFields() = currentResponse?.headers?.toMultimap()
    override fun getHeader(header: String) = currentResponse?.header(header)
    override fun getContentLength() = currentResponse?.header("Content-Length")?.toIntOrNull() ?: -1

    override fun cancel() {
        currentResponse?.body?.close()
    }

    override fun cleanUp() {
        currentResponse = null
    }
}
