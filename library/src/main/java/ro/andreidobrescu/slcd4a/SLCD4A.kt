package ro.andreidobrescu.slcd4a

object SLCD4A
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
