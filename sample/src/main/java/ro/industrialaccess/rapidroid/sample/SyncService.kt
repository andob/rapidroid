package ro.industrialaccess.rapidroid.sample

import com.google.gson.Gson
import ro.andob.rapidroid.Run
import ro.andob.rapidroid.thread.ThreadIsRunningFlag
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

class SyncService : Service()
{
    companion object
    {
        @JvmStatic
        private val isRunning = ThreadIsRunningFlag()

        private const val TAG = "SyncService"

        const val CACHE_FILE_NAME = "data.json"
    }

    override fun onBind(intent : Intent?) : IBinder? = null

    override fun onCreate()
    {
        super.onCreate()

        //run thread only if it is not already running
        Run.thread(threadIsRunningFlag = isRunning) {
            Run.workflow {
                sequential {
                    task { Log.i(TAG, "Do something") }
                    parallel {
                        task { Log.i(TAG, "Do something") }
                        task { Log.i(TAG, "Do something") }
                        task { Log.i(TAG, "Do something") }
                        task { Log.i(TAG, "Do something") }
                    }
                    task {
                        val items = ApiClient.Instance.getData().execute().body()!!
                        getFileStreamPath(CACHE_FILE_NAME).writeText(Gson().toJson(items))
                    }
                }
            }
        }
    }
}
