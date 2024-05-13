package DataStore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.preferencesOf
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import Data.LoginState
import Data.Savedata
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LoginDataStore(applicationContext: Context) {
    private val Context.loginDataStore by preferencesDataStore(name = "login_data")
    fun createLoginDataStore(context: Context): DataStore<Preferences> {
        return context.loginDataStore
    }
    private val isLoggedInKey = booleanPreferencesKey("isLoggedIn")
    private val userIdKey = stringPreferencesKey("account")
    private val userPasswordKey=stringPreferencesKey("password")
    private val isVisuallyImpairedKey= booleanPreferencesKey("isVisuallyImpaired")

    val defaultLoginState = preferencesOf(
        isLoggedInKey to false,
        userIdKey to "",
        userPasswordKey to "",
        isVisuallyImpairedKey to false
    )

    suspend fun saveLoginState(dataStore: DataStore<Preferences>, loginState: LoginState) {
        dataStore.edit { preferences ->
            preferences[isLoggedInKey] = loginState.isLoggedIn
            preferences[userIdKey] = loginState.currentUser?.account ?: ""
            preferences[userPasswordKey] = loginState.currentUser?.password ?: ""
            preferences[isVisuallyImpairedKey]=loginState.currentUser?.isVisuallyImpaired?:false
        }
    }

    fun loadLoginState(dataStore: DataStore<Preferences>): Flow<LoginState> {
        return dataStore.data.map { preferences ->
            LoginState(
                isLoggedIn = preferences[isLoggedInKey] ?: false,
                currentUser = if (preferences[isLoggedInKey] == false) {
                    Savedata(
                        account = preferences[userIdKey] ?: "",
                        password = preferences[userPasswordKey]?:"",
                        isVisuallyImpaired=preferences[isVisuallyImpairedKey]?:false
                    )
                } else {
                    null
                }
            )
        }
    }
}
