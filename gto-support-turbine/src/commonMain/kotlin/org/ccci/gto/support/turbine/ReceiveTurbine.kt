package org.ccci.gto.support.turbine

import app.cash.turbine.ReceiveTurbine

suspend fun <T : Any?> ReceiveTurbine<T>.awaitItemMatching(predicate: (T) -> Boolean): T {
    var item: T
    do {
        item = awaitItem()
    } while (!predicate(item))
    return item
}
