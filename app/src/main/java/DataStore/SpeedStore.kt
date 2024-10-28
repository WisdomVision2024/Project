package DataStore

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


data class Speed(
    val ttsSpeed: Float?=1.0f,
)
class SpeedStore(context: Context) {
    private val Context.loginDataStore by preferencesDataStore(name = "speed")
    private val dataStore=context.loginDataStore
    private val speed = floatPreferencesKey("speed")

    suspend fun saveSpeedState(ttsSpeed: Speed) {
        dataStore.edit { preferences ->
            preferences[speed] = ttsSpeed.ttsSpeed?:1.0f
        }
        Log.d("saveLoginState","is saved")
    }

    fun loadSpeedState(): Flow<Speed> {
        return dataStore.data.map { preferences ->
            val ttsSpeed = preferences[speed] ?: 1.0f
            Log.d("speed"," speed$ttsSpeed")
           Speed(ttsSpeed = ttsSpeed)
        }
    }
}