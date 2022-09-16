package org.ccci.gto.android.common.okta.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.okta.authfoundation.credential.TokenStorage
import kotlinx.coroutines.flow.first
import org.ccci.gto.android.common.okta.authfoundation.credential.ChangeAwareTokenStorage
import org.ccci.gto.android.common.okta.authfoundation.credential.DefaultTokenStorageObserverRegistry
import org.ccci.gto.android.common.okta.authfoundation.credential.TokenStorageObserverRegistry

class DataStoreTokenStorage(private val dataStore: DataStore<List<TokenStorage.Entry>>) :
    ChangeAwareTokenStorage,
    TokenStorageObserverRegistry by DefaultTokenStorageObserverRegistry() {
    constructor(context: Context) : this(context.defaultTokenStorage)

    companion object {
        private const val FILE_NAME = "org.ccci.gto.android.common.okta.datastore.tokens.json"

        private val Context.defaultTokenStorage: DataStore<List<TokenStorage.Entry>> by dataStore(
            fileName = FILE_NAME,
            serializer = TokenStorageEntriesSerializer
        )
    }

    override suspend fun entries() = dataStore.data.first()

    override suspend fun add(id: String) {
        dataStore.updateData { entries ->
            require(entries.none { it.identifier == id }) { "Error adding a new Credential to TokenStorage" }
            entries + TokenStorage.Entry(id, null, emptyMap())
        }
        notifyChanged(id)
    }

    override suspend fun remove(id: String) {
        dataStore.updateData { entries -> entries.filter { it.identifier != id } }
        notifyChanged(id)
    }

    override suspend fun replace(updatedEntry: TokenStorage.Entry) {
        dataStore.updateData { entries ->
            entries.map {
                if (it.identifier == updatedEntry.identifier) updatedEntry
                else it
            }
        }
        notifyChanged(updatedEntry.identifier)
    }
}
