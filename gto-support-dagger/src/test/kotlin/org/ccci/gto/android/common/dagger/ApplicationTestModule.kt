package org.ccci.gto.android.common.dagger

import android.app.Application
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext

@Module
object ApplicationTestModule {
    @Provides
    fun application() = ApplicationProvider.getApplicationContext<Application>()

    @Provides
    @ApplicationContext
    fun context(): Context = ApplicationProvider.getApplicationContext()
}
