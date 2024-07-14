package ViewModels

import Data.LoginResponse
import androidx.lifecycle.ViewModel
import assets.ApiService
import Data.SignupRequest
import android.content.Context
import Data.Savedata
import Data.SignupResponse
import DataStore.LoginDataStore
import DataStore.LoginState
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.JsonParseException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.ResponseBody

sealed class SignupUiState {
    data object Initial : SignupUiState()
    data class Success(val success:Boolean) : SignupUiState()
    data class Error(val message: String) : SignupUiState()
}

class Signup (private val apiService: ApiService,
              private val loginDataStore: LoginDataStore,
): ViewModel(){
    private val _signupUiState = MutableStateFlow<SignupUiState>(SignupUiState.Initial)
    val registerState: StateFlow<SignupUiState> = _signupUiState


    fun signup(
        account: String,
        username: String,
        password: String,
        email: String,
        isVisuallyImpaired: Boolean
    )
    {
        val signUpRequest=SignupRequest(account, password,username,isVisuallyImpaired,email)
        Log.d("SignUp","Identity:$isVisuallyImpaired")
        viewModelScope.launch(Dispatchers.IO){
            try {
                val response = apiService.signup(signUpRequest)
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        val responseBodyString = (responseBody as ResponseBody).string() // 使用 string() 获取响应体字符串
                        Log.d("ResponseBodyString", responseBodyString)
                        val gson = Gson()
                        try {
                            val signUpResponse = gson.fromJson(responseBodyString, SignupResponse::class.java)
                            Log.d("ManualDeserialization","success:${signUpResponse.success}")
                            val savedata=Savedata(account,isVisuallyImpaired)
                            storeUserDataInDataStore(true,savedata)
                            _signupUiState.value = SignupUiState.Success(signUpResponse.success)
                        }catch (e: JsonParseException) {
                            Log.d("JsonParseException", "Error parsing JSON: ${e.message}")
                            _signupUiState.value = SignupUiState.Error("Parsing error: ${e.message}")
                        }
                    }
                    else {
                        _signupUiState.value = SignupUiState.Error("Response body is null")
                    }
                } else {
                    _signupUiState.value = SignupUiState.Error(response.message())
                }
            } catch (e: Exception) {
                _signupUiState.value = SignupUiState.Error(e.message ?: "Unknown error")
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