package ro.andreidobrescu.slcd4a.functional_interfaces

//todo remove this file in Kotlin 1.4

fun (() -> (Unit)).toProcedure() : Procedure
{
    val lambda=this
    return object : Procedure {
        override fun invoke() {
            lambda.invoke()
        }
    }
}

fun <T> (() -> (T)).toSupplier() : Supplier<T>
{
    val lambda=this
    return object : Supplier<T> {
        override fun invoke() : T {
            return lambda.invoke()
        }
    }
}

fun <T> ((T) -> (Unit)).toConsumer() : Consumer<T>
{
    val lambda=this
    return object : Consumer<T> {
        override fun invoke(arg : T) {
            lambda.invoke(arg)
        }
    }
}

fun Procedure.toSupplier() : Supplier<Unit>
{
    val lambda=this
    return object : Supplier<Unit> {
        override fun invoke() {
            lambda.invoke()
        }
    }
}

fun <T> Procedure.toConsumer() : Consumer<T>
{
    val lambda=this
    return object : Consumer<T> {
        override fun invoke(arg : T) {
            lambda.invoke()
        }
    }
}

fun Procedure.toRunnable() : Runnable
{
    val lambda=this
    return Runnable {
        lambda.invoke()
    }
}
