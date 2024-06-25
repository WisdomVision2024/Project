package Data

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("Phone")
    val account: String,
    @SerializedName("Password")
    val password:String,
    @SerializedName("username")
    val username: String,
    @SerializedName("Email")
    val email: String?,
    @SerializedName("Identity")
    val isVisuallyImpaired:Boolean
)

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
    val ans:List<String?>
)

data class HelpRequest(
    val type:String,
    val description:String,
    val address:String
)

data class HelpResponse(
    val request: List<String?>?
)
data class LoginRequest(
    @SerializedName("Phone")
    val account: String,
    @SerializedName("Password")
    val password: String
)
data class SignupRequest(
    @SerializedName("Phone")
    val account: String,
    @SerializedName("Password")
    val password:String,
    @SerializedName("username")
    val username: String,
    @SerializedName("Email")
    val email: String?,
    @SerializedName("Identity")
    val isVisuallyImpaired:Boolean
)
data class LoginResponse(
    @SerializedName("Phone")
    val account: String?,
    @SerializedName("Password")
    val password:String?,
    @SerializedName("username")
    val username: String?,
    @SerializedName("Email")
    val email: String?,
    @SerializedName("Identity")
    val isVisuallyImpaired:Boolean?
)
data class SignupResponse(
    val success:Boolean,
    val errorMessage: String?
)
data class NameChangeRequest(
    @SerializedName("username")
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
