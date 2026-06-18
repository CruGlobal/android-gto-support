package org.ccci.gto.android.common.androidx.lifecycle

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.lifecycle.testing.TestLifecycleOwner
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.Before
import org.junit.Rule
import org.mockito.kotlin.mock

@OptIn(ExperimentalCoroutinesApi::class)
abstract class BaseLiveDataTest {
    protected val lifecycleOwner = TestLifecycleOwner(coroutineDispatcher = UnconfinedTestDispatcher())
    lateinit var observer: Observer<Any?>

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Before
    fun setupObserver() {
        observer = mock()
    }
}
