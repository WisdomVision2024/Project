package ViewModels

import Data.EmailChangeRequest
import Data.UpdateResponse
import DataStore.LanguageSettingsStore
import Language.Language
import android.content.Context
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import assets.ApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import Data.NameChangeRequest
import Data.PasswordChangeRequest

sealed class UpdateUiState {
    data object Initial : UpdateUiState()
    data class Success(val updateResponse: UpdateResponse?) : UpdateUiState()
    data class Error(val message: String) : UpdateUiState()
}
class Setting (private val apiService: ApiService,
               private val languageSettingsStore: LanguageSettingsStore) : ViewModel()
{
    private val _updateUiState = MutableStateFlow<UpdateUiState>(UpdateUiState.Initial)
    val updateState: StateFlow<UpdateUiState> = _updateUiState
    private val _currentLanguage = MutableStateFlow<Language>(Language.English)
    val currentLanguage: StateFlow<Language> = _currentLanguage
    fun initialize(context: Context) {
        viewModelScope.launch {
            val dataStore = languageSettingsStore.createLanguageSettingsStore(context)
            languageSettingsStore.loadLanguageSettings(dataStore).map { it.language }.collect { language ->
                _currentLanguage.value = language
            }
        }
    }
    fun SaveLanguageSettings(languageSettingsStore: LanguageSettingsStore, selectedLanguage: Language) {
        viewModelScope.launch() {
            val dataStore = languageSettingsStore.getDataStore() ?: return@launch
            languageSettingsStore.saveLanguageSettings(dataStore, selectedLanguage)
        }
    }


    fun updateLanguage(selectedLanguage: Language) {
        _currentLanguage.value = selectedLanguage
        // Persist the language to DataStore (optional, already done in saveLanguageSettings)
    }
    fun changeName(name:String){
        CoroutineScope(Dispatchers.IO).launch{
            try {
                val response = apiService.name(NameChangeRequest(name))
                if (response.isSuccessful){
                    if (response.body()?.success == true){
                        _updateUiState.value=UpdateUiState.Success(response.body())
                    }
                    else{
                        _updateUiState.value=UpdateUiState.Error(
                            response.body()?.errorMessage  ?:"Update failed"
                        )
                    }
                }
                else{_updateUiState.value=UpdateUiState.Error(response.message())}
            }
            catch (e: Exception){
                _updateUiState.value=UpdateUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
    fun changePassword(password:String){
        CoroutineScope(Dispatchers.IO).launch{
            try {
                val response = apiService.password(PasswordChangeRequest(password))
                if (response.isSuccessful){
                    if (response.body()?.success == true){
                        _updateUiState.value=UpdateUiState.Success(response.body())
                    }
                    else{
                        _updateUiState.value=UpdateUiState.Error(
                            response.body()?.errorMessage  ?:"Update failed"
                        )
                    }
                }
                else{_updateUiState.value=UpdateUiState.Error(response.message())}
            }
            catch (e: Exception){
                _updateUiState.value=UpdateUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
    fun changeEmail(email:String){
        CoroutineScope(Dispatchers.IO).launch{
            try {
                val response = apiService.email(EmailChangeRequest(email))
                if (response.isSuccessful){
                    if (response.body()?.success == true){
                        _updateUiState.value=UpdateUiState.Success(response.body())
                    }
                    else{
                        _updateUiState.value=UpdateUiState.Error(
                            response.body()?.errorMessage  ?:"Update failed"
                        )
                    }
                }
                else{_updateUiState.value=UpdateUiState.Error(response.message())}
            }
            catch (e: Exception){
                _updateUiState.value=UpdateUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}