package com.lqwawa.libs.filedownloader;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.AbortPolicy;
import java.util.concurrent.TimeUnit;

public class DownloadExecutor {

	private static final int CORE_POOL_SIZE = 1;
	private static final int MAXIMUM_POOL_SIZE = 1;
	private static final long KEEP_ALIVE_TIME = 5L;

	private static ThreadPoolExecutor threadPool;

	/** 执行任务，当线程池处于关闭时，将会重新创建新的线程池 */
	public static synchronized void execute(Runnable runnable) {
		if (runnable == null) {
			return;
		}
		if (threadPool == null || threadPool.isShutdown()) {
			threadPool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME,
					TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(),
					Executors.defaultThreadFactory(), new AbortPolicy());
		}
		threadPool.execute(runnable);
	}

	/** 取消线程池中某个未执行的任务 */
	public static synchronized boolean cancel(Runnable run) {
		if (threadPool != null && (!threadPool.isShutdown() || threadPool.isTerminating())) {
			return threadPool.getQueue().remove(run);
		}
		return false;
	}

	/** 查看线程池中是否还有某个未执行的任务 */
	public static synchronized boolean contains(Runnable run) {
		if (threadPool != null && (!threadPool.isShutdown() || threadPool.isTerminating())) {
			return threadPool.getQueue().contains(run);
		}
		return false;
	}
	
	/** 立刻关闭线程池，正在执行的任务也将会被中断 */
	public static void stop() {
		if (threadPool != null && (!threadPool.isShutdown() || threadPool.isTerminating())) {
			threadPool.shutdownNow();
		}
	}

	/** 平缓关闭单任务线程池，会确保所有已经加入的任务都将会被执行完毕才关闭 */
	public static synchronized void shutdown() {
		if (threadPool != null && (!threadPool.isShutdown() || threadPool.isTerminating())) {
			threadPool.shutdownNow();
		}
	}

}
