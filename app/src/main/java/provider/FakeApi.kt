package provider

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
import Data.User
import Data.GetOldPasswordResponse
import assets.ApiService
import retrofit2.Response

class FakeApi:ApiService {
    override suspend fun login(loginRequest: LoginRequest): Response<LoginResponse> {
        return Response.success(LoginResponse("A","AAA","A",
            true,"A@gmail.com"))
    }

    override suspend fun signup(signupRequest: SignupRequest): Response<SignupResponse> {
        return Response.success(SignupResponse(true,null))
    }

    override suspend fun name(nameRequest: NameChangeRequest): Response<UploadResponse> {
        return Response.success(UploadResponse(true,null))
    }

    override suspend fun getOldPassword(): Response<GetOldPasswordResponse> {
        return Response.success(GetOldPasswordResponse("AAA"))
    }

    override suspend fun password(passwordChangeRequest: PasswordChangeRequest): Response<UploadResponse> {
        return Response.success(UploadResponse(true,null))
    }

    override suspend fun email(emailChangeRequest: EmailChangeRequest): Response<UploadResponse> {
        return Response.success(UploadResponse(true,null))
    }

    override suspend fun identify(identifiedData: IdentifiedData): Response<IdentifiedResponse> {
        return Response.success(IdentifiedResponse("Fake response"))
    }

    override suspend fun sendRequest(helpRequest: HelpRequest): Response<UploadResponse> {
        return Response.success(UploadResponse(true,null))
    }

    override suspend fun getRequire(helpRequest: HelpRequest): Response<HelpResponse> {
        return Response.success(HelpResponse(null))
    }
}