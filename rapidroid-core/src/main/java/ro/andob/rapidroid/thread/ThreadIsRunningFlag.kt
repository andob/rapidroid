package ro.andob.rapidroid.thread

import java.util.concurrent.atomic.AtomicBoolean

class ThreadIsRunningFlag
{
    private val isRunning = AtomicBoolean(false)

    fun get() = isRunning.get()

    internal fun set(isRunning : Boolean)
    {
        this.isRunning.set(isRunning)
    }
}
