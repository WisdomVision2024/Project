package ViewModels

import android.content.Context
import androidx.lifecycle.ViewModel
import assets.ApiService
import com.google.gson.JsonParseException
import Data.LoginRequest
import Data.LoginState
import Data.Savedata
import DataStore.LanguageSettingsStore
import DataStore.LoginDataStore
import Language.Language
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.io.IOException

sealed class LoginUiState {
    data object Initial : LoginUiState()
    data object Loading : LoginUiState()
    data class Success(val isVisuallyImpaired:Boolean?) : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}
class Login(private val apiService: ApiService,
            private val applicationContext: Context,
            private val loginDataStore: LoginDataStore,
    private val languageSettingsStore:LanguageSettingsStore) : ViewModel() {
    private val _loginState = MutableStateFlow<LoginUiState>(LoginUiState.Initial)
    val loginState: StateFlow<LoginUiState> = _loginState
    private lateinit var dataStore: DataStore<Preferences>
    private val _currentLanguage = MutableStateFlow(Language.English)
    val currentLanguage: StateFlow<Language> = _currentLanguage
    fun initialize(context: Context) {
        dataStore= languageSettingsStore.createLanguageSettingsStore(context)
        viewModelScope.launch {
            languageSettingsStore.loadLanguageSettings(dataStore)
                .map { it.language }
                .collect { language ->
                    _currentLanguage.value = language
                }
        }
    }

    fun login(account: String, password: String) {
        viewModelScope.launch(Dispatchers.IO){
            try {
                val response = apiService.login(LoginRequest(account, password))
                loginState
                if (response.isSuccessful) {
                    val account = response.body()?.account
                    val isVisuallyImpaired=response.body()?.isVisuallyImpaired
                    val savedata = Savedata(account,isVisuallyImpaired)
                    if (account != null&&isVisuallyImpaired!=null) {
                        storeUserDataInDataStore(true,savedata)
                        _loginState.value = LoginUiState.Success(isVisuallyImpaired)
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
    private suspend fun storeUserDataInDataStore(isLoggedIn:Boolean,savedata: Savedata) {
        val loginState = LoginState(
            isLoggedIn = isLoggedIn,
            currentUser = savedata
        )
        loginDataStore.saveLoginState(loginDataStore.createLoginDataStore(applicationContext), loginState)
    }
}

