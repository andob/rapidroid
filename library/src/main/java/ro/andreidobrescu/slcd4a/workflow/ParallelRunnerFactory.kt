package ro.andreidobrescu.slcd4a.workflow

import java.util.concurrent.CountDownLatch
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference

object ParallelRunnerFactory
{
    private val threadPoolExecutor by lazy {
        val threadPoolExecutor=ThreadPoolExecutor(6, 6, 1, TimeUnit.MINUTES, LinkedBlockingQueue(Int.MAX_VALUE))
        threadPoolExecutor.setRejectedExecutionHandler { runnable, executor -> Thread(runnable).start() }
        threadPoolExecutor.allowCoreThreadTimeOut(true)
        return@lazy threadPoolExecutor
    }

    fun newParallelRunner(workflowContext : WorkflowContext,
                          tasks : List<ComposableWorkflowTask>)
                        : ComposableWorkflowTask
    {
        return lambda@ {
            if (tasks.isEmpty())
                return@lambda

            if (tasks.size==1)
                tasks.first().invoke()

            val latch=CountDownLatch(tasks.size)

            val atomicError=AtomicReference<Exception>(null)

            for (task in tasks)
            {
                threadPoolExecutor.execute {
                    try
                    {
                        workflowContext.withTransaction {
                            task.invoke()
                        }

                        latch.countDown()
                    }
                    catch (ex : Exception)
                    {
                        atomicError.set(ex)

                        while (latch.count>0)
                            latch.countDown()
                    }
                }
            }

            latch.await(Long.MAX_VALUE, TimeUnit.HOURS)

            if (atomicError.get()!=null)
                throw atomicError.get()!!
        }
    }
}