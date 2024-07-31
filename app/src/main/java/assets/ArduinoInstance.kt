package assets

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

private val client = OkHttpClient.Builder()
    .connectTimeout(60, TimeUnit.SECONDS) // 連接超時為60秒
    .readTimeout(60, TimeUnit.SECONDS)    // 讀取超時為60秒
    .addInterceptor(logging)
    .build()

private val retrofit=
    Retrofit.Builder()
        .addConverterFactory(ScalarsConverterFactory.create())
        .baseUrl("http://172.20.10.2") // 這裡放你的ip
        .build()
object ArduinoInstance {
    val arduinoApi: ArduinoApi by lazy {
        retrofit.create(ArduinoApi::class.java)
    }
}