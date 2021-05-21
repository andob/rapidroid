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

    internal fun toSequentialRunner() =
        SequentialRunnerFactory.newSequentialRunner(
            workflowContext = workflowContext,
            tasks = tasks)

    internal fun toParallelRunner() =
        ParallelRunnerFactory.newParallelRunner(
            workflowContext = workflowContext,
            tasks = tasks)

    fun sequential(block : WorkflowTasksCollector.() -> (Unit))
    {
        val collector = WorkflowTasksCollector(workflowContext)
        block.invoke(collector)
        tasks.add(collector.toSequentialRunner())
    }

    fun parallel(block : WorkflowTasksCollector.() -> (Unit))
    {
        val collector = WorkflowTasksCollector(workflowContext)
        block.invoke(collector)
        tasks.add(collector.toParallelRunner())
    }

    fun <T> parallelList
    (
        itemsProvider : () -> (List<T>),
        itemSubtask : (T) -> (Unit)
    )
    {
        tasks.add {
            val items = itemsProvider()
            if (items.isNotEmpty())
            {
                val collector = WorkflowTasksCollector(workflowContext)
                items.map { item -> collector.task { itemSubtask(item) } }
                collector.toParallelRunner().invoke()
            }
        }
    }
}
