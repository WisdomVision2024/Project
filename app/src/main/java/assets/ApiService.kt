package assets

import Data.AcceptCommissionResponse
import Data.HelpResponse
import Data.IdentifiedData
import Data.IdentifiedResponse
import Data.LoginRequest
import Data.SignupRequest
import Data.UpdateResponse
import Data.UploadImageResponse
import Data.User
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiService {
    @POST("login/")
    suspend fun login(@Body loginRequest: LoginRequest): Response<ResponseBody>
    @POST("signup/")
    suspend fun signup(@Body signupRequest: SignupRequest):Response<ResponseBody>
    @POST("update/")
    suspend fun update(@Body user: User):Response<UpdateResponse>
    @POST("gemini/")
    suspend fun identify(@Body identifiedData: IdentifiedData):Response<IdentifiedResponse>
    @POST("focus/")
    suspend fun focusIdentify(@Body identifiedData: IdentifiedData):Response<IdentifiedResponse>
    @GET("getunity/")
    suspend fun getRequire():Response<HelpResponse>
    @Multipart
    @POST("object/")
    suspend fun uploadImage(@Part image: MultipartBody.Part): Response<UploadImageResponse?>
    @Multipart
    @POST("continue/")
    suspend fun uploadFocusImage(@Part image: MultipartBody.Part): Response<IdentifiedResponse?>
    @Multipart
    @POST("help/")
    suspend fun uploadHelpImage(@Part image: MultipartBody.Part): Response<UploadImageResponse?>
}

