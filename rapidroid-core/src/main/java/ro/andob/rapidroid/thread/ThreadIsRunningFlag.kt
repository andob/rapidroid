package ro.andob.rapidroid.thread

import java.util.concurrent.atomic.AtomicBoolean

class ThreadIsRunningFlag
{
    private val isRunning = AtomicBoolean(false)

    fun get() = isRunning.get()

    internal fun compareAndSet(expectedValue : Boolean, newValue : Boolean) =
        isRunning.compareAndSet(expectedValue, newValue)

    internal fun set(isRunning : Boolean)
    {
        this.isRunning.set(isRunning)
    }
}
