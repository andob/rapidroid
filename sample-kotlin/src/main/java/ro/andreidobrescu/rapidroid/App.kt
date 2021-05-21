package ro.andreidobrescu.rapidroid

import android.app.Application

class App : Application()
{
    override fun onCreate()
    {
        super.onCreate()

        Rapidroid.exceptionLogger = object : Rapidroid.ExceptionLogger {
            override fun log(ex : Throwable) = ex.printStackTrace()
        }
    }
}
