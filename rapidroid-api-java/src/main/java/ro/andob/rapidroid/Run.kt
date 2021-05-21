package ro.andob.rapidroid

import ro.andob.rapidroid.actor.Actor
import ro.andob.rapidroid.future.Future
import ro.andob.rapidroid.future.Supplier
import ro.andob.rapidroid.thread.ThreadIsRunningFlag
import ro.andob.rapidroid.thread.ThreadRunner
import ro.andob.rapidroid.thread.UIThreadRunner
import ro.andob.rapidroid.workflow.WorkflowContext
import java.util.concurrent.ThreadPoolExecutor

object Run
{
    /*
     * Threads
     */

    @JvmStatic
    fun thread(runnable : Runnable) =
        ThreadRunner.run(runnable)

    @JvmStatic
    fun thread(threadIsRunningFlag : ThreadIsRunningFlag, runnable : Runnable) =
        ThreadRunner.run(runnable, threadIsRunningFlag)

    @JvmStatic
    fun threadIfNotAlreadyRunning(threadIsRunningFlag : ThreadIsRunningFlag, runnable : Runnable) =
        ThreadRunner.runIfNotAlreadyRunning(runnable, threadIsRunningFlag)

    @JvmStatic
    fun onUiThread(runnable : Runnable) =
        UIThreadRunner.runOnUIThread(runnable)

    /*
     * Futures
     */

    @JvmStatic
    fun async(runnable : Runnable) : Future<Unit> =
        Future { runnable.run() }

    @JvmStatic
    fun <T> async(supplier : Supplier<T>) : Future<T> =
        Future(supplier)

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
        fun thread(runnable : Runnable) =
            ThreadRunner.run(runnable, threadPoolExecutor = threadPoolExecutor)

        fun async(runnable : Runnable) : Future<Unit> =
            Future({ runnable.run() }, threadPoolExecutor)

        fun <T> async(supplier : Supplier<T>) : Future<T> =
            Future(supplier, threadPoolExecutor)

        fun workflow(dslBlock : WorkflowContext.() -> (Unit)) =
            dslBlock.invoke(WorkflowContext(threadPoolExecutor))
    }
}
