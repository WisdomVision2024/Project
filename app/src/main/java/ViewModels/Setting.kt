package ViewModels

import DataStore.LanguageSettingsStore
import Language.Language
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import assets.ApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class Setting (private val apiService: ApiService) : ViewModel() {
    fun SaveLanguageSettings(languageSettingsStore: LanguageSettingsStore, selectedLanguage: Language) {
        viewModelScope.launch() {
            val dataStore = languageSettingsStore.getDataStore() ?: return@launch
            languageSettingsStore.saveLanguageSettings(dataStore, selectedLanguage)
        }
    }
    private val _currentLanguage = MutableStateFlow<Language>(Language.English)
    val currentLanguage: StateFlow<Language> = _currentLanguage

    fun updateLanguage(selectedLanguage: Language) {
        _currentLanguage.value = selectedLanguage
        // Persist the language to DataStore (optional, already done in saveLanguageSettings)
    }
}