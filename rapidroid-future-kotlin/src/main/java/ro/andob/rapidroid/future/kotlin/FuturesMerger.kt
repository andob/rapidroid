package ro.andob.rapidroid.future.kotlin

import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference

object FuturesMerger
{
    @JvmStatic
    fun <T, Q> merge(firstFuture : Future<T?>, secondFuture : Future<Q?>) : Future<Pair<T?, Q?>> = Future {

        val isFirstFutureRunning = AtomicBoolean(true)
        val isSecondFutureRunning = AtomicBoolean(true)
        val firstFutureResult = AtomicReference<T?>(null)
        val secondFutureResult = AtomicReference<Q?>(null)
        val firstFutureError = AtomicReference<Throwable>(null)
        val secondFutureError = AtomicReference<Throwable>(null)

        firstFuture
            .onSuccess { result -> firstFutureResult.set(result) }
            .onError { error -> firstFutureError.set(error) }
            .onAny { isFirstFutureRunning.set(false) }

        secondFuture
            .onSuccess { result -> secondFutureResult.set(result) }
            .onError { error -> secondFutureError.set(error) }
            .onAny { isSecondFutureRunning.set(false) }

        while (isFirstFutureRunning.get())
            Thread.sleep(1)
        while (isSecondFutureRunning.get())
            Thread.sleep(1)

        if (firstFutureError.get()!=null)
            throw firstFutureError.get()
        if (secondFutureError.get()!=null)
            throw secondFutureError.get()

        Pair(firstFutureResult.get(), secondFutureResult.get())
    }
}
