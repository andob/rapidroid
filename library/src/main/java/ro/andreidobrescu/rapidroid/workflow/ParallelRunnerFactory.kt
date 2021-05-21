package ro.andreidobrescu.rapidroid.workflow

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference

object ParallelRunnerFactory
{
    fun newParallelRunner(workflowContext : WorkflowContext,
                          tasks : List<ComposableWorkflowTask>)
                        : ComposableWorkflowTask
    {
        return lambda@ {
            if (tasks.isEmpty())
                return@lambda

            if (tasks.size==1)
            {
                tasks.first().invoke()
                return@lambda
            }

            val latch=CountDownLatch(tasks.size)

            val atomicError=AtomicReference<Exception>(null)

            for (task in tasks)
            {
                workflowContext.threadPoolExecutor.execute {
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