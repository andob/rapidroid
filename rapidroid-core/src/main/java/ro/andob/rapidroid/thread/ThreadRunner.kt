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

        Thread(runnable).start()
    }

    fun runIfNotAlreadyRunning(procedure : Procedure, threadIsRunningFlag : ThreadIsRunningFlag)
    {
        if (!threadIsRunningFlag.get())
            run(procedure, threadIsRunningFlag)
    }
}
