package ro.andreidobrescu.slcd4a

import android.app.Application

class App : Application()
{
    override fun onCreate()
    {
        super.onCreate()

        SLCD4A.exceptionLogger=object : SLCD4A.ExceptionLogger {
            override fun log(ex : Throwable) {
                println(ex)
            }
        }
    }
}
