package assets

import Data.AcceptCommissionResponse
import Data.EmailChangeRequest
import Data.GetOldPasswordResponse
import Data.HelpRequest
import Data.HelpResponse
import Data.IdentifiedData
import Data.IdentifiedResponse
import Data.LoginRequest
import Data.NameChangeRequest
import Data.PasswordChangeRequest
import Data.SignupRequest
import Data.UploadImageResponse
import Data.UploadResponse
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
    @PUT("setting/newName")
    suspend fun name(@Query("Phone")account:String?,@Body nameRequest: NameChangeRequest):Response<UploadResponse>
    @GET("setting/oldPassword")
    suspend fun getOldPassword(@Query("Phone")account:String?): Response<GetOldPasswordResponse>
    @PUT("setting/newPassword")
    suspend fun password(@Query("Phone")account:String?,@Body passwordChangeRequest: PasswordChangeRequest):Response<UploadResponse>
    @PUT("setting/newEmail")
    suspend fun email(@Query("Phone")account:String?,@Body emailChangeRequest: EmailChangeRequest): Response<UploadResponse>
    @POST("gemini/")
    suspend fun identify(@Body identifiedData: IdentifiedData):Response<IdentifiedResponse>
    @POST("sendRequest")
    suspend fun sendRequest(@Body helpRequest: IdentifiedData):Response<IdentifiedResponse>
    @GET("getRequest")
    suspend fun getRequire():Response<HelpResponse>
    @PUT("acceptCommission")
    suspend fun acceptCommission(@Query("id")id:String, @Query ("Phone")account: String ):Response<AcceptCommissionResponse>
    @PUT("cancelCommission")
    suspend fun cancelCommission(@Query("id")id:String, @Query ("Phone")account: String ):Response<AcceptCommissionResponse>
    @Multipart
    @POST("yolo/")
    suspend fun uploadImage(@Part image: MultipartBody.Part): Response<UploadImageResponse?>
}

