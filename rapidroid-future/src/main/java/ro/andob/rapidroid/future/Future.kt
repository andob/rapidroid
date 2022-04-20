package ro.andob.rapidroid.future

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import ro.andob.rapidroid.*
import ro.andob.rapidroid.thread.UIThreadRunner
import java.util.LinkedList
import java.util.Queue

class Future<RESULT>(resultSupplier : Supplier<RESULT>)
{
    private val onSuccess : Queue<(RESULT) -> Unit> = LinkedList()
    @JvmSynthetic fun onSuccess(onSuccess : (RESULT) -> Unit) = apply { this.onSuccess.add(onSuccess) }
    fun onSuccess(onSuccess : Consumer<RESULT>) = apply { this.onSuccess.add { onSuccess.accept(it) } }
    fun onSuccess(onSuccess : Procedure) = apply { this.onSuccess.add { onSuccess.call() } }

    private val onError : Queue<(Throwable) -> Unit> = LinkedList()
    @JvmSynthetic fun onError(onError : (Throwable) -> Unit) = apply { this.onError.add(onError) }
    fun onError(onError : Consumer<Throwable>) = apply { this.onError.add { onError.accept(it) } }

    private val onAny : Queue<() -> Unit> = LinkedList()
    @JvmSynthetic fun onAny(onAny : () -> Unit) = apply { this.onAny.add(onAny) }
    fun onAny(onAny : Procedure) = apply { this.onAny.add { onAny.call() } }

    private fun Queue<() -> Unit>.invoke() { while(!isEmpty()) remove().invoke() }
    private fun <T> Queue<(T) -> Unit>.invoke(arg : T) { while(!isEmpty()) remove().invoke(arg) }

    init
    {
        FutureThreadPoolExecutors.DEFAULT.execute {
            try
            {
                val startTimestampInMills = System.currentTimeMillis()
                val result = resultSupplier.get()
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

    fun withLifecycleOwner(lifecycleOwner : LifecycleOwner) : Future<RESULT> = apply {
        lifecycleOwner.lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source : LifecycleOwner, event : Lifecycle.Event) {
                if (event==Lifecycle.Event.ON_DESTROY) {
                    onAny.clear()
                    onError.clear()
                    onSuccess.clear()
                }
            }
        })
    }

    fun <LIFECYCLE_OWNER : LifecycleOwner> withLifecycleOwner(loadingViewHandler : LoadingViewHandler<LIFECYCLE_OWNER>) = apply {

        UIThreadRunner.runOnUIThread { loadingViewHandler.showLoadingView.accept(loadingViewHandler.lifecycleOwner) }
        onAny.add { loadingViewHandler.hideLoadingView.accept(loadingViewHandler.lifecycleOwner) }

        loadingViewHandler.lifecycleOwner.lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source : LifecycleOwner, event : Lifecycle.Event) {
                if (event==Lifecycle.Event.ON_DESTROY) {
                    onAny.clear()
                    onError.clear()
                    onSuccess.clear()
                }
            }
        })

        loadingViewHandler.cancellationToken?.addCancellationListener {
            onAny.clear()
            onError.clear()
            onSuccess.clear()
        }
    }

    private fun callOnSuccess(result : RESULT)
    {
        UIThreadRunner.runOnUIThread {
            onAny.invoke()
            onSuccess.invoke(result)
            onError.clear()
        }
    }

    private fun callOnError(ex : Throwable)
    {
        UIThreadRunner.runOnUIThread {
            onAny.invoke()
            onError.invoke(ex)
            onSuccess.clear()
        }
    }
}
