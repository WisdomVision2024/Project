package provider

import Class.CameraManager
import ViewModels.CameraViewModel
import ViewModels.UsbCamera
import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import assets.ApiService

class PhoneCameraFactor (private val application: Application,
                         private val cameraManager: CameraManager
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CameraViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CameraViewModel(application,cameraManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}