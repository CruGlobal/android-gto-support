package org.ccci.gto.android.common.androidx.collection

import androidx.collection.LruCache

operator fun <K : Any, V : Any> LruCache<K, V>.set(key: K, value: V) = put(key, value)

fun <K : Any, V : Any> LruCache<K, V>.getOrPut(key: K, create: (K) -> V) = get(key) ?: create(key).also { put(key, it) }
