package com.risewide.bdebugapp.communication.util;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import android.os.AsyncTask;
import android.util.Log;

/**
 * Created by birdea on 2017-08-10.
 */

public class HandyThreadTask {

	private static final String TAG = HandyThreadTask.class.getSimpleName();

	/**
	 * Most code are referenced from {@link AsyncTask}.
	 */
	private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
	// We want at least 2 threads and at most 4 threads in the core pool,
	// preferring to have 1 less than the CPU count to avoid saturating
	// the CPU with background work
	private static final int CORE_POOL_SIZE = Math.max(2, Math.min(CPU_COUNT - 1, 8));
	private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
	private static final int KEEP_ALIVE_SECONDS = 5;

	private static final ThreadFactory sThreadFactory = new ThreadFactory() {
		private final AtomicInteger mCount = new AtomicInteger(1);

		public Thread newThread(Runnable r) {
			int id = mCount.getAndIncrement();
			Log.d(TAG, "newThread _id:"+id);
			return new Thread(r, "HandyThreadTask #" + id);
		}
	};

	private static final BlockingQueue<Runnable> sPoolWorkQueue =
			new LinkedBlockingQueue<>();

	/**
	 * An {@link Executor} that can be used to execute tasks in parallel.
	 */
	public static final ExecutorService THREAD_POOL_EXECUTOR;

	static {
		ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
				CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_SECONDS, TimeUnit.SECONDS,
				sPoolWorkQueue, sThreadFactory);
		threadPoolExecutor.allowCoreThreadTimeOut(true);
		Log.d(TAG, "threadPoolExecutor - CPU_COUNT:"+CPU_COUNT+", CORE_POOL_SIZE:"+CORE_POOL_SIZE+", MAXIMUM_POOL_SIZE:"+MAXIMUM_POOL_SIZE);
		THREAD_POOL_EXECUTOR = threadPoolExecutor;
	}

	public static void execute(Runnable runnable) {
		Log.d(TAG, "execute runnable:"+runnable);
		THREAD_POOL_EXECUTOR.execute(runnable);
	}
}
