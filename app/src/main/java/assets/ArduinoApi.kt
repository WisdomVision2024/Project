package assets

import retrofit2.http.GET

interface ArduinoApi {
    @GET("/getPosition.asp?ID=1001")
    suspend fun getDistance():String
}