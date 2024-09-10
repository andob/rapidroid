package ro.industrialaccess.rapidroid.sample

import ro.andob.rapidroid.Rapidroid
import android.app.Application
import android.os.Process

class SampleApp : Application()
{
    override fun onCreate()
    {
        super.onCreate()

        Rapidroid.exceptionLogger = Rapidroid.ExceptionLogger { ex ->
            ExceptionLogger.enqueueEvent(ExceptionLogger.LogEvent(ex))
        }

        Thread.setDefaultUncaughtExceptionHandler { _, ex ->
            ExceptionLogger.enqueueEvent(ExceptionLogger.LogEvent(ex))
            Process.killProcess(Process.myPid())
        }
    }
}
