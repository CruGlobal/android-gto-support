package org.ccci.gto.android.common.androidx.compose.ui.platform

import androidx.compose.ui.platform.AndroidUiDispatcher
import kotlin.test.assertFalse
import kotlinx.coroutines.CoroutineDispatcher

// HACK: Workaround for https://github.com/robolectric/robolectric/issues/7055#issuecomment-1551119229
@OptIn(ExperimentalStdlibApi::class)
fun clearAndroidUiDispatcher() {
    val androidUiDispatcher = AndroidUiDispatcher.Main[CoroutineDispatcher.Key] as AndroidUiDispatcher

    val dispatchCallback by lazy {
        androidUiDispatcher.javaClass.getDeclaredField("dispatchCallback")
            .apply { isAccessible = true }
            .get(androidUiDispatcher) as Runnable
    }

    var scheduledFrameDispatch = false
    var scheduledTrampolineDispatch = false
    for (i in 0 until 5) {
        scheduledFrameDispatch = androidUiDispatcher.javaClass.getDeclaredField("scheduledFrameDispatch")
            .apply { isAccessible = true }
            .getBoolean(androidUiDispatcher)
        scheduledTrampolineDispatch = androidUiDispatcher.javaClass.getDeclaredField("scheduledTrampolineDispatch")
            .apply { isAccessible = true }
            .getBoolean(androidUiDispatcher)
        if (!scheduledFrameDispatch && !scheduledTrampolineDispatch) break

        dispatchCallback.run()
    }

    assertFalse(scheduledFrameDispatch)
    assertFalse(scheduledTrampolineDispatch)
}
