package ro.andreidobrescu.rapidroid.workflow;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import ro.andreidobrescu.rapidroid.Run;

public final class WorkflowThreadPoolExecutors
{
    public static final ThreadPoolExecutor DEFAULT;

    static
    {
        DEFAULT=new ThreadPoolExecutor(6, 6, 1, TimeUnit.MINUTES, new LinkedBlockingQueue<>(Integer.MAX_VALUE));
        DEFAULT.setRejectedExecutionHandler(((runnable, executor) -> Run.thread(runnable::run)));
        DEFAULT.allowCoreThreadTimeOut(true);
    }
}
