package ro.andob.rapidroid.future;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import ro.andob.rapidroid.thread.ThreadRunner;

public class FutureThreadPoolExecutors
{
    public static final ThreadPoolExecutor DEFAULT;

    static
    {
        DEFAULT=new ThreadPoolExecutor(4, 4, 1, TimeUnit.MINUTES, new LinkedBlockingQueue<>(Integer.MAX_VALUE));
        DEFAULT.setRejectedExecutionHandler(((runnable, executor) -> ThreadRunner.run(runnable)));
        DEFAULT.allowCoreThreadTimeOut(true);
    }
}
