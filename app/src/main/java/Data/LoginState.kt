package Data

data class LoginState(
    val isLoggedIn: Boolean = false,
    val currentUser: Savedata? = null
)
