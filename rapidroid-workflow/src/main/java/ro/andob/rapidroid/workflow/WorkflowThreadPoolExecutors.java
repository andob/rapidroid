package ro.andob.rapidroid.workflow;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import ro.andob.rapidroid.thread.ThreadRunner;

public final class WorkflowThreadPoolExecutors
{
    public static final ThreadPoolExecutor DEFAULT;

    static
    {
        DEFAULT=new ThreadPoolExecutor(6, 6, 1, TimeUnit.MINUTES, new LinkedBlockingQueue<>(Integer.MAX_VALUE));
        DEFAULT.setRejectedExecutionHandler(((runnable, executor) -> ThreadRunner.run(runnable)));
        DEFAULT.allowCoreThreadTimeOut(true);
    }
}
