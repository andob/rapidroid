package ro.andob.rapidroid.future

import ro.andob.rapidroid.CancellationToken
import ro.andob.rapidroid.Procedure
import ro.andob.rapidroid.Rapidroid
import ro.andob.rapidroid.Supplier
import ro.andob.rapidroid.Consumer
import ro.andob.rapidroid.thread.UIThreadRunner

class Future<RESULT>(supplier : Supplier<RESULT>)
{
    @Volatile private var onSuccess : ((RESULT) -> Unit)? = null
    @JvmSynthetic fun onSuccess(onSuccess : (RESULT) -> Unit) = also { this.onSuccess = onSuccess }
    fun onSuccess(onSuccess : Consumer<RESULT>) = also { this.onSuccess = { onSuccess.accept(it) } }
    fun onSuccess(onSuccess : Procedure) = also { this.onSuccess = { onSuccess.call()} }

    @Volatile private var onError : ((Throwable) -> Unit)? = null
    @JvmSynthetic fun onError(onError : (Throwable) -> Unit) = also { this.onError = onError }
    fun onError(onError : Consumer<Throwable>) = also { this.onError = { onError.accept(it) } }

    @Volatile private var onAny : (() -> Unit)? = null
    @JvmSynthetic fun onAny(onAny : () -> Unit) = also { this.onAny = onAny }
    fun onAny(onAny : Procedure) = also { this.onAny = { onAny.call() } }

    init
    {
        FutureThreadPoolExecutors.DEFAULT.execute {
            try
            {
                val startTimestampInMills = System.currentTimeMillis()
                val result = supplier.get()
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
