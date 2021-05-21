package ro.andob.rapidroid.future

import ro.andob.rapidroid.Procedure
import ro.andob.rapidroid.Rapidroid

fun _try(lambda : Procedure) = Procedure {
    try
    {
        lambda.call()
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