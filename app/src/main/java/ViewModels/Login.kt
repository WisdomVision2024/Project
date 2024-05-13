package ViewModels

import android.content.Context
import androidx.lifecycle.ViewModel
import assets.ApiService
import com.google.gson.JsonParseException
import Data.LoginRequest
import Data.LoginState
import Data.Savedata
import Data.User
import DataStore.LoginDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.IOException

sealed class LoginUiState {
    data object Initial : LoginUiState()
    data object Loading : LoginUiState()
    data class Success(val user: User?) : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}
class Login(private val apiService: ApiService,
            private val applicationContext: Context,
            private val loginDataStore: LoginDataStore) : ViewModel() {
    private val _loginState = MutableStateFlow<LoginUiState>(LoginUiState.Initial)
    val loginState: StateFlow<LoginUiState> = _loginState
    fun login(username: String, password: String) {
        _loginState.value = LoginUiState.Loading // Indicate loading state

        CoroutineScope(Dispatchers.IO).launch  {
            try {
                val response = apiService.login(LoginRequest(username, password))
                if (response.isSuccessful) {
                    val user = response.body()?.user
                    val savedata= user?.let { Savedata(it.account,user.password,user.isVisuallyImpaired) }
                    if (user != null) {
                        savedata?.let { storeUserDataInDataStore(it) }
                        _loginState.value = LoginUiState.Success(response.body()?.user)
                    }
                    else{_loginState.value = LoginUiState.Error("Wrong  user data!")}
                } else {
                    _loginState.value = LoginUiState.Error(response.message())
                }
            } catch (e: Exception) {
                val errorMessage = when (e) {
                    is IOException -> "Network error"
                    is JsonParseException -> "Parsing error"
                    else -> e.message ?: "Unknown error"
                }
                _loginState.value = LoginUiState.Error(errorMessage)
            }
        }
    }
    private suspend fun storeUserDataInDataStore(savedata: Savedata) {
        val loginState = LoginState(
            isLoggedIn = true,
            currentUser = savedata
        )
        loginDataStore.saveLoginState(loginDataStore.createLoginDataStore(applicationContext), loginState)
    }
}

