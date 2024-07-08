package assets

import Data.EmailChangeRequest
import Data.GetOldPasswordResponse
import Data.HelpRequest
import Data.HelpResponse
import Data.IdentifiedData
import Data.IdentifiedResponse
import Data.LoginRequest
import Data.LoginResponse
import Data.NameChangeRequest
import Data.PasswordChangeRequest
import Data.SignupRequest
import Data.SignupResponse
import Data.UploadResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface ApiService {
    @POST("login/")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>
    @POST("signup/")
    suspend fun signup(@Body signupRequest: SignupRequest):Response<SignupResponse>
    @PUT("Setting/newName")
    suspend fun name(@Body nameRequest: NameChangeRequest):Response<UploadResponse>
    @GET("Setting/oldPassword")
    suspend fun getOldPassword(): Response<GetOldPasswordResponse>
    @PUT("Setting/newPassword")
    suspend fun password(@Body passwordChangeRequest: PasswordChangeRequest):Response<UploadResponse>
    @PUT("Setting/newEmail")
    suspend fun email(@Body emailChangeRequest: EmailChangeRequest): Response<UploadResponse>
    @POST("Identified")
    suspend fun identify(@Body identifiedData: IdentifiedData):Response<IdentifiedResponse>
    @POST("SendRequest")
    suspend fun sendRequest(@Body helpRequest: HelpRequest):Response<UploadResponse>
    @GET("GetRequest")
    suspend fun getRequire(@Query("helpRequest") helpRequest: HelpRequest):Response<HelpResponse>
}
