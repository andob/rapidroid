package ro.andob.rapidroid.actor

import ro.andob.rapidroid.Procedure
import ro.andob.rapidroid.Rapidroid
import ro.andob.rapidroid.thread.ThreadIsRunningFlag
import ro.andob.rapidroid.thread.ThreadRunner
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.Phaser

abstract class Actor<EVENT>
{
    private val threadWasStarted = ThreadIsRunningFlag()
    private val phaser = Phaser(1)

    private val eventQueue = LinkedBlockingQueue<EVENT>(Int.MAX_VALUE)

    private inline fun loopForever(block : () -> Unit) { while(true) block() }

    fun enqueueEvent(event : EVENT)
    {
        eventQueue.add(event)
        phaser.register()

        val eventQueueConsumer = Procedure {
            loopForever {
                try { handleEvent(eventQueue.take()!!) }
                catch (ex : Throwable) { Rapidroid.exceptionLogger.log(ex) }
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
