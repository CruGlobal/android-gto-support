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

    private final String mName;
    private final int mPoolSize;

    private ThreadPoolExecutor mDefaultExecutor = null;
    private Executor mExecutor = null;
    private final BlockingQueue<Future<Integer>> mTasks = new LinkedBlockingQueue<>();

    protected ThreadedIntentService(final String name) {
        this(name, 10);
    }

    protected ThreadedIntentService(final String name, final int poolSize) {
        mName = name;
        mPoolSize = poolSize;
    }

    /* BEGIN lifecycle */

    @Override
    public void onCreate() {
        super.onCreate();

        // create the executor for this IntentService
        final Executor executor = onCreateExecutor();
        mExecutor = executor != null ? executor : defaultExecutor();
    }

    protected Executor onCreateExecutor() {
        return null;
    }

    @Override
    public IBinder onBind(final Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        final RunnableFuture<Integer> task = new IntentRunnable(intent, startId);
        mTasks.add(task);
        mExecutor.execute(task);
        return mRedelivery ? START_REDELIVER_INTENT : START_NOT_STICKY;
    }

    protected abstract void onHandleIntent(Intent intent);

    @Override
    public void onDestroy() {
        super.onDestroy();
        onDestroyExecutor();
    }

    protected void onDestroyExecutor() {
        mExecutor = null;

        // shutdown the default executor if it exists
        if (mDefaultExecutor != null) {
            mDefaultExecutor.shutdown();
        }
    }

    /* END lifecycle */

    public void setIntentRedelivery(final boolean enabled) {
        mRedelivery = enabled;
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    private Executor defaultExecutor() {
        // create the defaultExecutor if it doesn't exist yet
        if (mDefaultExecutor == null) {
            final ThreadFactory factory = new ThreadFactory() {
                private final AtomicInteger mCount = new AtomicInteger(1);

                @Override
                public Thread newThread(final Runnable r) {
                    return new Thread(r, mName + " #" + mCount.getAndIncrement());
                }
            };
            final BlockingQueue<Runnable> queue = new PriorityBlockingQueue<>(1, new IntentPriorityComparator());
            mDefaultExecutor = new ThreadPoolExecutor(mPoolSize, mPoolSize, 10, TimeUnit.SECONDS, queue, factory);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                mDefaultExecutor.allowCoreThreadTimeOut(true);
            } else {
                mDefaultExecutor.setCorePoolSize(0);
            }
        }

        return mDefaultExecutor;
    }

    void cleanDoneTasks() {
        synchronized (mTasks) {
            Future<Integer> task;
            while ((task = mTasks.peek()) != null && task.isDone()) {
                final Future<Integer> task2 = mTasks.remove();
                try {
                    stopSelf(task2.get());
                } catch (final InterruptedException e) {
                    // suppress interrupted exceptions, but still signal a stop for the current task
                    if (task2 instanceof IntentRunnable) {
                        stopSelf(((IntentRunnable) task2).mStartId);
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

    private final class IntentRunnable extends FutureTask<Integer> {
        final int mPriority;
        final int mStartId;

        private IntentRunnable(final Intent intent, final int startId) {
            super(new Callable<Integer>() {
                @Override
                public Integer call() {
                    onHandleIntent(intent);
                    return startId;
                }
            });

            mStartId = startId;
            mPriority = intent.getIntExtra(EXTRA_PRIORITY, PRIORITY_DEFAULT);
        }

        @Override
        protected void done() {
            super.done();
            ThreadedIntentService.this.cleanDoneTasks();
        }
    }

    protected static class IntentPriorityComparator implements Comparator<Runnable> {
        @Override
        public int compare(final Runnable lhs, final Runnable rhs) {
            final Integer lhsPri = lhs instanceof IntentRunnable ? ((IntentRunnable) lhs).mPriority : PRIORITY_DEFAULT;
            final Integer rhsPri = rhs instanceof IntentRunnable ? ((IntentRunnable) rhs).mPriority : PRIORITY_DEFAULT;
            return lhsPri.compareTo(rhsPri);
        }
    }
}
