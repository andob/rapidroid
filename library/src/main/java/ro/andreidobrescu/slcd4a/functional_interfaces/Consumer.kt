package ro.andreidobrescu.slcd4a.functional_interfaces

import java.lang.Exception

interface Consumer<T>
{
    @Throws(Exception::class)
    fun invoke(arg : T)
}
