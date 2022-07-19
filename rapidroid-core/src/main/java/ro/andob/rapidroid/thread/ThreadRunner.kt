package ro.andob.rapidroid.thread

import ro.andob.rapidroid.Procedure
import ro.andob.rapidroid.Rapidroid

object ThreadRunner
{
    @JvmStatic
    fun run(procedure : Procedure) = run(procedure, null)

    @JvmStatic
    fun run
    (
        procedure : Procedure,
        threadIsRunningFlag : ThreadIsRunningFlag? = null,
    ) : Thread
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

        val thread = Thread(runnable)
        thread.start()
        return thread
    }

    fun runIfNotAlreadyRunning(procedure : Procedure, threadIsRunningFlag : ThreadIsRunningFlag) : Thread?
    {
        if (threadIsRunningFlag.compareAndSet(expectedValue = false, newValue = true))
            return run(procedure, threadIsRunningFlag)
        return null
    }
}
