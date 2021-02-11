package org.ccci.gto.android.common.androidx.lifecycle

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.testing.TestLifecycleOwner
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import org.junit.Before
import org.junit.Test

class LambdaLifecycleObserverTest {
    @OptIn(ExperimentalCoroutinesApi::class)
    private val lifecycleOwner = TestLifecycleOwner(coroutineDispatcher = TestCoroutineDispatcher())
    private lateinit var lambda: (LifecycleOwner) -> Unit

    @Before
    fun setup() {
        lambda = mock()
    }

    @Test
    fun testOnDestroy() {
        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        lifecycleOwner.lifecycle.onDestroy(lambda)

        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
        verify(lambda, never()).invoke(any())
        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        verify(lambda).invoke(eq(lifecycleOwner))
    }
}
