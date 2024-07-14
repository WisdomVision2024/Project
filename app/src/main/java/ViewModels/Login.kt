package ViewModels

import androidx.lifecycle.ViewModel
import assets.ApiService
import com.google.gson.JsonParseException
import Data.LoginRequest
import Data.LoginResponse
import Data.Savedata
import DataStore.LoginDataStore
import DataStore.LoginState
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
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
        _loginState.value = LoginUiState.Loading
        val loginRequest = LoginRequest(account, password)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = apiService.login(loginRequest)
                Log.d("RawResponse", response.raw().toString())
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        val responseBodyString = (responseBody as ResponseBody).string() // 使用 string() 获取响应体字符串
                        Log.d("ResponseBodyString", responseBodyString)
                        val gson = Gson()
                        try {
                            val loginResponse = gson.fromJson(responseBodyString, LoginResponse::class.java)
                            Log.d("ManualDeserialization", "account:${loginResponse.account}," +
                                    " password:${loginResponse.password}, name:${loginResponse.username}, " +
                                    "email:${loginResponse.email}, isVisuallyImpaired:${loginResponse.isVisuallyImpaired}, " +
                                    "success:${loginResponse.success},errorMessage${loginResponse.errorMessage}")
                            val savedata = Savedata(loginResponse.account, loginResponse.isVisuallyImpaired)
                            storeUserDataInDataStore(true, savedata)
                            _loginState.value = LoginUiState.Success(loginResponse.isVisuallyImpaired)
                        } catch (e: JsonParseException) {
                            Log.d("JsonParseException", "Error parsing JSON: ${e.message}")
                            _loginState.value = LoginUiState.Error("Parsing error: ${e.message}")
                        }
                    } else {
                        Log.d("ResponseBody", "Response body is null")
                        _loginState.value = LoginUiState.Error("Response body is null")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.d("ResponseError", "Error: ${response.message()}, Error Body: $errorBody")
                    _loginState.value = LoginUiState.Error("Error: ${errorBody ?: response.message()}")
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
                Log.d("ResponseError", "response error $errorMessage")
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

