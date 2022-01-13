package ro.andob.rapidroid.workflow

import ro.andob.rapidroid.Rapidroid

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
                return@lambda tasks[0]()

            groupTasks(workflowContext, numberOfThreads, tasks)
                .map { task -> createTaskThread(workflowContext, task) }
                .map { thread -> thread.also { it.start() } }
                .forEach { thread -> thread.tryJoin() }

            workflowContext.errorNotifier.throwOnError()
        }
    }

    private fun groupTasks
    (
        workflowContext : WorkflowContext,
        numberOfThreads : Int,
        tasks : List<() -> Unit>,
    ) : List<() -> Unit>
    {
        val tasksGroups = Array(size = numberOfThreads,
            init = { mutableListOf<() -> Unit>() })

        for ((index, task) in tasks.shuffled().withIndex())
        {
            val groupIndex = index.mod(numberOfThreads)
            tasksGroups[groupIndex].add(task)
        }

        return tasksGroups.map { taskGroup -> {
            taskGroup.forEach { task ->
                workflowContext.transactional {
                    task.invoke()
                }
            }
        } }
    }

    private fun createTaskThread
    (
        workflowContext : WorkflowContext,
        task : () -> Unit,
    ) = Thread {
        try { task.invoke() }
        catch (ex : Throwable) { workflowContext.errorNotifier.notify(ex) }
    }

    private fun Thread.tryJoin() = try { join() }
        catch (ex : InterruptedException) { Rapidroid.exceptionLogger.log(ex) }
}