package ro.andreidobrescu.rapidroid

import ro.andreidobrescu.rapidroid.functional_interfaces.toConsumer
import ro.andreidobrescu.rapidroid.functional_interfaces.toProcedure
import ro.andreidobrescu.rapidroid.functional_interfaces.toSupplier
import ro.andreidobrescu.rapidroid.future.Future
import ro.andreidobrescu.rapidroid.thread.ThreadIsRunningFlag

//Kotlin functional interfaces compatibility layer

/*
 * Threads
 */

fun Run.thread(task : () -> (Unit)) =
    Run.thread(task.toProcedure())

fun Run.thread(threadIsRunningFlag : ThreadIsRunningFlag, task : () -> (Unit)) =
    Run.thread(threadIsRunningFlag, task.toProcedure())

fun Run.threadIfNotAlreadyRunning(threadIsRunningFlag : ThreadIsRunningFlag, task : () -> (Unit)) =
    Run.threadIfNotAlreadyRunning(threadIsRunningFlag, task.toProcedure())

fun Run.onUiThread(task : () -> (Unit)) =
    Run.onUiThread(task.toProcedure())

/*
 * Futures
 */

fun <T> Run.async(task : () -> (T)) =
    Run.async(task.toSupplier())

fun <T> Future<T>.onAny(onAny : () -> (Unit)) =
    onAny(onAny.toProcedure())

fun <T> Future<T>.onSuccess(onSuccess : (T) -> (Unit)) =
    onSuccess(onSuccess.toConsumer())

fun <T> Future<T>.onError(onError : (Throwable) -> (Unit)) =
    onError(onError.toConsumer())
