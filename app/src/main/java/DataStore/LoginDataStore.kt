package DataStore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.preferencesOf
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import Data.Savedata
import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

data class LoginState(
    val isLoggedIn: Boolean = false,
    val currentUser: Savedata? = null
)
class LoginDataStore (context: Context){
    private val Context.loginDataStore by preferencesDataStore(name = "loginData")
    private val dataStore=context.loginDataStore
    private val isLoggedInKey = booleanPreferencesKey("isLoggedIn")
    private val userIdKey = stringPreferencesKey("account")
    private val isVisuallyImpairedKey= booleanPreferencesKey("isVisuallyImpaired")

    suspend fun saveLoginState(loginState: LoginState) {
        dataStore.edit { preferences ->
            preferences[isLoggedInKey] = loginState.isLoggedIn
            preferences[userIdKey] = loginState.currentUser?.account ?: ""
            preferences[isVisuallyImpairedKey]=loginState.currentUser?.isVisuallyImpaired?:false
        }
        Log.d("saveLoginState","is saved")
    }

    fun loadLoginState(): Flow<LoginState> {
        return dataStore.data.map { preferences ->
            val isLoggedIn = preferences[isLoggedInKey] ?: false
            val currentUser = if (isLoggedIn) {
                Savedata(
                    account = preferences[userIdKey] ?: "",
                    isVisuallyImpaired = preferences[isVisuallyImpairedKey] ?: false
                )
            } else {
                null
            }
            Log.d("loadLoginData","isLoggedIn${isLoggedIn},current user$currentUser")
            LoginState(
                isLoggedIn = isLoggedIn,
                currentUser = currentUser
            )
        }
    }
}
