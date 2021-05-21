package ro.andob.rapidroid.thread

import ro.andob.rapidroid.Rapidroid
import java.util.concurrent.ThreadPoolExecutor

object ThreadRunner
{
    @JvmStatic
    fun run(runnable : Runnable) = run(runnable, null, null)

    @JvmStatic
    fun run
    (
        runnable : Runnable,
        threadIsRunningFlag : ThreadIsRunningFlag? = null,
        threadPoolExecutor : ThreadPoolExecutor? = null
    )
    {
        val wrappedRunnable = Runnable {
            try
            {
                threadIsRunningFlag?.set(true)

                runnable.run()
            }
            catch (ex : Throwable)
            {
                Rapidroid.exceptionLogger.log(ex)
            }
            finally
            {
                threadIsRunningFlag?.set(false)
            }
        }

        if (threadPoolExecutor!=null)
            threadPoolExecutor.execute(wrappedRunnable)
        else Thread(runnable).start()
    }

    fun runIfNotAlreadyRunning(task : Runnable, threadIsRunningFlag : ThreadIsRunningFlag)
    {
        if (!threadIsRunningFlag.get())
            run(task, threadIsRunningFlag)
    }
}
