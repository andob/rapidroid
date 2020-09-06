package ro.andreidobrescu.slcd4a.thread

import ro.andreidobrescu.slcd4a.SLCD4A
import ro.andreidobrescu.slcd4a.functional_interfaces.Procedure
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
                SLCD4A.exceptionLogger.log(ex)
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
