package ro.andob.rapidroid.future

import ro.andob.rapidroid.Rapidroid
import ro.andob.rapidroid.thread.UIThreadRunner
import java.util.concurrent.ThreadPoolExecutor

class Future<RESULT>
{
    @Volatile private var onSuccess : ((RESULT) -> Unit)? = null
    @Volatile private var onError : ((Throwable) -> Unit)? = null
    @Volatile private var onAny : (() -> Unit)? = null

    fun onSuccess(onSuccess : (RESULT) -> Unit) = also { this.onSuccess = onSuccess }
    fun onError(onError : (Throwable) -> Unit) = also { this.onError = onError }
    fun onAny(onAny : () -> Unit) = also { this.onAny = onAny }

    constructor(supplier : () -> RESULT) : this(supplier, FutureThreadPoolExecutors.DEFAULT)

    constructor
    (
        supplier : () -> RESULT,
        threadPoolExecutor : ThreadPoolExecutor,
    )
    {
        threadPoolExecutor.execute {
            try
            {
                val startTimestampInMills = System.currentTimeMillis()
                val result = supplier.invoke()
                val stopTimestampInMills = System.currentTimeMillis()

                //hack: avoid race conditions
                val minThreadExecutionTime = 10L
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

    fun withCancellationToken(cancellationToken : CancellationToken) : Future<RESULT>
    {
        cancellationToken.addCancellationListener {
            this.onSuccess = null
            this.onError = null
            this.onAny = null
        }

        return this
    }

    private fun callOnSuccess(result : RESULT)
    {
        UIThreadRunner.runOnUIThread {
            onAny?.invoke()
            onSuccess?.invoke(result)

            onAny = null
            onSuccess = null
            onError = null
        }
    }

    private fun callOnError(ex : Throwable)
    {
        UIThreadRunner.runOnUIThread {
            onAny?.invoke()
            onError?.invoke(ex)

            onAny = null
            onSuccess = null
            onError = null
        }
    }
}
