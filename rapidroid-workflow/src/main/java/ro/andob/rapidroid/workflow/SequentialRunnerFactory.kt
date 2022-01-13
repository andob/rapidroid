package ro.andob.rapidroid.workflow

object SequentialRunnerFactory
{
    fun newSequentialRunner
    (
        workflowContext : WorkflowContext,
        tasks : List<() -> Unit>
    ) : () -> Unit
    {
        return {
            for (task in tasks)
            {
                workflowContext.transactional {
                    task.invoke()
                }
            }
        }
    }
}
