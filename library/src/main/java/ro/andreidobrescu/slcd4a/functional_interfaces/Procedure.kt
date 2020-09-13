package ro.andreidobrescu.slcd4a.functional_interfaces

interface Procedure
{
    @Throws(Exception::class)
    fun invoke()
}
