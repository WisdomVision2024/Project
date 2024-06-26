package DataStore

import Language.Language
import Language.LanguageSetting
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Locale

class LanguageSettingsStore {
    private val Context.languageSettingsDataStore by preferencesDataStore(name = "language_settings")
    fun createLanguageSettingsStore(context: Context): DataStore<Preferences> {
        return context.languageSettingsDataStore
    }

    private val languageKey = stringPreferencesKey ("language")
    private val localeKey = stringPreferencesKey("locale")

    suspend fun saveLanguageSettings(dataStore: DataStore<Preferences>, language: Language) {
        dataStore.edit { preferences ->
            preferences[languageKey] = language.name
            preferences[localeKey] = language.locale.toString()
        }
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

