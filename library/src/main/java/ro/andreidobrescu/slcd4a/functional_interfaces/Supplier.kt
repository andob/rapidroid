package ro.andreidobrescu.slcd4a.functional_interfaces

import java.lang.Exception

//todo convert to interface fun on Kotlin 1.4
interface Supplier<T>
{
    @Throws(Exception::class)
    fun invoke() : T
}
