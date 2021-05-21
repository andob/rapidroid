package ro.andob.rapidroid.actor

import ro.andob.rapidroid.thread.ThreadIsRunningFlag
import ro.andob.rapidroid.thread.ThreadRunner
import java.util.concurrent.LinkedBlockingQueue

abstract class Actor<EVENT>
{
    private val isRunning = ThreadIsRunningFlag()
    fun isRunning() = isRunning.get()

    private val eventQueue = LinkedBlockingQueue<EVENT>()

    fun enqueueEvent(event : EVENT)
    {
        eventQueue.add(event)

        ThreadRunner.runIfNotAlreadyRunning(::consumeEventQueue, isRunning)
    }

    private fun consumeEventQueue()
    {
        while (!eventQueue.isEmpty())
        {
            handleEvent(eventQueue.remove())
        }
    }

    abstract fun handleEvent(event : EVENT)
}
