package assets

import Data.HelpRequest
import Data.HelpResponse
import Data.IdentifiedData
import Data.IdentifiedResponse
import Data.ImportResponse
import Data.LoginRequest
import Data.SignupRequest
import Data.UpdateResponse
import Data.UploadImageResponse
import Data.User
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

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
    @GET("getunity2/")
    suspend fun getRequire():Response<HelpResponse>
    @GET("getunity1/")
    suspend fun sendRequire():Response<HelpRequest>
    @Multipart
    @POST("object/")
    suspend fun uploadImage(@Part image: MultipartBody.Part): Response<UploadImageResponse?>
    @Multipart
    @POST("continue/")
    suspend fun uploadFocusImage(@Part image: MultipartBody.Part): Response<IdentifiedResponse?>
    @Multipart
    @POST("help/")
    suspend fun uploadHelpImage(@Part image: MultipartBody.Part): Response<UploadImageResponse?>

    @Multipart
    @POST("updateimg/")
    suspend fun importImage(@Part("filename")filename:RequestBody,@Part image: MultipartBody.Part): Response<ImportResponse?>
}

