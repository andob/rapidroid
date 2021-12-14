package ro.andob.rapidroid.kotlin

import ro.andob.rapidroid.actor.Actor
import ro.andob.rapidroid.future.kotlin.Future
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
    fun thread(procedure : () -> Unit) : Thread =
        ThreadRunner.run(procedure)

    @JvmStatic
    fun thread(threadIsRunningFlag : ThreadIsRunningFlag, procedure : () -> Unit) : Thread =
        ThreadRunner.run(procedure, threadIsRunningFlag)

    @JvmStatic
    fun threadIfNotAlreadyRunning(threadIsRunningFlag : ThreadIsRunningFlag, procedure : () -> Unit) : Thread? =
        ThreadRunner.runIfNotAlreadyRunning(procedure, threadIsRunningFlag)

    @JvmStatic
    fun onUiThread(procedure : () -> Unit) =
        UIThreadRunner.runOnUIThread(procedure)

    /*
     * Futures
     */

    @JvmStatic
    fun <T> async(supplier : () -> T) : Future<T> =
        Future(supplier)

    /*
     * Workflow
     */

    @JvmStatic
    fun workflow(dslBlock : WorkflowContext.() -> Unit) =
        dslBlock.invoke(WorkflowContext())

    /*
     * Actor
     */

    @JvmStatic
    fun <EVENT> eventOnActor(actor : Actor<EVENT>, event : EVENT) =
        actor.enqueueEvent(event)
}
