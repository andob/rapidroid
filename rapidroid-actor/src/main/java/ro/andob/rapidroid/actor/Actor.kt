package ro.andob.rapidroid.actor

import ro.andob.rapidroid.Procedure
import ro.andob.rapidroid.Rapidroid
import ro.andob.rapidroid.thread.ThreadIsRunningFlag
import ro.andob.rapidroid.thread.ThreadRunner
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.Phaser
import java.util.concurrent.atomic.AtomicBoolean

abstract class Actor<EVENT>
{
    private val threadWasStarted = ThreadIsRunningFlag()
    private val phaser = Phaser(1)
    private val isPaused = AtomicBoolean(false)

    private val eventQueue = LinkedBlockingQueue<EVENT>(Int.MAX_VALUE)

    private inline fun loopForever(block : () -> Unit) { while(true) block() }

    fun enqueueEvent(event : EVENT)
    {
        eventQueue.add(event)
        phaser.register()

        val eventQueueConsumer = Procedure {
            loopForever {
                if (isPaused.get())
                    Thread.sleep(1)

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

    fun awaitCompletionAndThen(lambda : () -> Unit)
    {
        pause()

        try
        {
            phaser.arriveAndAwaitAdvance()
            lambda()
        }
        finally
        {
            resume()
        }
    }

    fun pause() = isPaused.set(true)
    fun resume() = isPaused.set(false)

    abstract fun handleEvent(event : EVENT)
}
