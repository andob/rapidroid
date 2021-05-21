package ro.andreidobrescu.rapidroid.workflow

object SequentialRunnerFactory
{
    fun newSequentialRunner(workflowContext : WorkflowContext,
                            tasks : List<ComposableWorkflowTask>)
                            : ComposableWorkflowTask
    {
        return {
            for (task in tasks)
            {
                workflowContext.withTransaction {
                    task.invoke()
                }
            }
        }
    }
}
