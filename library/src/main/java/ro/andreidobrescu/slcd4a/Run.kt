package ro.andreidobrescu.slcd4a

import ro.andreidobrescu.slcd4a.functional_interfaces.Procedure
import ro.andreidobrescu.slcd4a.functional_interfaces.Supplier
import ro.andreidobrescu.slcd4a.functional_interfaces.toSupplier
import ro.andreidobrescu.slcd4a.future.Future
import ro.andreidobrescu.slcd4a.ui_thread.UIThreadRunner

object Run
{
    /*
     * Threads
     */

    @JvmStatic
    fun thread(task : Procedure)
    {
        Thread {
            try { task.invoke() }
            catch (ex : Throwable) { SLCD4A.exceptionLogger.log(ex) }
        }.start()
    }

    @JvmStatic
    fun onUiThread(task : Procedure) =
        UIThreadRunner.implementation.invoke(task)

    /*
     * Futures
     */

    @JvmStatic
    fun async(task : Procedure) =
        async(task.toSupplier())

    @JvmStatic
    fun <T> async(task : Supplier<T>) =
        Future(task)

//todo    Run.workflow -> Workflow
//todo    Run.ifNotAlreadyRunning -> Once<Thread>
//todo    Run.actor -> Actor / Single-threaded message queue
}
