package org.ccci.gto.android.common.dagger.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.reset
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import javax.inject.Provider
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.instanceOf
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class DaggerViewModelFactoryTest {
    lateinit var app: Application
    lateinit var factory: DaggerViewModelFactory
    lateinit var provider: Provider<ViewModel>

    @Before
    fun setup() {
        app = mock()
        provider = mock()

        factory = DaggerViewModelFactory(app, mapOf(DaggerViewModel::class.java to provider))
    }

    @Test
    fun testCreate() {
        whenever(provider.get()).thenReturn(DaggerViewModel(1))

        with(factory.create(DaggerViewModel::class.java)) {
            assertNotNull(this)
            assertEquals(1, a)
            verify(provider).get()
            reset(provider)
        }

        with(factory.create(SimpleAndroidViewModel::class.java)) {
            assertThat(this, instanceOf(SimpleAndroidViewModel::class.java))
            assertNotNull(getApplication())
            verify(provider, never()).get()
        }

        assertThat(factory.create(SimpleViewModel::class.java), instanceOf(SimpleViewModel::class.java))
        verify(provider, never()).get()
    }

    @Test
    fun testCreateOrNull() {
        whenever(provider.get()).thenReturn(DaggerViewModel(1))

        with(factory.createOrNull(DaggerViewModel::class.java)) {
            assertNotNull(this)
            assertEquals(1, this!!.a)
            verify(provider).get()
            reset(provider)
        }

        assertNull(factory.createOrNull(SimpleAndroidViewModel::class.java))
        assertNull(factory.createOrNull(SimpleViewModel::class.java))
    }

    class SimpleViewModel : ViewModel()
    class SimpleAndroidViewModel(application: Application) : AndroidViewModel(application)
    class DaggerViewModel(val a: Int) : ViewModel()
}
