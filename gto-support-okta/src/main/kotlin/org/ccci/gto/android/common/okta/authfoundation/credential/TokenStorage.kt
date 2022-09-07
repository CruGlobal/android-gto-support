package org.ccci.gto.android.common.okta.authfoundation.credential

import com.okta.authfoundation.credential.TokenStorage

suspend fun TokenStorage.migrateTo(target: TokenStorage) = entries().forEach { entry ->
    if (target.entries().none { it.identifier == entry.identifier }) target.add(entry.identifier)
    target.replace(entry)
    remove(entry.identifier)
}
