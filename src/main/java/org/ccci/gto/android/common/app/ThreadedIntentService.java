package org.ccci.gto.android.common.app;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import java.util.Comparator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class ThreadedIntentService extends Service {
    public static final String EXTRA_PRIORITY = ThreadedIntentService.class.getName() + ".EXTRA_PRIORITY";

    public static final int PRIORITY_LOW = 20;
    public static final int PRIORITY_DEFAULT = 10;
    public static final int PRIORITY_HIGH = 0;

    private boolean mRedelivery;

    private final String name;
    private final int poolSize;

    private ThreadPoolExecutor defaultExecutor = null;
    private Executor executor = null;
    private final BlockingQueue<Future<Integer>> tasks = new LinkedBlockingQueue<Future<Integer>>();

    protected ThreadedIntentService(final String name) {
        this(name, 10);
    }

    protected ThreadedIntentService(final String name, final int poolSize) {
        this.name = name;
        this.poolSize = poolSize;
    }

    /* BEGIN lifecycle */

    @Override
    public void onCreate() {
        super.onCreate();

        // create the executor for this IntentService
        final Executor executor = this.onCreateExecutor();
        this.executor = executor != null ? executor : defaultExecutor();
    }

    public Executor onCreateExecutor() {
        return null;
    }

    @Override
    public IBinder onBind(final Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        final RunnableFuture<Integer> task = new IntentRunnable(intent, startId);
        this.tasks.add(task);
        this.executor.execute(task);
        return mRedelivery ? START_REDELIVER_INTENT : START_NOT_STICKY;
    }

    protected abstract void onHandleIntent(Intent intent);

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.onDestroyExecutor();
    }

    public void onDestroyExecutor() {
        this.executor = null;

        // shutdown the default executor if it exists
        if (this.defaultExecutor != null) {
            this.defaultExecutor.shutdown();
        }
    }

    /* END lifecycle */

    public void setIntentRedelivery(final boolean enabled) {
        mRedelivery = enabled;
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    private Executor defaultExecutor() {
        // create the defaultExecutor if it doesn't exist yet
        if (this.defaultExecutor == null) {
            final ThreadFactory threadFactory = new ThreadFactory() {
                private final String name = ThreadedIntentService.this.name;
                private final AtomicInteger count = new AtomicInteger(1);

                public Thread newThread(final Runnable r) {
                    return new Thread(r, name + " #" + count.getAndIncrement());
                }
            };
            final BlockingQueue<Runnable> queue = new PriorityBlockingQueue<>(1, new IntentPriorityComparator());
            this.defaultExecutor =
                    new ThreadPoolExecutor(this.poolSize, this.poolSize, 10, TimeUnit.SECONDS, queue, threadFactory);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                this.defaultExecutor.allowCoreThreadTimeOut(true);
            } else {
                this.defaultExecutor.setCorePoolSize(0);
            }
        }

        return this.defaultExecutor;
    }

    private void cleanDoneTasks() {
        synchronized (this.tasks) {
            Future<Integer> task;
            while ((task = this.tasks.peek()) != null && task.isDone()) {
                final Future<Integer> task2 = this.tasks.remove();
                try {
                    this.stopSelf(task2.get());
                } catch (final InterruptedException e) {
                    // suppress interrupted exceptions, but still signal a stop for the current task
                    if (task2 instanceof IntentRunnable) {
                        this.stopSelf(((IntentRunnable) task2).startId);
                    }
                } catch (final ExecutionException e) {
                    // propagate runtime ExecutionExceptions
                    final Throwable cause = e.getCause();
                    if (cause instanceof RuntimeException) {
                        throw (RuntimeException) cause;
                    } else {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    private class IntentRunnable extends FutureTask<Integer> {
        private final int priority;
        private final int startId;

        private IntentRunnable(final Intent intent, final int startId) {
            super(new Callable<Integer>() {
                @Override
                public Integer call() {
                    ThreadedIntentService.this.onHandleIntent(intent);
                    return startId;
                }
            });

            this.startId = startId;
            this.priority = intent.getIntExtra(EXTRA_PRIORITY, PRIORITY_DEFAULT);
        }

        @Override
        protected void done() {
            super.done();
            ThreadedIntentService.this.cleanDoneTasks();
        }
    }

    protected class IntentPriorityComparator implements Comparator<Runnable> {
        @Override
        public int compare(final Runnable lhs, final Runnable rhs) {
            final Integer lhsPri = lhs instanceof IntentRunnable ? ((IntentRunnable) lhs).priority : PRIORITY_DEFAULT;
            final Integer rhsPri = rhs instanceof IntentRunnable ? ((IntentRunnable) rhs).priority : PRIORITY_DEFAULT;
            return lhsPri.compareTo(rhsPri);
        }
    }
}
