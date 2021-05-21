package ro.andob.rapidroid.thread

import android.os.Handler
import android.os.Looper
import ro.andob.rapidroid.Procedure
import ro.andob.rapidroid.Rapidroid

object UIThreadRunner
{
    @JvmStatic
    fun runOnUIThread(procedure : Procedure)
    {
        try { Handler(Looper.getMainLooper()).post { procedure.call() } }
        catch (ex : Throwable) { Rapidroid.exceptionLogger.log(ex) }
    }
}
