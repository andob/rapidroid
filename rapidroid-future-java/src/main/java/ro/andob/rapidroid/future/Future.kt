package ro.andob.rapidroid.future

import ro.andob.rapidroid.Rapidroid
import ro.andob.rapidroid.thread.UIThreadRunner
import java.util.function.Consumer
import java.util.function.Supplier
import kotlin.concurrent.thread

class Future<RESULT>(supplier : Supplier<RESULT>)
{
    @Volatile private var onSuccess : Consumer<RESULT>? = null
    @Volatile private var onError : Consumer<Throwable>? = null
    @Volatile private var onAny : Runnable? = null

    fun onSuccess(onSuccess : Runnable) = also { this.onSuccess = Consumer { onSuccess.run() } }
    fun onSuccess(onSuccess : Consumer<RESULT>) = also { this.onSuccess = onSuccess }
    fun onError(onError : Consumer<Throwable>) = also { this.onError = onError }
    fun onAny(onAny : Runnable) = also { this.onAny = onAny }

    init
    {
        thread(start = true) {
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
            onAny?.run()
            onSuccess?.accept(result)

            onAny = null
            onSuccess = null
            onError = null
        }
    }

    private fun callOnError(ex : Throwable)
    {
        UIThreadRunner.runOnUIThread {
            onAny?.run()
            onError?.accept(ex)

            onAny = null
            onSuccess = null
            onError = null
        }
    }
}
