package assets

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private val retrofit=
    Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl("http://163.13.201.104:8080/") // 這裡放你的伺服器 URL
        .client(client)
        .build()
object ArduinoInstance {
    val arduinoApi: ArduinoApi by lazy {
        retrofit.create(ArduinoApi::class.java)
    }
}