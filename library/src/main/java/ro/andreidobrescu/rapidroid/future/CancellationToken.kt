package ro.andreidobrescu.rapidroid.future

import java.util.*

class CancellationToken
{
    private val cancellationListeners : Queue<() -> (Unit)> = LinkedList<() -> (Unit)>()

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
