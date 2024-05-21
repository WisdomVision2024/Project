package assets

import Data.EmailChangeRequest
import Data.LoginRequest
import Data.LoginResponse
import Data.NameChangeRequest
import Data.PasswordChangeRequest
import Data.SignupRequest
import Data.SignupResponse
import Data.UpdateResponse
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT

interface ApiService {
    @GET("Login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>
    @POST("Signup")
    suspend fun signup(@Body signupRequest: SignupRequest):Response<SignupResponse>
    @PUT("Setting")
    suspend fun name(@Body nameRequest: NameChangeRequest):Response<UpdateResponse>
    @GET@PUT("Setting")
    suspend fun password(@Body passwordChangeRequest: PasswordChangeRequest):Response<UpdateResponse>
    @PUT("Setting")
    suspend fun email(@Body emailChangeRequest: EmailChangeRequest): Response<UpdateResponse>
}
private class Api(){
    private val retrofit = Retrofit.Builder()
        .baseUrl("YOUR_BASE_URL_HERE")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}
