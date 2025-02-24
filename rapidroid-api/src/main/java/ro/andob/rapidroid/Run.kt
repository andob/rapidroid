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
     * Threads Kotlin API
     */

    @JvmStatic @JvmSynthetic
    fun thread(procedure : () -> Unit) : Thread =
        ThreadRunner.run(procedure)

    @JvmStatic @JvmSynthetic
    fun thread(threadIsRunningFlag : ThreadIsRunningFlag, procedure : () -> Unit) : Thread =
        ThreadRunner.run(procedure, threadIsRunningFlag)

    @JvmStatic @JvmSynthetic
    fun threadIfNotAlreadyRunning(threadIsRunningFlag : ThreadIsRunningFlag, procedure : () -> Unit) : Thread? =
        ThreadRunner.runIfNotAlreadyRunning(procedure, threadIsRunningFlag)

    @JvmStatic @JvmSynthetic
    fun onUiThread(procedure : () -> Unit) =
        UIThreadRunner.runOnUIThread(procedure)

    /*
     * Threads Java API
     */

    @JvmStatic @JvmName("thread")
    fun javaAPIThread(procedure : Procedure) : Thread =
        ThreadRunner.run(procedure)

    @JvmStatic @JvmName("thread")
    fun javaAPIThread(threadIsRunningFlag : ThreadIsRunningFlag, procedure : Procedure) : Thread =
        ThreadRunner.run(procedure, threadIsRunningFlag)

    @JvmStatic @JvmName("threadIfNotAlreadyRunning")
    fun javaAPIThreadIfNotAlreadyRunning(threadIsRunningFlag : ThreadIsRunningFlag, procedure : Procedure) : Thread? =
        ThreadRunner.runIfNotAlreadyRunning(procedure, threadIsRunningFlag)

    @JvmStatic @JvmName("onUiThread")
    fun javaAPIOnUiThread(procedure : Procedure) =
        UIThreadRunner.runOnUIThread(procedure)

    /*
     * Futures Kotlin API
     */

    @JvmStatic @JvmSynthetic
    fun <T> async(supplier : () -> T) : Future<T> =
        Future(supplier)

    /*
     * Futures Java API
     */

    @JvmStatic @JvmName("async")
    fun javaAPIAsync(procedure : Procedure) : Future<Unit> =
        Future { procedure.call() }

    @JvmStatic @JvmName("async")
    fun <T> javaAPIAsync(supplier : Supplier<T>) : Future<T> =
        Future(supplier)

    /*
     * Workflow Kotlin API
     */

    @JvmStatic @JvmSynthetic
    fun workflow(dslBlock : WorkflowContext.() -> (Unit)) =
        dslBlock.invoke(WorkflowContext())

    /*
     * Actor Java+Kotlin API
     */

    @JvmStatic
    fun <EVENT> eventOnActor(actor : Actor<EVENT>, event : EVENT) =
        actor.enqueueEvent(event)
}
