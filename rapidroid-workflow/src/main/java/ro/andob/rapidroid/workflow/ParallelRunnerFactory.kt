package ro.andob.rapidroid.workflow

import ro.andob.rapidroid.Rapidroid
import java.util.concurrent.atomic.AtomicReference

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

            val errorHolder = AtomicReference<Throwable>(null)

            groupTasks(numberOfThreads, tasks)
                .map { task -> createTaskThread(workflowContext, errorHolder, task) }
                .map { thread -> thread.also { it.start() } }
                .forEach { thread -> thread.tryJoin() }

            errorHolder.get()?.let { throw it }
        }
    }

    private fun groupTasks
    (
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
            taskGroup.forEach { task -> task() }
        } }
    }

    private fun createTaskThread
    (
        workflowContext : WorkflowContext,
        errorHolder : AtomicReference<Throwable>,
        task : () -> Unit,
    ) = Thread {
        try
        {
            workflowContext.withTransaction {
                task.invoke()
            }
        }
        catch (ex : Throwable)
        {
            errorHolder.set(ex)
        }
    }

    private fun Thread.tryJoin() = try { join() }
        catch (ex : InterruptedException) { Rapidroid.exceptionLogger.log(ex) }
}