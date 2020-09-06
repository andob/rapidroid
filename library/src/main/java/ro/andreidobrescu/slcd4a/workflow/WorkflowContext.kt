package ro.andreidobrescu.slcd4a.workflow

import ro.andreidobrescu.slcd4a.SLCD4A
import java.util.concurrent.ThreadPoolExecutor

class WorkflowContext
(
    internal val threadPoolExecutor : ThreadPoolExecutor = WorkflowThreadPoolExecutors.DEFAULT
)
{
    private val errorNotifier = WorkflowErrorNotifier()

    internal fun withTransaction(block : WorkflowContext.() -> (Unit))
    {
        errorNotifier.throwOnError()
        block.invoke(this)
        errorNotifier.throwOnError()
    }

    fun sequential(block : WorkflowTasksCollector.() -> (Unit))
    {
        val collector=WorkflowTasksCollector(this)
        block.invoke(collector)

        try
        {
            collector.toSequentialRunner().invoke()
        }
        catch (ex : Throwable)
        {
            errorNotifier.notify(ex)
            SLCD4A.exceptionLogger.log(ex)
            throw ex
        }
    }

    fun parallel(block : WorkflowTasksCollector.() -> (Unit))
    {
        val collector=WorkflowTasksCollector(this)
        block.invoke(collector)

        try
        {
            collector.toParallelRunner().invoke()
        }
        catch (ex : Throwable)
        {
            errorNotifier.notify(ex)
            SLCD4A.exceptionLogger.log(ex)
            throw ex
        }
    }
}
