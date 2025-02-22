package provider

import DataStore.SpeedStore
import ViewModels.CameraViewModel
import ViewModels.TTS
import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class TTSFactor (private val app:Application,
                 private val speedStore: SpeedStore): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CameraViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TTS(app,speedStore) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}