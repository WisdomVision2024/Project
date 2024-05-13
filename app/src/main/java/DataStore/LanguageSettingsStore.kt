package DataStore

import Language.Language
import Language.LanguageSetting
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.preferencesOf
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.util.Locale

class LanguageSettingsStore {
    val Context.languageSettingsDataStore by preferencesDataStore(name = "language_settings")
    fun createLanguageSettingsStore(context: Context): DataStore<Preferences> {
        return context.languageSettingsDataStore
    }

    val languageKey = stringPreferencesKey ("language")
    val localeKey = stringPreferencesKey("locale")

    val defaultLanguageSettings = preferencesOf(
        languageKey to Language.Chinese.name,
        localeKey to Language.Chinese.locale.toString()
    )
    suspend fun saveLanguageSettings(dataStore: DataStore<Preferences>, language: Language) {
        dataStore.edit { preferences ->
            preferences[languageKey] = language.name
            preferences[localeKey] = language.locale.toString()
        }
    }
    private var dataStore: DataStore<Preferences>? = null

    fun setDataStore(context: Context) {
        this.dataStore = context.languageSettingsDataStore // 在合适的地方设置 dataStore
    }

    fun getDataStore(): DataStore<Preferences>? {
        return dataStore
    }
    private val _currentLanguage = MutableStateFlow(Language.English)
    val currentLanguage: StateFlow<Language> = _currentLanguage
    fun setCurrentLanguage(language: Language) {
        _currentLanguage.value = language
    }

    fun loadLanguageSettings(dataStore: DataStore<Preferences>): Flow<LanguageSetting> {
        return dataStore.data.map { preferences ->
            LanguageSetting(
                language = Language.valueOf(preferences[languageKey] ?: Language.English.name),
                locale = Locale.forLanguageTag(preferences[localeKey] ?: Language.English.locale.toString())
            )
        }
    }
}
