package ro.andob.rapidroid.thread

import ro.andob.rapidroid.Procedure
import ro.andob.rapidroid.Rapidroid
import java.util.concurrent.ThreadPoolExecutor

object ThreadRunner
{
    @JvmStatic
    fun run(procedure : Procedure) = run(procedure, null, null)

    @JvmStatic
    fun run
    (
        procedure : Procedure,
        threadIsRunningFlag : ThreadIsRunningFlag? = null,
        threadPoolExecutor : ThreadPoolExecutor? = null
    )
    {
        val runnable = Runnable {
            try
            {
                threadIsRunningFlag?.set(true)

                procedure.call()
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
            threadPoolExecutor.execute(runnable)
        else Thread(runnable).start()
    }

    fun runIfNotAlreadyRunning(procedure : Procedure, threadIsRunningFlag : ThreadIsRunningFlag)
    {
        if (!threadIsRunningFlag.get())
            run(procedure, threadIsRunningFlag)
    }
}
