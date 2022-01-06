package ro.andob.rapidroid

import java.util.*

class CancellationToken
{
    private val cancellationListeners : Queue<() -> (Unit)> = LinkedList()

    @JvmSynthetic
    fun addCancellationListener(listener : () -> (Unit))
    {
        cancellationListeners.add(listener)
    }

    fun addCancellationListener(listener : Procedure)
    {
        cancellationListeners.add { listener.call() }
    }

    fun notifyCancellation()
    {
        while (!cancellationListeners.isEmpty())
            cancellationListeners.remove().invoke()
    }
}
