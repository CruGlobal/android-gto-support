package org.ccci.gto.android.common.androidx.collection

import android.util.LruCache

operator fun <K : Any, V : Any> LruCache<K, V>.get(key: K) = get(key)
operator fun <K : Any, V : Any> LruCache<K, V>.set(key: K, value: V) = put(key, value)
