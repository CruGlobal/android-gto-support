package org.ccci.gto.android.common.okta.authfoundation.credential

import com.okta.authfoundation.credential.TokenStorage
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class MigrationTokenStorage(
    private val storage: TokenStorage,
    private val originalStorage: TokenStorage,
) : TokenStorage, ChangeAwareTokenStorage, TokenStorageObserverRegistry by DefaultTokenStorageObserverRegistry() {
    private val migrationMutex = Mutex()
    private var isMigrated = false

    private suspend fun migrateStorage() {
        if (isMigrated) return

        migrationMutex.withLock {
            if (isMigrated) return@withLock
            originalStorage.migrateTo(storage)
            isMigrated = true
        }
    }

    override suspend fun add(id: String) {
        migrateStorage()
        storage.add(id)
        notifyChanged(id)
    }

    override suspend fun entries(): List<TokenStorage.Entry> {
        migrateStorage()
        return storage.entries()
    }

    override suspend fun remove(id: String) {
        migrateStorage()
        storage.remove(id)
        notifyChanged(id)
    }

    override suspend fun replace(updatedEntry: TokenStorage.Entry) {
        migrateStorage()
        storage.replace(updatedEntry)
        notifyChanged(updatedEntry.identifier)
    }
}
