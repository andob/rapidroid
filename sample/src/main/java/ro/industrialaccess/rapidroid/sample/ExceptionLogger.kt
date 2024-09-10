package ro.industrialaccess.rapidroid.sample

import ro.andob.rapidroid.actor.Actor
import android.util.Log

object ExceptionLogger : Actor<ExceptionLogger.LogEvent>()
{
    class LogEvent(val exception : Throwable)

    override fun handleEvent(event : LogEvent)
    {
        val message = event.exception.message?:""
        val stackTrace = Log.getStackTraceString(event.exception)
        ApiClient.Instance.logException(message, stackTrace).execute()

        event.exception.printStackTrace()
    }
}
