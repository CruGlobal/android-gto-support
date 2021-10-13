package org.ccci.gto.android.common.androidx.lifecycle

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import org.junit.Before
import org.junit.Rule
import org.mockito.kotlin.mock

abstract class BaseLiveDataTest {
    lateinit var observer: Observer<Any?>

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Before
    fun setupObserver() {
        observer = mock()
    }
}
