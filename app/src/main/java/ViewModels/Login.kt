package ViewModels

import androidx.lifecycle.ViewModel
import assets.ApiService
import com.google.gson.JsonParseException
import Data.LoginRequest
import Data.Savedata
import DataStore.LoginDataStore
import DataStore.LoginState
import android.util.Log
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.SocketException

sealed class LoginUiState {
    data object Initial : LoginUiState()
    data object Loading : LoginUiState()
    data class Success(val isVisuallyImpaired:Boolean?) : LoginUiState()
    data class Error(val message: String) : LoginUiState()
    data class OtherError(val message: String):LoginUiState()
}
class Login(private val apiService: ApiService,
            private val loginDataStore: LoginDataStore,
            ) : ViewModel() {
    private val _loginState = MutableStateFlow<LoginUiState>(LoginUiState.Initial)
    val loginState: StateFlow<LoginUiState> = _loginState

    fun login(account: String, password: String) {
        Log.d("LoginViewModel", "login called with account: $account, password: $password")
        _loginState.value=LoginUiState.Loading
        viewModelScope.launch(Dispatchers.IO){
            try {
                val response = apiService.login(LoginRequest(account, password))
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    Log.d("ResponseSuccess","account:${responseBody?.account}," +
                            "password:${responseBody?.password},name:${responseBody?.username}" +
                            ",email:${responseBody?.email},isVisuallyImpaired:${responseBody?.isVisuallyImpaired}")
                    val savedata = Savedata(responseBody?.account,responseBody?.isVisuallyImpaired)
                    storeUserDataInDataStore(true,savedata)
                    _loginState.value = LoginUiState.Success(responseBody?.isVisuallyImpaired)
                } else {
                    Log.d("ResponseError","response error")
                    _loginState.value = LoginUiState.Error(
                        "Error: ${response.errorBody()?.string() ?: response.message()}")
                }
            } catch (e: Exception) {
                val errorMessage = when (e) {
                    is IOException -> {
                        when (e) {
                            is SocketException -> "Connect Error"
                            else -> "Network error"
                        }
                    }
                    is JsonParseException -> "Parsing error"
                    else -> e.message ?: "Unknown error"
                }
                _loginState.value = LoginUiState.OtherError(errorMessage)
                Log.d("ResponseError","response error$errorMessage")
            }
        }
    }
    private suspend fun storeUserDataInDataStore(isLoggedIn:Boolean,savedata: Savedata) {
        val loginState = LoginState(
            isLoggedIn = isLoggedIn,
            currentUser = savedata
        )
        loginDataStore.saveLoginState(loginState)
        Log.d("LoginStoreData","is success")
    }
}

