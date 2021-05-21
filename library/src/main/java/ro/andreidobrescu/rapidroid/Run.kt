package ro.andreidobrescu.rapidroid

import ro.andreidobrescu.rapidroid.actor.Actor
import ro.andreidobrescu.rapidroid.functional_interfaces.Procedure
import ro.andreidobrescu.rapidroid.functional_interfaces.Supplier
import ro.andreidobrescu.rapidroid.functional_interfaces.toSupplier
import ro.andreidobrescu.rapidroid.future.Future
import ro.andreidobrescu.rapidroid.thread.ThreadIsRunningFlag
import ro.andreidobrescu.rapidroid.thread.ThreadRunner
import ro.andreidobrescu.rapidroid.thread.UIThreadRunner
import ro.andreidobrescu.rapidroid.workflow.WorkflowContext
import java.util.concurrent.ThreadPoolExecutor

object Run
{
    /*
     * Threads
     */

    @JvmStatic
    fun thread(task : Procedure) =
        ThreadRunner.run(task)

    @JvmStatic
    fun thread(threadIsRunningFlag : ThreadIsRunningFlag, task : Procedure) =
        ThreadRunner.run(task, threadIsRunningFlag)

    @JvmStatic
    fun threadIfNotAlreadyRunning(threadIsRunningFlag : ThreadIsRunningFlag, task : Procedure) =
        ThreadRunner.runIfNotAlreadyRunning(task, threadIsRunningFlag)

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

    /*
     * Workflow
     */

    @JvmStatic
    fun workflow(dslBlock : WorkflowContext.() -> (Unit)) =
        dslBlock.invoke(WorkflowContext())

    /*
     * Actor
     */

    @JvmStatic
    fun <EVENT> eventOnActor(actor : Actor<EVENT>, event : EVENT) =
        actor.enqueueEvent(event)

    /*
     * On thread pool executor
     */

    @JvmStatic
    fun onThreadPoolExecutor(threadPoolExecutor : ThreadPoolExecutor) =
        RunOnThreadPoolExecutor(threadPoolExecutor)

    class RunOnThreadPoolExecutor(private val threadPoolExecutor : ThreadPoolExecutor)
    {
        fun thread(task : Procedure) =
            ThreadRunner.run(task, threadPoolExecutor = threadPoolExecutor)

        fun workflow(dslBlock : WorkflowContext.() -> (Unit)) =
            dslBlock.invoke(WorkflowContext(threadPoolExecutor))
    }
}
