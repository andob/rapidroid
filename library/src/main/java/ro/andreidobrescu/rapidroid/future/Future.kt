package ro.andreidobrescu.rapidroid.future

import ro.andreidobrescu.rapidroid.Run
import ro.andreidobrescu.rapidroid.Rapidroid
import ro.andreidobrescu.rapidroid.functional_interfaces.*
import ro.andreidobrescu.rapidroid.onUiThread
import kotlin.concurrent.thread

class Future<RESULT>
{
    @Volatile private var onSuccess : Consumer<RESULT>? = null
    @Volatile private var onError : Consumer<Throwable>? = null
    @Volatile private var onAny : Procedure? = null

    constructor(task : Supplier<RESULT>)
    {
        thread(start = true) {
            try
            {
                val startTimestampInMills=System.currentTimeMillis()
                val result=task.invoke()
                val stopTimestampInMills=System.currentTimeMillis()

                //hack: avoid race conditions
                val minThreadExecutionTime=10L
                if (stopTimestampInMills-startTimestampInMills<minThreadExecutionTime)
                    Thread.sleep(minThreadExecutionTime)

                callOnSuccess(result)
            }
            catch (ex : Throwable)
            {
                Rapidroid.exceptionLogger.log(ex)

                callOnError(ex)
            }
        }
    }

    fun onSuccess(onSuccess : Procedure) : Future<RESULT> =
        onSuccess(onSuccess.toConsumer())

    fun onSuccess(onSuccess : Consumer<RESULT>) : Future<RESULT>
    {
        this.onSuccess=onSuccess
        return this
    }

    fun onError(onError : Consumer<Throwable>) : Future<RESULT>
    {
        this.onError=onError
        return this
    }

    fun onAny(onAny : Procedure) : Future<RESULT>
    {
        this.onAny=onAny
        return this
    }

    fun withCancellationToken(cancellationToken : CancellationToken) : Future<RESULT>
    {
        cancellationToken.addCancellationListener {
            this.onSuccess=null
            this.onError=null
            this.onAny=null
        }

        return this
    }

    private fun callOnSuccess(result : RESULT)
    {
        Run.onUiThread {
            onAny?.invoke()
            onSuccess?.invoke(result)

            onAny=null
            onSuccess=null
            onError=null
        }
    }

    private fun callOnError(ex : Throwable)
    {
        Run.onUiThread {
            onAny?.invoke()
            onError?.invoke(ex)

            onAny=null
            onSuccess=null
            onError=null
        }
    }
}
