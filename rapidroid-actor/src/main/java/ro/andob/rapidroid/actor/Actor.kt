package ro.andob.rapidroid.actor

import ro.andob.rapidroid.Procedure
import ro.andob.rapidroid.thread.ThreadIsRunningFlag
import ro.andob.rapidroid.thread.ThreadRunner
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.Phaser

abstract class Actor<EVENT>
{
    private val threadWasStarted = ThreadIsRunningFlag()
    private val phaser = Phaser(1)

    private val eventQueue = LinkedBlockingQueue<EVENT>(Int.MAX_VALUE)

    fun enqueueEvent(event : EVENT)
    {
        eventQueue.add(event)
        phaser.register()

        val eventQueueConsumer = Procedure {
            while(true) {
                try { handleEvent(eventQueue.take()!!) }
                finally { phaser.arriveAndDeregister() }
            }
        }

        ThreadRunner.runIfNotAlreadyRunning(eventQueueConsumer, threadWasStarted)
    }

    fun awaitCompletion()
    {
        phaser.arriveAndAwaitAdvance()
    }

    abstract fun handleEvent(event : EVENT)
}
