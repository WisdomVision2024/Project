package ViewModels

import androidx.lifecycle.ViewModel
import assets.ApiService
import Data.SignupRequest
import Data.SignupResponse
import DataStore.LanguageSettingsStore
import Language.Language
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

sealed class SignupUiState {
    data object Initial : SignupUiState()
    data class Success(val signupResponse: SignupResponse?) : SignupUiState()
    data class Error(val message: String) : SignupUiState()
}

class Signup (private val apiService: ApiService,
              private val languageSettingsStore: LanguageSettingsStore
): ViewModel(){
    private val _signupUiState = MutableStateFlow<SignupUiState>(SignupUiState.Initial)
    val registerState: StateFlow<SignupUiState> = _signupUiState

    private val _currentLanguage = MutableStateFlow(Language.English)
    val currentLanguage: StateFlow<Language> = _currentLanguage

    private lateinit var dataStore: DataStore<Preferences>
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
    fun saveLanguageSettings(languageSettingsStore: LanguageSettingsStore,
                             selectedLanguage: Language, context: Context?) {
        context ?: return
        viewModelScope.launch{
            languageSettingsStore.saveLanguageSettings(dataStore, selectedLanguage)
            _currentLanguage.value=selectedLanguage
        }
    }
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
                val response = apiService.signup(SignupRequest(account, username, password,email,isVisuallyImpaired))
                if (response.isSuccessful) {
                    if (response.body()?.success == true) {
                        _signupUiState.value = SignupUiState.Success(response.body())
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
}