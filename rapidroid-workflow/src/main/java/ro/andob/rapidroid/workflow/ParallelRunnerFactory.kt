package ro.andob.rapidroid.workflow

import java.util.concurrent.Callable
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executors

object ParallelRunnerFactory
{
    fun newParallelRunner
    (
        workflowContext : WorkflowContext,
        numberOfThreads : Int,
        tasks : List<() -> Unit>,
    ) : () -> Unit
    {
        return lambda@ {
            if (tasks.isEmpty())
                return@lambda

            if (tasks.size==1)
            {
                tasks.first().invoke()
                return@lambda
            }

            val executor = Executors.newFixedThreadPool(numberOfThreads)
            val futures = executor.invokeAll(tasks.map { Callable { it() } })
            var error : Throwable? = null

            try
            {
                futures.forEach { future ->
                    workflowContext.withTransaction {
                        future.get()
                    }
                }
            }
            catch (ex : Exception)
            {
                error = (ex as? ExecutionException)?.cause?:ex
            }
            finally
            {
                executor.shutdownNow()
            }

            if (error!=null)
                throw error
        }
    }
}