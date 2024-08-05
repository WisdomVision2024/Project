package provider


import ViewModels.UsbCamera
import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import assets.ApiService

class UsbCameraFactory(private val application: Application,
                        private val apiService: ApiService):ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UsbCamera::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UsbCamera(application,apiService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}