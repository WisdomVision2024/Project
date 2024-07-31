package ViewModels

import Class.UsbCameraManager
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import assets.RetrofitInstance
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
    data class Success(val success:Boolean?) : CameraState()
    data class Error(val message: String?) : CameraState()
}
class UsbCamera(application: Application):AndroidViewModel(application) {
    private val usbCameraManager = UsbCameraManager(application)

    val imageLiveData: LiveData<ByteArray> get() = usbCameraManager.imageLiveData

    private val _cameraState=MutableStateFlow<CameraState>(CameraState.Initial)
    val cameraState:StateFlow<CameraState> = _cameraState
    init {
        usbCameraManager.initialize()
    }

    fun startCapture() {
        usbCameraManager.initialize()
    }

    fun stopCapture() {
        usbCameraManager.stopCapture()
    }

    fun uploadImage(imageData: ByteArray) {
        viewModelScope.launch {
            val tempFile = File(getApplication<Application>().cacheDir, "temp_image.jpg")
            val fos = FileOutputStream(tempFile)
            fos.write(imageData)
            fos.close()

            // Create a RequestBody instance from the file
            val requestFile = tempFile.asRequestBody("image/jpeg".toMediaType())
            val body = MultipartBody.Part.createFormData("image", tempFile.name, requestFile)
            // Upload the image
            try {
                val response = RetrofitInstance.apiService.uploadImage(body)
                if (response.isSuccessful) {
                    val success=response.body()?.success
                    _cameraState.value=CameraState.Success(success)
                } else {
                    val error=response.body()?.errorMessage
                    _cameraState.value=CameraState.Error(error)
                }
            } catch (e: Exception) {
                _cameraState.value=CameraState.Error(e.message)
                // Handle network error
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        usbCameraManager.release()
    }
}