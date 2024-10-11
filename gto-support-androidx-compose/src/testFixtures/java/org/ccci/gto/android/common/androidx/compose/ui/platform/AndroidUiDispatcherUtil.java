package org.ccci.gto.android.common.androidx.compose.ui.platform;

public class AndroidUiDispatcherUtil {
    /**
     * This method is provided to workaround a bug with Kotlin Test Fixtures not being accessible via an external aar
     * dependency.
     */
    public static void runScheduledDispatches() {
        AndroidUiDispatcher_TestFixturesKt.clearAndroidUiDispatcher();
    }
}
