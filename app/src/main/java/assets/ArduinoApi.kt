package assets

import Data.ArduinoRequire
import Data.ArduinoRequireResponse
import Data.Distance
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ArduinoApi {
    @GET("")
    suspend fun getDistance():Response<Distance>
    @POST("")
    suspend fun require(@Body arduinoRequire: ArduinoRequire):Response<ArduinoRequireResponse>
}