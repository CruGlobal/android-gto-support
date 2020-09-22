package org.ccci.gto.android.common.dagger.splitinstall

import android.content.Context
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.Reusable
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.multibindings.IntoSet
import dagger.multibindings.Multibinds
import org.ccci.gto.android.common.dagger.FirstNonNullCachingProvider
import org.ccci.gto.android.common.dagger.eager.EagerSingleton
import org.ccci.gto.android.common.dagger.eager.EagerSingleton.ThreadMode
import org.ccci.gto.android.common.dagger.splitinstall.eager.SplitInstallEagerSingletonInitializer

@Module
abstract class SplitInstallModule {
    @Binds
    @IntoSet
    @EagerSingleton(threadMode = ThreadMode.MAIN_ASYNC)
    abstract fun splitInstallEagerSingletonInitializer(service: SplitInstallEagerSingletonInitializer): Any

    @Multibinds
    abstract fun splitInstallComponents(): Map<String, FirstNonNullCachingProvider<SplitInstallComponent>>

    companion object {
        @Provides
        @Reusable
        fun provideSplitInstallManager(@ApplicationContext context: Context) =
            SplitInstallManagerFactory.create(context)
    }
}
