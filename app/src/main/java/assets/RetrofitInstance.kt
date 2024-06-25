package assets

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory


private val retrofit=
    Retrofit.Builder()
        .baseUrl("http://163.13.201.104:8080/") // 這裡放你的伺服器 URL
        .addConverterFactory(GsonConverterFactory.create())
        .addConverterFactory(ScalarsConverterFactory.create())
        .build()

object RetrofitInstance {
    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}