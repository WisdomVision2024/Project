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
data class NameResponse(
    val username: String
)
data class PasswordChangeRequest(
    val password: String
)
data class PasswordChangeResponse(
    val success: Boolean
)
data class EmailChangeRequest(
    val email: String,
    val password: String
)
data class EmailChangeResponse(
    val success: Boolean
)

