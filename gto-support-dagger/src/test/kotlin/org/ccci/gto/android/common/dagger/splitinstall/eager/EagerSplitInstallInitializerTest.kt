package org.ccci.gto.android.common.dagger.splitinstall.eager

import com.google.android.play.core.splitinstall.SplitInstallManager
import com.google.android.play.core.splitinstall.SplitInstallSessionState
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus
import org.ccci.gto.android.common.dagger.FirstNonNullCachingProvider
import org.ccci.gto.android.common.dagger.eager.EagerSingletonInitializer
import org.ccci.gto.android.common.dagger.splitinstall.SplitInstallComponent
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

private const val FEATURE1 = "feature1"
private const val FEATURE2 = "feature2"

class EagerSplitInstallInitializerTest {
    private lateinit var baseInitializer: EagerSingletonInitializer
    private lateinit var splitInstallManager: SplitInstallManager

    private lateinit var feature1: SplitInstallEagerSingletonInitializerProvider
    private lateinit var feature1Initializer: EagerSingletonInitializer
    private lateinit var feature1Provider: FirstNonNullCachingProvider<SplitInstallComponent>
    private lateinit var feature2Provider: FirstNonNullCachingProvider<SplitInstallComponent>

    @Before
    fun setup() {
        baseInitializer = mock {
            on { activityCreated = any() }.thenCallRealMethod()
            on { activityCreated }.thenCallRealMethod()
        }
        splitInstallManager = mock()

        feature1Initializer = mock()
        feature1 = mock { on { eagerSingletonInitializer() } doReturn feature1Initializer }
        feature1Provider = mock()
        feature2Provider = mock()
    }

    @Test
    fun verifyDontRegisterIfNoComponents() {
        val initializer = SplitInstallEagerSingletonInitializer(baseInitializer, splitInstallManager, emptyMap())

        verify(splitInstallManager, never()).registerListener(initializer)
        verify(splitInstallManager, never()).installedModules
    }

    @Test
    fun verifyInitializeAlreadyInstalledModules() {
        whenever(splitInstallManager.installedModules).thenReturn(setOf(FEATURE1, FEATURE2))
        whenever(feature1Provider.get()).thenReturn(feature1)
        val initializer = SplitInstallEagerSingletonInitializer(
            baseInitializer,
            splitInstallManager,
            mapOf(FEATURE1 to feature1Provider)
        )

        verify(splitInstallManager).registerListener(initializer)
        verify(feature1Provider).get()
        verify(feature1).eagerSingletonInitializer()
        verify(splitInstallManager).unregisterListener(initializer)
    }

    @Test
    fun verifyInitializeModulesThatAreInstalledLater() {
        whenever(feature1Provider.get()).thenReturn(feature1)
        val initializer = SplitInstallEagerSingletonInitializer(
            baseInitializer,
            splitInstallManager,
            mapOf(FEATURE1 to feature1Provider)
        )

        verify(splitInstallManager).registerListener(initializer)
        verify(feature1Provider, never()).get()
        verify(feature1, never()).eagerSingletonInitializer()
        verify(splitInstallManager, never()).unregisterListener(initializer)

        initializer.onStateUpdate(createState(modules = listOf(FEATURE1)))
        verify(feature1Provider).get()
        verify(feature1).eagerSingletonInitializer()
        verify(splitInstallManager).unregisterListener(initializer)
    }

    @Test
    fun verifyInitializeActivityCreatedSingletonsWhenActivityAlreadyCreated() {
        baseInitializer.activityCreated = true
        whenever(splitInstallManager.installedModules).thenReturn(setOf(FEATURE1))
        whenever(feature1Provider.get()).thenReturn(feature1)
        SplitInstallEagerSingletonInitializer(baseInitializer, splitInstallManager, mapOf(FEATURE1 to feature1Provider))

        verify(feature1Initializer).initializeActivityCreatedSingletons()
    }

    @Test
    fun verifyDontInitializeActivityCreatedSingletonsWhenActivityNotCreated() {
        baseInitializer.activityCreated = false
        whenever(splitInstallManager.installedModules).thenReturn(setOf(FEATURE1))
        whenever(feature1Provider.get()).thenReturn(feature1)
        SplitInstallEagerSingletonInitializer(baseInitializer, splitInstallManager, mapOf(FEATURE1 to feature1Provider))

        verify(feature1Initializer, never()).initializeActivityCreatedSingletons()
    }

    private fun createState(modules: List<String> = emptyList(), status: Int = SplitInstallSessionStatus.INSTALLED) =
        SplitInstallSessionState.create(0, status, 0, 0L, 0L, modules, emptyList())
}
