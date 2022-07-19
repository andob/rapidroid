package ro.andob.rapidroid.workflow

class WorkflowTasksCollector
(
    private val workflowContext : WorkflowContext
)
{
    private val tasks = mutableListOf<() -> Unit>()

    fun task(task : () -> Unit)
    {
        tasks.add(task)
    }

    internal fun toSequentialRunner() : () -> Unit
    {
        return SequentialRunnerFactory.newSequentialRunner(
                workflowContext = workflowContext,
                tasks = tasks)
    }

    internal fun toParallelRunner(numberOfThreads : Int) : () -> Unit
    {
        return ParallelRunnerFactory.newParallelRunner(
                workflowContext = workflowContext,
                numberOfThreads = numberOfThreads,
                tasks = tasks)
    }

    fun sequential
    (
        block : WorkflowTasksCollector.() -> (Unit)
    )
    {
        val collector = WorkflowTasksCollector(workflowContext)
        block.invoke(collector)
        tasks.add(collector.toSequentialRunner())
    }

    fun parallel
    (
        numberOfThreads : Int = WorkflowContext.DEFAULT_NUMBER_OF_THREADS,
        block : WorkflowTasksCollector.() -> (Unit)
    )
    {
        val collector = WorkflowTasksCollector(workflowContext)
        block.invoke(collector)
        tasks.add(collector.toParallelRunner(numberOfThreads))
    }

    fun <T> parallelList
    (
        itemsProvider : () -> List<T>,
        itemSubtask : (T) -> Unit,
        preconditions : (() -> Unit)? = null,
        numberOfThreads : Int = WorkflowContext.DEFAULT_NUMBER_OF_THREADS,
    )
    {
        tasks.add {
            preconditions?.invoke()
            val items = itemsProvider()
            if (items.isNotEmpty())
            {
                val collector = WorkflowTasksCollector(workflowContext)
                items.map { item -> collector.task { itemSubtask(item) } }
                collector.toParallelRunner(numberOfThreads).invoke()
            }
        }
    }
}
