package com.lqwawa.intleducation.lqpay.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

 /**
  * ================================================
  * 作    者：Blizzard-liu
  * 版    本：1.0
  * 创建日期：2017/7/17 15:02
  * 描    述：
  * 修订历史：
  * ================================================
  */

public class ThreadManager {
    private static ExecutorService mExecutors = Executors.newSingleThreadExecutor();

    public static void execute(Runnable runnable) {
        if (mExecutors == null) {
            mExecutors = Executors.newSingleThreadExecutor();
        }
        mExecutors.execute(runnable);
    }

    public static void shutdown() {
        if (mExecutors == null) return;
        if (mExecutors.isShutdown()) return;
        mExecutors.shutdownNow();
        mExecutors = null;
    }
}
