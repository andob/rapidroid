package ro.andob.rapidroid.workflow

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
        val error = errorHolder.get()
        if (error != null)
            throw error
    }
}
