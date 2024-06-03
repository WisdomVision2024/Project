package assets

import Data.EmailChangeRequest
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
import Data.getOldPasswordResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT

interface ApiService {
    @GET("api/user/")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>
    @POST("Signup")
    suspend fun signup(@Body signupRequest: SignupRequest):Response<SignupResponse>
    @PUT("Setting/newName")
    suspend fun name(@Body nameRequest: NameChangeRequest):Response<UploadResponse>
    @GET("Setting/oldPassword")
    suspend fun getOldPassword(): Response<getOldPasswordResponse>
    @PUT("Setting/newPassword")
    suspend fun password(@Body passwordChangeRequest: PasswordChangeRequest):Response<UploadResponse>
    @PUT("Setting/newEmail")
    suspend fun email(@Body emailChangeRequest: EmailChangeRequest): Response<UploadResponse>
    @POST("Identified")
    suspend fun identify(@Body identifiedData: IdentifiedData):Response<IdentifiedResponse>
    @POST("SendRequest")
    suspend fun sendRequest(@Body helpRequest: HelpRequest):Response<UploadResponse>
    @GET("GetRequest")
    suspend fun getRequire(@Body helpRequest: HelpRequest):Response<HelpResponse>
}
