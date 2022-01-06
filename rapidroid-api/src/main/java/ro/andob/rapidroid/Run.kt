package ro.andob.rapidroid

import ro.andob.rapidroid.actor.Actor
import ro.andob.rapidroid.future.Future
import ro.andob.rapidroid.thread.ThreadIsRunningFlag
import ro.andob.rapidroid.thread.ThreadRunner
import ro.andob.rapidroid.thread.UIThreadRunner
import ro.andob.rapidroid.workflow.WorkflowContext

object Run
{
    /*
     * Threads
     */

    @JvmStatic
    fun thread(procedure : Procedure) : Thread =
        ThreadRunner.run(procedure)

    @JvmStatic @JvmSynthetic
    fun thread(procedure : () -> Unit) : Thread =
        ThreadRunner.run(procedure)

    @JvmStatic
    fun thread(threadIsRunningFlag : ThreadIsRunningFlag, procedure : Procedure) : Thread =
        ThreadRunner.run(procedure, threadIsRunningFlag)

    @JvmStatic @JvmSynthetic
    fun thread(threadIsRunningFlag : ThreadIsRunningFlag, procedure : () -> Unit) : Thread =
        ThreadRunner.run(procedure, threadIsRunningFlag)

    @JvmStatic
    fun threadIfNotAlreadyRunning(threadIsRunningFlag : ThreadIsRunningFlag, procedure : Procedure) : Thread? =
        ThreadRunner.runIfNotAlreadyRunning(procedure, threadIsRunningFlag)

    @JvmStatic @JvmSynthetic
    fun threadIfNotAlreadyRunning(threadIsRunningFlag : ThreadIsRunningFlag, procedure : () -> Unit) : Thread? =
        ThreadRunner.runIfNotAlreadyRunning(procedure, threadIsRunningFlag)

    @JvmStatic
    fun onUiThread(procedure : Procedure) =
        UIThreadRunner.runOnUIThread(procedure)

    @JvmStatic @JvmSynthetic
    fun onUiThread(procedure : () -> Unit) =
        UIThreadRunner.runOnUIThread(procedure)

    /*
     * Futures
     */

    @JvmStatic
    fun async(procedure : Procedure) : Future<Unit> =
        Future { procedure.call() }

    @JvmStatic
    fun <T> async(supplier : Supplier<T>) : Future<T> =
        Future(supplier)

    @JvmStatic @JvmSynthetic
    fun <T> async(supplier : () -> T) : Future<T> =
        Future(supplier)

    /*
     * Workflow
     */

    @JvmStatic @JvmSynthetic
    fun workflow(dslBlock : WorkflowContext.() -> (Unit)) =
        dslBlock.invoke(WorkflowContext())

    /*
     * Actor
     */

    @JvmStatic
    fun <EVENT> eventOnActor(actor : Actor<EVENT>, event : EVENT) =
        actor.enqueueEvent(event)
}
