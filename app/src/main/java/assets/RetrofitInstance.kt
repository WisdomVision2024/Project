package assets

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

val logging = HttpLoggingInterceptor().apply {
    setLevel(HttpLoggingInterceptor.Level.BODY)
}

// 配置 OkHttpClient 並添加 HttpLoggingInterceptor
private val client = OkHttpClient.Builder()
    .connectTimeout(10, TimeUnit.MINUTES) // 連接超時為10秒
    .readTimeout(10, TimeUnit.MINUTES)    // 讀取超時為10秒
    .addInterceptor(logging)
    .build()

private val retrofit=
    Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl("http://163.13.201.104:8080/") // 這裡放你的伺服器 URL
        .client(client)
        .build()

object RetrofitInstance {
    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}