package ViewModels

import Class.UsbCameraManager
import Class.UvcCameraManager
import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import assets.ApiService
import assets.RetrofitInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

sealed class CameraState {
    data object Initial : CameraState()
    data class Success(val status:String?) : CameraState()
    data class Error(val message: String?) : CameraState()
}
class UsbCamera(application: Application,apiService: ApiService):AndroidViewModel(application) {
    val uvcCameraManager = UvcCameraManager(application.applicationContext,apiService)

    fun initializeCamera() {
        uvcCameraManager.captureImage()
    }

    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.IO + job)
    fun captureAndUploadImage() {
        scope.launch {
            uvcCameraManager.captureImage()
        }
    }

    private fun releaseCamera() {
        uvcCameraManager.release()
    }

    override fun onCleared() {
        super.onCleared()
        releaseCamera()
    }
}