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

    public static final Executor SERIAL_EXECUTOR = COMPAT.serialExecutor();

    public static void execute(@NonNull final Runnable task) {
        COMPAT.execute(task);
    }

    interface Compat {
        void execute(@NonNull Runnable task);

        @NonNull
        Executor serialExecutor();
    }

    static class FroyoCompat implements Compat {
        private Executor mExecutor;

        @NonNull
        @TargetApi(Build.VERSION_CODES.GINGERBREAD)
        private synchronized Executor getExecutor() {
            if (mExecutor == null) {
                mExecutor = Executors.newFixedThreadPool(1);
                if (mExecutor instanceof ThreadPoolExecutor) {
                    ((ThreadPoolExecutor) mExecutor).setKeepAliveTime(30, TimeUnit.SECONDS);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                        ((ThreadPoolExecutor) mExecutor).allowCoreThreadTimeOut(true);
                    } else {
                        ((ThreadPoolExecutor) mExecutor).setCorePoolSize(0);
                    }
                }
            }

            return mExecutor;
        }

        @Override
        public void execute(@NonNull final Runnable task) {
            getExecutor().execute(task);
        }

        @NonNull
        @Override
        public Executor serialExecutor() {
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
        public Executor serialExecutor() {
            return AsyncTask.SERIAL_EXECUTOR;
        }
    }
}
