package com.lqwawa.intleducation.factory.throwable;

import android.os.Process;

import com.lqwawa.intleducation.common.utils.IOUtil;
import com.lqwawa.intleducation.common.utils.LogUtil;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 全局异常捕获处理器
 * @date 2018/04/09 14:14
 * @history v1.0
 * **********************************
 */
public class LQUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        final Writer result = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(result);

        Throwable cause = throwable;

        while(null!=cause){
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }

        //CRASH堆栈信息
        final String stacktraceAsString = result.toString();

        LogUtil.e(this.getClass(),stacktraceAsString);

        IOUtil.closeIO(printWriter);
        IOUtil.closeIO(result);
    }
}
