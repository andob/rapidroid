package ro.andreidobrescu.rapidroid

object Rapidroid
{
    @JvmStatic
    var exceptionLogger = object : ExceptionLogger {
        override fun log(ex: Throwable) {
            ex.printStackTrace()
        }
    }

    interface ExceptionLogger
    {
        fun log(ex : Throwable)
    }
}
