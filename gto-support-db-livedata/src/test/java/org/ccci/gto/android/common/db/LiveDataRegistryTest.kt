package org.ccci.gto.android.common.db

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import java.util.concurrent.atomic.AtomicBoolean
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.yield
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class LiveDataRegistryTest {
    @get:Rule
    val instantTaskRule = InstantTaskExecutorRule()

    private val registry = LiveDataRegistry()
    lateinit var dao: LiveDataDao

    @Before
    fun setup() {
        dao = mock()
        whenever(dao.liveDataRegistry).thenReturn(registry)
        whenever(dao.findLiveData(Obj::class.java)).thenCallRealMethod()
    }

    @Test
    fun verifyinvalidateNoConcurrentModification() {
        // create 2 racing threads, 1 invalidating LiveData handles, and 1 registering new LiveData handles
        runBlocking {
            val shutdown = AtomicBoolean(false)
            launch(Dispatchers.Default) {
                while (!shutdown.get()) {
                    registry.invalidate(Obj::class.java)
                    yield()
                }
            }

            (0..1000).map { dao.findLiveData(Obj::class.java) }
            shutdown.set(true)
        }
    }
}

class Obj
