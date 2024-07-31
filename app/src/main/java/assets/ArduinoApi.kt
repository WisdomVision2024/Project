package assets

import retrofit2.http.GET

interface ArduinoApi {
    @GET("/")
    suspend fun getDistance():String
}