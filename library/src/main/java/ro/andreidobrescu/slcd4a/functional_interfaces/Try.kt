package ro.andreidobrescu.slcd4a.functional_interfaces

import ro.andreidobrescu.slcd4a.SLCD4A

fun _try(lambda : Procedure) = object : Procedure {
    override fun invoke() {
        try { lambda.invoke() }
        catch (ex : Exception) { SLCD4A.exceptionLogger.log(ex) }
    }
}

fun <T> _try(lambda : Consumer<T>) = object : Consumer<T> {
    override fun invoke(arg : T) {
        try { lambda.invoke(arg) }
        catch (ex : Exception) { SLCD4A.exceptionLogger.log(ex) }
    }
}

fun <T> _try(lambda : Supplier<T>) = object : Supplier<T?> {
    override fun invoke() : T? {
        try { return lambda.invoke() }
        catch (ex : Exception) { SLCD4A.exceptionLogger.log(ex) }
        return null
    }
}
