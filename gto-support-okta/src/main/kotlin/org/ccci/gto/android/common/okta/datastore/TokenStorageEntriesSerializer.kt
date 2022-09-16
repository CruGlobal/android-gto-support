package org.ccci.gto.android.common.okta.datastore

import androidx.datastore.core.Serializer
import com.okta.authfoundation.credential.TokenStorage
import java.io.InputStream
import java.io.OutputStream
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import org.ccci.gto.android.common.okta.authfoundation.credential.StoredEntries

@OptIn(ExperimentalSerializationApi::class)
object TokenStorageEntriesSerializer : Serializer<List<TokenStorage.Entry>> {
    private val json: Json = Json

    override val defaultValue: List<TokenStorage.Entry> = emptyList()

    override suspend fun readFrom(input: InputStream) =
        json.decodeFromStream<StoredEntries>(input).asTokenStorageEntries()

    override suspend fun writeTo(t: List<TokenStorage.Entry>, output: OutputStream) {
        json.encodeToStream(StoredEntries.from(t), output)
    }
}
