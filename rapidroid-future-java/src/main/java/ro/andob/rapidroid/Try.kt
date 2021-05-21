package ro.andob.rapidroid

import java.util.function.Consumer
import java.util.function.Supplier

fun _try(lambda : Runnable) = Runnable {
    try
    {
        lambda.run()
    }
    catch (ex : Exception)
    {
        Rapidroid.exceptionLogger.log(ex)
    }
}

fun <T> _try(lambda : Consumer<T>) = Consumer<T> { arg ->
    try
    {
        lambda.accept(arg)
    }
    catch (ex : Exception)
    {
        Rapidroid.exceptionLogger.log(ex)
    }
}

fun <T> _try(lambda : Supplier<T>) = Supplier<T?> {
    try
    {
        return@Supplier lambda.get()
    }
    catch (ex : Exception)
    {
        Rapidroid.exceptionLogger.log(ex)
        return@Supplier null
    }
}
