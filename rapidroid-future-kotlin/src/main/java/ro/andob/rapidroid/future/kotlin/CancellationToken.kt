package ro.andob.rapidroid.future.kotlin

import java.util.*

class CancellationToken
{
    private val cancellationListeners : Queue<() -> (Unit)> = LinkedList()

    fun addCancellationListener(listener : () -> (Unit))
    {
        cancellationListeners.add(listener)
    }

    fun notifyCancellation()
    {
        while (!cancellationListeners.isEmpty())
            cancellationListeners.remove().invoke()
    }
}
