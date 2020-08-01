package ro.andreidobrescu.slcd4a.actor

import ro.andreidobrescu.slcd4a.Run
import ro.andreidobrescu.slcd4a.thread.ThreadIsRunningFlag
import ro.andreidobrescu.slcd4a.threadIfNotAlreadyRunning
import java.util.concurrent.LinkedBlockingQueue

abstract class Actor<EVENT>
{
    private val isRunning = ThreadIsRunningFlag()
    fun isRunning() = isRunning.get()

    private val eventQueue = LinkedBlockingQueue<EVENT>()

    fun enqueueEvent(event : EVENT)
    {
        eventQueue.add(event)

        Run.threadIfNotAlreadyRunning(
            threadIsRunningFlag = isRunning,
            task = {
                while (!eventQueue.isEmpty())
                {
                    val event=eventQueue.remove()
                    handleEvent(event)
                }
            })
    }

    abstract fun handleEvent(event : EVENT)
}
