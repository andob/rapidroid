package ro.andreidobrescu.slcd4a.workflow

class WorkflowTasksCollector
(
    private val workflowContext : WorkflowContext
)
{
    private val tasks = mutableListOf<ComposableWorkflowTask>()

    fun task(task : ComposableWorkflowTask)
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
        val collector=WorkflowTasksCollector(workflowContext)
        block.invoke(collector)
        tasks.add(collector.toSequentialRunner())
    }

    fun parallel(block : WorkflowTasksCollector.() -> (Unit))
    {
        val collector=WorkflowTasksCollector(workflowContext)
        block.invoke(collector)
        tasks.add(collector.toParallelRunner())
    }
}
