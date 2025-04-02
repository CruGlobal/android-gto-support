package org.ccci.gto.android.common.dagger.coroutines

import dagger.Module
import dagger.Provides
import dagger.Reusable
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.ccci.gto.android.common.dagger.coroutines.DispatcherType.Type.DEFAULT
import org.ccci.gto.android.common.dagger.coroutines.DispatcherType.Type.IO
import org.ccci.gto.android.common.dagger.coroutines.DispatcherType.Type.MAIN
import org.ccci.gto.android.common.dagger.coroutines.DispatcherType.Type.MAIN_IMMEDIATE
import org.ccci.gto.android.common.dagger.coroutines.DispatcherType.Type.UNCONFINED

@Module
object CoroutinesModule {
    @get:[Provides Reusable DispatcherType(MAIN)]
    val mainDispatcher: CoroutineDispatcher get() = Dispatchers.Main

    @get:[Provides Reusable DispatcherType(MAIN_IMMEDIATE)]
    val mainImmediateDispatcher: CoroutineDispatcher get() = Dispatchers.Main.immediate

    @get:[Provides Reusable DispatcherType(DEFAULT)]
    val defaultDispatcher: CoroutineDispatcher get() = Dispatchers.Default

    @get:[Provides Reusable DispatcherType(IO)]
    val ioDispatcher: CoroutineDispatcher get() = Dispatchers.IO

    @get:[Provides Reusable DispatcherType(UNCONFINED)]
    val unconfinedDispatcher: CoroutineDispatcher get() = Dispatchers.Unconfined
}
