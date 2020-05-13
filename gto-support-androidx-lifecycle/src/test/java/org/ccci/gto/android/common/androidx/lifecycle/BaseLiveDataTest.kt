package org.ccci.gto.android.common.androidx.lifecycle

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.nhaarman.mockitokotlin2.mock
import org.junit.Before
import org.junit.Rule

abstract class BaseLiveDataTest {
    lateinit var observer: Observer<Any?>

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Before
    fun setupObserver() {
        observer = mock()
    }
}
