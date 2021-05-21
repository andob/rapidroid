package ro.andreidobrescu.rapidroid.functional_interfaces

import ro.andreidobrescu.rapidroid.rapidroid

fun _try(lambda : Procedure) = object : Procedure {
    override fun invoke() {
        try { lambda.invoke() }
        catch (ex : Exception) { rapidroid.exceptionLogger.log(ex) }
    }
}

fun <T> _try(lambda : Consumer<T>) = object : Consumer<T> {
    override fun invoke(arg : T) {
        try { lambda.invoke(arg) }
        catch (ex : Exception) { rapidroid.exceptionLogger.log(ex) }
    }
}

fun <T> _try(lambda : Supplier<T>) = object : Supplier<T?> {
    override fun invoke() : T? {
        try { return lambda.invoke() }
        catch (ex : Exception) { rapidroid.exceptionLogger.log(ex) }
        return null
    }
}
