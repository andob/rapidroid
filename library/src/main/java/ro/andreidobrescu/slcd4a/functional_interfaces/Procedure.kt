package ro.andreidobrescu.slcd4a.functional_interfaces

//todo convert to fun interface on Kotlin 1.4
interface Procedure
{
    @Throws(Exception::class)
    fun invoke()
}
