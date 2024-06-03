package ViewModels

import Data.EmailChangeRequest
import Data.UploadResponse
import DataStore.LanguageSettingsStore
import Language.Language
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import assets.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import Data.NameChangeRequest
import Data.PasswordChangeRequest
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import Language.LanguageManager

sealed class UpdateUiState {
    data object Initial : UpdateUiState()
    data class Success(val uploadResponse: UploadResponse?) : UpdateUiState()
    data class Error(val message: String) : UpdateUiState()
}
class Setting(private val apiService: ApiService,
               private val languageSettingsStore: LanguageSettingsStore
) : ViewModel()
{
    private val _updateUiState = MutableStateFlow<UpdateUiState>(UpdateUiState.Initial)
    val updateState: StateFlow<UpdateUiState> = _updateUiState

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

    fun changeName(name:String){
        viewModelScope.launch(Dispatchers.IO){
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
    fun changePassword(oldPassword:String, newPassword:String){
        viewModelScope.launch(Dispatchers.IO){
            try {
                val oldPasswordResponse = apiService.getOldPassword()
                if (oldPasswordResponse.isSuccessful) {
                    val serverOldPassword = oldPasswordResponse.body()
                    if (serverOldPassword != null && serverOldPassword.equals(oldPassword)) {
                        // Step 2: 旧密码验证通过，更新新密码
                        val response = apiService.password(PasswordChangeRequest(newPassword))
                        if (response.isSuccessful) {
                            if (response.body()?.success == true) {
                                _updateUiState.value = UpdateUiState.Success(response.body())
                            } else {
                                _updateUiState.value = UpdateUiState.Error(response.body()?.errorMessage ?: "Update failed")
                            }
                        } else {
                            _updateUiState.value = UpdateUiState.Error(response.message())
                        }
                    } else {
                        _updateUiState.value = UpdateUiState.Error("Old password is incorrect")
                    }
                } else {
                    _updateUiState.value = UpdateUiState.Error(oldPasswordResponse.message())
                }
            } catch (e: Exception) {
                _updateUiState.value = UpdateUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
    fun changeEmail(email:String){
        viewModelScope.launch(Dispatchers.IO){
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