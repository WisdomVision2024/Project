package Data

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable
import org.w3c.dom.Text

data class User(
    @SerializedName("Phone")
    val account: String,
    @SerializedName("Password")
    val password:String,
    @SerializedName("Name")
    val username: String,
    @SerializedName("Email")
    val email: String?,
    @SerializedName("Identity")
    val isVisuallyImpaired:Boolean
)
@Serializable
data class Savedata(
    @SerializedName("Phone")
    val account: String?,
    @SerializedName("Identity")
    val isVisuallyImpaired:Boolean?
)

data class IdentifiedData(
    val text:String?
)
data class IdentifiedResponse(
    val ans:String?
)

data class HelpRequest(
    val type:String,
    val description:String,
    val address:String
)

data class HelpResponse(
    val request: List<HelpRequest>?
)

@Serializable
data class SignupRequest(
    @SerializedName("Phone")
    val account: String,
    @SerializedName("Password")
    val password:String,
    @SerializedName("Name")
    val username: String,
    @SerializedName("Identity")
    val isVisuallyImpaired:Boolean,
    @SerializedName("Email")
    val email: String?
)
@Serializable
data class SignupResponse(
    @SerializedName("success")
    val success:Boolean,
    @SerializedName("errorMessage")
    val errorMessage: String?
)

@Serializable
data class LoginRequest(
    @SerializedName("Phone")
    val account: String,
    @SerializedName("Password")
    val password: String
)
@Serializable
data class LoginResponse(
    @SerializedName("Phone")
    val account: String,
    @SerializedName("Password")
    val password:String,
    @SerializedName("Name")
    val username: String,
    @SerializedName("Identity")
    val isVisuallyImpaired:Boolean,
    @SerializedName("Email")
    val email: String?,
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("errorMessage")
    val errorMessage:String
)
data class LoginErrorResponse(
    val success: Boolean,
    val errorMessage: String
)
data class NameChangeRequest(
    @SerializedName("Name")
    val username: String,
)

data class PasswordChangeRequest(
    @SerializedName("Password")
    val password: String
)

data class GetOldPasswordResponse(
    @SerializedName("Password")
    val oldPassword: String
)

data class EmailChangeRequest(
    @SerializedName("Email")
    val email: String
)
data class UploadResponse(
    val success: Boolean,
    val errorMessage: String?
)
