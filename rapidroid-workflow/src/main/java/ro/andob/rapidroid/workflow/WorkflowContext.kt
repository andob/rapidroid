package ro.andob.rapidroid.workflow

import ro.andob.rapidroid.Rapidroid

class WorkflowContext
{
    companion object
    {
        internal const val DEFAULT_NUMBER_OF_THREADS = 4
    }

    internal val errorNotifier = WorkflowErrorNotifier()

    internal fun transactional
    (
        block : WorkflowContext.() -> (Unit)
    )
    {
        errorNotifier.throwOnError()
        block.invoke(this)
        errorNotifier.throwOnError()
    }

    fun sequential
    (
        block : WorkflowTasksCollector.() -> (Unit)
    )
    {
        val collector = WorkflowTasksCollector(this)
        block.invoke(collector)

        try
        {
            collector.toSequentialRunner().invoke()
        }
        catch (ex : Throwable)
        {
            errorNotifier.notify(ex)
            Rapidroid.exceptionLogger.log(ex)
            throw ex
        }
    }

    fun parallel
    (
        numberOfThreads : Int = DEFAULT_NUMBER_OF_THREADS,
        block : WorkflowTasksCollector.() -> (Unit),
    )
    {
        val collector = WorkflowTasksCollector(this)
        block.invoke(collector)

        try
        {
            collector.toParallelRunner(numberOfThreads).invoke()
        }
        catch (ex : Throwable)
        {
            errorNotifier.notify(ex)
            Rapidroid.exceptionLogger.log(ex)
            throw ex
        }
    }
}
