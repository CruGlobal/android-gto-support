package org.ccci.gto.android.common.util;

import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class AsyncTaskCompat {
    private static final Compat COMPAT;
    static {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            COMPAT = new FroyoCompat();
        } else {
            COMPAT = new HoneycombCompat();
        }
    }

    public static void execute(@NonNull final Runnable task) {
        COMPAT.execute(task);
    }

    @NonNull
    public static Executor SERIAL_EXECUTOR() {
        return COMPAT.SERIAL_EXECUTOR();
    }

    interface Compat {
        void execute(@NonNull Runnable task);

        @NonNull
        Executor SERIAL_EXECUTOR();
    }

    static class FroyoCompat implements Compat {
        private static Executor EXECUTOR;

        @NonNull
        @TargetApi(Build.VERSION_CODES.GINGERBREAD)
        private static synchronized Executor getExecutor() {
            if (EXECUTOR == null) {
                EXECUTOR = Executors.newFixedThreadPool(1);
                if (EXECUTOR instanceof ThreadPoolExecutor) {
                    ((ThreadPoolExecutor) EXECUTOR).setKeepAliveTime(30, TimeUnit.SECONDS);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                        ((ThreadPoolExecutor) EXECUTOR).allowCoreThreadTimeOut(true);
                    } else {
                        ((ThreadPoolExecutor) EXECUTOR).setCorePoolSize(0);
                    }
                }
            }

            return EXECUTOR;
        }

        @Override
        public void execute(@NonNull final Runnable task) {
            getExecutor().execute(task);
        }

        @NonNull
        @Override
        public Executor SERIAL_EXECUTOR() {
            return getExecutor();
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    static class HoneycombCompat extends FroyoCompat {
        @Override
        public void execute(@NonNull Runnable task) {
            AsyncTask.execute(task);
        }

        @NonNull
        @Override
        public Executor SERIAL_EXECUTOR() {
            return AsyncTask.SERIAL_EXECUTOR;
        }
    }
}
