package ro.andob.rapidroid.thread

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import kotlin.system.exitProcess

class Watchdog(private val args : Args)
{
    class Args
    (
        private val timerTick : Int,
        private val timerTickUnit : TimeUnit,
        private val loggingTimeout : Int,
        private val loggingTimeoutUnit : TimeUnit,
        val logger : (String) -> Unit,
        private val crashTimeout : Int,
        private val crashTimeoutUnit : TimeUnit,
        val crasher : (() -> Unit)? = null,
    )
    {
        val timerTickInMills get() = timerTickUnit.toMillis(timerTick.toLong())
        val loggingTimeoutInMills get() = loggingTimeoutUnit.toMillis(loggingTimeout.toLong())
        val crashTimeoutInMills get() = crashTimeoutUnit.toMillis(crashTimeout.toLong())

        companion object
        {
            @JvmStatic
            fun inSeconds(
                timerTick : Int,
                loggingTimeout : Int, logger : (String) -> Unit,
                crashTimeout : Int, crasher : (() -> Unit)? = null,
            ) = Args(
                timerTick = timerTick, timerTickUnit = TimeUnit.SECONDS,
                loggingTimeout = loggingTimeout, loggingTimeoutUnit = TimeUnit.SECONDS, logger = logger,
                crashTimeout = crashTimeout, crashTimeoutUnit = TimeUnit.SECONDS, crasher = crasher,
            )

            @JvmStatic
            fun inMills(
                timerTick : Int,
                loggingTimeout : Int, logger : (String) -> Unit,
                crashTimeout : Int, crasher : (() -> Unit)? = null,
            ) = Args(
                timerTick = timerTick, timerTickUnit = TimeUnit.MILLISECONDS,
                loggingTimeout = loggingTimeout, loggingTimeoutUnit = TimeUnit.MILLISECONDS, logger = logger,
                crashTimeout = crashTimeout, crashTimeoutUnit = TimeUnit.MILLISECONDS, crasher = crasher,
            )
        }
    }

    fun wrap(tasks : List<() -> Unit>) : List<() -> Unit>
    {
        if (tasks.isEmpty())
            return listOf()

        val watchdog = WatchdogImpl(args = args, numberOfTasks = tasks.size)

        return tasks.map { task -> watchdog.watched(task) }
    }

    private class WatchdogImpl(val args : Args, numberOfTasks : Int)
    {
        private val numberOfFinishedTasks = AtomicInteger(numberOfTasks)
        private val tasksData = ConcurrentHashMap<() -> Unit, TaskData?>(numberOfTasks)

        private class TaskData
        (
            val thread : Thread = Thread.currentThread(),
            val startedAtInMills : Long = System.currentTimeMillis(),
        )

        init
        {
            if (numberOfTasks <= 0)
                throw RuntimeException("Invalid numberOfTasks: $numberOfTasks")

            Thread {
                while (numberOfFinishedTasks.get() > 0)
                {
                    Thread.sleep(args.timerTickInMills)

                    for ((task, taskData) in tasksData)
                        watch(task, taskData)
                }
            }.start()
        }

        private fun watch(task : () -> Unit, taskData : TaskData?)
        {
            if (taskData != null)
            {
                val nowInMills = System.currentTimeMillis()
                val deltaTimeInMills = nowInMills - taskData.startedAtInMills
                if (deltaTimeInMills > args.loggingTimeoutInMills)
                {
                    val stackTrace = StringBuilder()
                    for (element in taskData.thread.stackTrace)
                        stackTrace.append(element.toString()).append('\n')

                    args.logger.invoke("Thread is hanging for ${deltaTimeInMills}ms!\n$stackTrace")
                }

                if (deltaTimeInMills > args.crashTimeoutInMills)
                {
                    try
                    {
                        val stackTrace = StringBuilder()
                        for (element in taskData.thread.stackTrace)
                            stackTrace.append(element.toString()).append('\n')

                        args.logger.invoke("App will crash because thread is hanging for ${deltaTimeInMills}ms!\n$stackTrace")

                        if (args.crasher!=null)
                            args.crasher.invoke()
                        else exitProcess(-1)
                    }
                    finally
                    {
                        tasksData.remove(task)
                        numberOfFinishedTasks.decrementAndGet()
                    }
                }
            }
        }

        fun watched(task : () -> Unit) : () -> Unit = {
            try
            {
                tasksData[task] = TaskData()
                task()
            }
            finally
            {
                tasksData.remove(task)
                numberOfFinishedTasks.decrementAndGet()
            }
        }
    }
}
