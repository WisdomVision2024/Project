package Data

data class User(
    val account: String,
    val password:String,
    val username: String,
    val email: String,
    val isVisuallyImpaired:Boolean
)

data class Savedata(
    val account: String,
    val password:String,
    val isVisuallyImpaired:Boolean
)

data class LoginRequest(
    val account: String,
    val password: String
)
data class SignupRequest(
    val account: String,
    val password:String,
    val username: String,
    val email: String,
    val isVisuallyImpaired:Boolean
)
data class LoginResponse(
    val token: String,
    val user: User?
)
data class SignupResponse(
    val success:Boolean,
    val errorMessage: String?
)
data class NameChangeRequest(
    val username: String,
)

data class PasswordChangeRequest(
    val password: String
)

data class EmailChangeRequest(
    val email: String
)
data class UpdateResponse(
    val success: Boolean,
    val errorMessage: String?
)
