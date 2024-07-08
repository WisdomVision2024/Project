package ViewModels

import androidx.lifecycle.ViewModel
import assets.ApiService
import Data.SignupRequest
import android.content.Context
import Data.Savedata
import DataStore.LoginDataStore
import DataStore.LoginState
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class SignupUiState {
    data object Initial : SignupUiState()
    data class Success(val isVisuallyImpaired:Boolean?) : SignupUiState()
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
        viewModelScope.launch(Dispatchers.IO){
            try {
                val response = apiService.signup(
                    SignupRequest(account, username, password,email,isVisuallyImpaired))
                if (response.isSuccessful) {
                    if (response.body()?.success == true) {
                        val savedata=Savedata(account=account,isVisuallyImpaired=isVisuallyImpaired)
                        Log.d("SignUp","account:$account,isVisuallyImpaired=$isVisuallyImpaired")
                        storeUserDataInDataStore(true,savedata)
                        _signupUiState.value = SignupUiState.Success(isVisuallyImpaired)
                    } else {
                        _signupUiState.value = SignupUiState.Error(
                            response.body()?.errorMessage  ?: "Registration failed"
                        )
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