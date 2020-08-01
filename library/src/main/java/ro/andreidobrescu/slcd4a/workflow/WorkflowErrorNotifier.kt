package ro.andreidobrescu.slcd4a.workflow

import java.util.concurrent.atomic.AtomicReference

class WorkflowErrorNotifier
{
    private val errorHolder = AtomicReference<Throwable?>(null)

    fun notify(error : Throwable)
    {
        errorHolder.set(error)
    }

    fun throwOnError()
    {
        val error=errorHolder.get()
        if (error!=null)
            throw error
    }
}
