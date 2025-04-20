package ro.industrialaccess.rapidroid.sample

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ro.andob.rapidroid.Run
import ro.andob.rapidroid.future.LoadingViewHandler
import ro.industrialaccess.rapidroid.sample.SyncService.Companion.CACHE_FILE_NAME
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity()
{
    private val getDataFromAPIButton get() = findViewById<Button>(R.id.getDataFromAPIButton)
    private val getDataFromCacheButton get() = findViewById<Button>(R.id.getDataFromCacheButton)
    private val loadingView get() = findViewById<View>(R.id.loadingView)

    override fun onCreate(savedInstanceState : Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getDataFromAPIButton.setOnClickListener { onGetDataFromAPIButtonClicked() }
        getDataFromCacheButton.setOnClickListener { onGetDataFromCacheButtonClicked() }
    }

    override fun onResume()
    {
        super.onResume()
        startService(Intent(this, SyncService::class.java))
    }

    private fun onGetDataFromAPIButtonClicked()
    {
        Run.future { ApiClient.Instance.getData().execute().body()!! }
            .withLoadingViewHandler(getLoadingViewHandler())
            .onError { ex -> showToast(ex.message?:"") }
            .onSuccess { data -> showData(data) }
    }

    private fun onGetDataFromCacheButtonClicked()
    {
        Run.future {
            val dataJson = getFileStreamPath(CACHE_FILE_NAME).readText()
            val dataType = object : TypeToken<List<Item>>() {}
            return@future Gson().fromJson(dataJson, dataType)
        }
        .withLoadingViewHandler(getLoadingViewHandler())
        .onError { ex -> showToast(ex.message?:"") }
        .onSuccess { data -> showData(data) }
    }

    private fun getLoadingViewHandler() : LoadingViewHandler
    {
        return LoadingViewHandler(lifecycleOwner = this,
            showLoadingView = { loadingView.visibility = View.VISIBLE },
            hideLoadingView = { loadingView.visibility = View.GONE })
    }

    private fun showToast(message : String)
    {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun showData(data : List<Item>)
    {
        val listItemLayout = android.R.layout.simple_list_item_1
        val listItems = data.map { it.toString() }

        AlertDialog.Builder(this).setTitle("DATA")
            .setAdapter(ArrayAdapter(this, listItemLayout, listItems)) { _, _ -> }
            .show()
    }
}
