package Data

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    @SerializedName("Phone")
    val account: String,
    @SerializedName("newName")
    val username: String?,
    @SerializedName("newEmail")
    val email: String?,
    @SerializedName("oldPassword")
    val oldPassword:String,
    @SerializedName("newPassword")
    val newPassword:String?
)

@Serializable
data class UpdateResponse(
    @SerializedName("success")
    val success:Boolean,
    @SerializedName("errorMessage")
    val errorMessage:String,
    @SerializedName("Phone")
    val phone:String,
    @SerializedName("Name")
    val name:String,
    @SerializedName("Email")
    val email:String
)

@Serializable
data class Savedata(
    @SerializedName("Phone")
    val account: String?,
    @SerializedName("Identity")
    val isVisuallyImpaired:Boolean?
)

@Serializable
data class IdentifiedData(
    @SerializedName("T")
    val text:String?
)

@Serializable
data class IdentifiedResponse(
    @SerializedName("status")
    val status: String?,
    @SerializedName("message")
    val ans:String?
)

data class HelpRequest(
    @SerializedName("player")
    val  message: Name?
)

data class HelpResponse(
    @SerializedName("position")
    val position:List<Position>?
)

data class Name(
    @SerializedName("playerName")
    val name:String?
)
data class Position(
    @SerializedName("x")
    val x:Float,
    @SerializedName("y")
    val y:Float
)

data class AcceptCommissionResponse(
    val success: Boolean,
    val helpRequest: HelpRequest?,
    val message: String?
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

data class NameChangeRequest(
    @SerializedName("Name")
    val username: String,
)

data class UploadImageResponse(
    val success: Boolean?,
    @SerializedName("status")
    val status:String?,
    @SerializedName("message")
    val errorMessage: String?,
    @SerializedName("counts")
    val counts:Counts,
    @SerializedName("results")
    val results:List<Results>
)

data class Counts(
    val counts:String?
)
data class Results(
    val results: List<String?>
)
