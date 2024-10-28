package assets

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

private val client = OkHttpClient.Builder()
    .connectTimeout(100, TimeUnit.SECONDS) // 連接超時為100秒
    .readTimeout(100, TimeUnit.SECONDS)    // 讀取超時為100秒
    .addInterceptor(logging)
    .build()

private val retrofit=
    Retrofit.Builder()
        .addConverterFactory(ScalarsConverterFactory.create())
        .baseUrl("http://192.168.149.88:80") // 這裡放你的ip
        .client(client)
        .build()
object ArduinoInstance {
    val arduinoApi: ArduinoApi by lazy {
        retrofit.create(ArduinoApi::class.java)
    }
}