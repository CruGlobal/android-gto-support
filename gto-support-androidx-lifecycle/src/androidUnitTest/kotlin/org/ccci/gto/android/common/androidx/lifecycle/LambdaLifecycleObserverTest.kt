package org.ccci.gto.android.common.androidx.lifecycle

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.testing.TestLifecycleOwner
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify

class LambdaLifecycleObserverTest {
    @OptIn(ExperimentalCoroutinesApi::class)
    private val lifecycleOwner = TestLifecycleOwner(coroutineDispatcher = UnconfinedTestDispatcher())
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
