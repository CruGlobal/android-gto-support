package org.ccci.gto.android.common.dagger.splitinstall.eager

import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.Component
import javax.inject.Singleton
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.ccci.gto.android.common.dagger.ApplicationTestModule
import org.ccci.gto.android.common.dagger.eager.EagerModule
import org.ccci.gto.android.common.dagger.eager.EagerSingletonInitializer
import org.ccci.gto.android.common.dagger.splitinstall.SplitInstallModule
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@OptIn(DelicateCoroutinesApi::class, ExperimentalCoroutinesApi::class)
class EagerSplitInstallInitializerWiringTest {
    private val mainThreadDispatcher = newSingleThreadContext("Main Thread")

    @Before
    fun setup() {
        Dispatchers.setMain(mainThreadDispatcher)
    }

    @After
    fun cleanup() {
        Dispatchers.resetMain()
        mainThreadDispatcher.close()
    }

    @Test
    fun verifyEagerSingletonInitializerNoInfiniteRecursion() {
        val component = DaggerEagerSplitInstallInitializerWiringTest_TestComponent.create()
        val initializer = component.eagerSingletonInitializer()
        initializer.job.complete()
        runBlocking { initializer.job.join() }
    }

    @Singleton
    @Component(modules = [SplitInstallModule::class, EagerModule::class, ApplicationTestModule::class])
    interface TestComponent {
        fun eagerSingletonInitializer(): EagerSingletonInitializer
    }
}
