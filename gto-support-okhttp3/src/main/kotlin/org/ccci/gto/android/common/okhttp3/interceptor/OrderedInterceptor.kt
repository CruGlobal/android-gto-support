package org.ccci.gto.android.common.okhttp3.interceptor

import okhttp3.Interceptor
import org.ccci.gto.android.common.base.Ordered

internal class OrderedInterceptor(delegate: Interceptor, override val order: Int) : Interceptor by delegate, Ordered

fun Interceptor.orderBy(order: Int): Interceptor = OrderedInterceptor(this, order)
