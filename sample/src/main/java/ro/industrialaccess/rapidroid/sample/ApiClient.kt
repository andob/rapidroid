package ro.industrialaccess.rapidroid.sample

import java.io.Serializable
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiClient
{
    companion object
    {
        @JvmStatic
        val Instance : ApiClient = Retrofit.Builder()
            .baseUrl("https://raw.githubusercontent.com/")
            .addConverterFactory(GsonConverterFactory.create(Gson()))
            .client(OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().also { interceptor ->
                    interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
                }).build())
            .build()
            .create(ApiClient::class.java)
    }

    @GET("/andob/rapidroid/master/sample/data.json")
    fun getData() : Call<List<Item>>

    @GET("/andob/rapidroid/master/sample/data.json")
    fun logException(
        @Query("message") message : String,
        @Query("stackTrace") stackTrace : String,
    ) : Call<List<Item>>
}

data class Item
(
    val title : String,
    val contents : String,
) : Serializable
{
    override fun toString() = title
}
