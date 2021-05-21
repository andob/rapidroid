package ro.andob.rapidroid

object Rapidroid
{
    fun interface ExceptionLogger { fun log(ex : Throwable) }

    @JvmStatic
    var exceptionLogger = ExceptionLogger { ex -> ex.printStackTrace() }
}
