package ro.andreidobrescu.slcd4a.functional_interfaces

import java.lang.Exception

//todo convert to interface fun in Kotlin 1.4
interface Consumer<T>
{
    @Throws(Exception::class)
    fun invoke(arg : T)
}
