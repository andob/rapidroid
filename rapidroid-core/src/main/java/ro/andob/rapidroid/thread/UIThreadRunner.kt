package ro.andob.rapidroid.thread

import android.os.Handler
import android.os.Looper
import ro.andob.rapidroid.Rapidroid

object UIThreadRunner
{
    @JvmStatic
    fun runOnUIThread(runnable : Runnable)
    {
        try { Handler(Looper.getMainLooper()).post(runnable) }
        catch (ex : Throwable) { Rapidroid.exceptionLogger.log(ex) }
    }
}
