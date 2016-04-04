package org.ccci.gto.android.common.util;

import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;

import org.ccci.gto.android.common.concurrent.NamedThreadFactory;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class AsyncTaskCompat {
    private static final Compat COMPAT;
    static {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            COMPAT = new GingerbreadCompat();
        } else {
            COMPAT = new HoneycombCompat();
        }
    }

    public static final Executor SERIAL_EXECUTOR = COMPAT.serialExecutor();
    public static final Executor THREAD_POOL_EXECUTOR = COMPAT.threadPoolExecutor();

    public static void execute(@NonNull final Runnable task) {
        COMPAT.execute(task);
    }

    interface Compat {
        void execute(@NonNull Runnable task);

        @NonNull
        Executor serialExecutor();

        @NonNull
        Executor threadPoolExecutor();
    }

    static class GingerbreadCompat implements Compat {
        private Executor mExecutor;

        @NonNull
        private synchronized Executor getExecutor() {
            if (mExecutor == null) {
                mExecutor = Executors.newFixedThreadPool(1, new NamedThreadFactory("AsyncTaskCompat"));
                if (mExecutor instanceof ThreadPoolExecutor) {
                    ((ThreadPoolExecutor) mExecutor).setKeepAliveTime(30, TimeUnit.SECONDS);
                    ((ThreadPoolExecutor) mExecutor).allowCoreThreadTimeOut(true);
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

        @NonNull
        @Override
        public Executor threadPoolExecutor() {
            return getExecutor();
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    static class HoneycombCompat extends GingerbreadCompat {
        @Override
        public void execute(@NonNull Runnable task) {
            AsyncTask.execute(task);
        }

        @NonNull
        @Override
        public Executor serialExecutor() {
            return AsyncTask.SERIAL_EXECUTOR;
        }

        @NonNull
        @Override
        public Executor threadPoolExecutor() {
            return AsyncTask.THREAD_POOL_EXECUTOR;
        }
    }
}
