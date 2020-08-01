package ro.andreidobrescu.slcd4a

object SLCD4A
{
    @JvmStatic
    var exceptionLogger = object : ExceptionLogger {
        override fun log(ex: Throwable) {
            ex.printStackTrace()
        }
    }

    //todo convert to fun interface on kotlin 1.4
    interface ExceptionLogger
    {
        fun log(ex : Throwable)
    }
}
