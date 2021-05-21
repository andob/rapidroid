package ro.andreidobrescu.rapidroid.thread

import ro.andreidobrescu.rapidroid.rapidroid
import ro.andreidobrescu.rapidroid.functional_interfaces.Procedure
import java.util.concurrent.ThreadPoolExecutor

object ThreadRunner
{
    fun run(task : Procedure,
            threadIsRunningFlag : ThreadIsRunningFlag? = null,
            threadPoolExecutor : ThreadPoolExecutor? = null)
    {
        val runnable=Runnable {
            try
            {
                threadIsRunningFlag?.set(true)

                task.invoke()
            }
            catch (ex : Throwable)
            {
                rapidroid.exceptionLogger.log(ex)
            }
            finally
            {
                threadIsRunningFlag?.set(false)
            }
        }

        if (threadPoolExecutor!=null)
            threadPoolExecutor.execute(runnable)
        else Thread(runnable).start()
    }

    fun runIfNotAlreadyRunning(task : Procedure, threadIsRunningFlag : ThreadIsRunningFlag)
    {
        if (!threadIsRunningFlag.get())
            run(task, threadIsRunningFlag)
    }
}
