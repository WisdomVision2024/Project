package ViewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import assets.ApiService
import assets.RetrofitInstance
import com.serenegiant.usb.USBMonitor
import com.serenegiant.usbcameracommon.UVCCameraHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class Camera(private val application: Application,
             private val usbMonitor: USBMonitor,
             private val uvcCameraHandler: UVCCameraHandler,
             private val onDeviceConnectListener: USBMonitor.OnDeviceConnectListener,
    private val apiService: ApiService
): AndroidViewModel(application) {

    private val _cameraState= MutableStateFlow<CameraState>(CameraState.Initial)
    val cameraState: StateFlow<CameraState> = _cameraState

    fun captureImage(){
        val timestamp = System.currentTimeMillis().toString()
        val tempFile = File(application.cacheDir, "captured_image_$timestamp.png")
        uvcCameraHandler.captureStill(tempFile.absolutePath)
        uploadImage(tempFile)
    }
    private fun uploadImage(file: File){
        val byteArray = file.readBytes()
        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(
                "file", file.name,
                byteArray.toRequestBody("image/png".toMediaTypeOrNull(), 0, byteArray.size)
            )
            .build()
        viewModelScope.launch {
            val response = RetrofitInstance.apiService.uploadImage(requestBody)
            try {
                if (response.isSuccessful) {
                    val status=response.body()?.status
                    Log.d("upload","$status")
                    val error=response.body()?.errorMessage
                    Log.d("upload","$error")
                    _cameraState.value= CameraState.Success(status)
                } else {
                    val status=response.body()?.status
                    Log.d("upload","$status")
                    val error=response.body()?.errorMessage
                    Log.d("upload","$error")
                    _cameraState.value= CameraState.Error(error)
                }
            } catch (e: Exception) {
            _cameraState.value= CameraState.Error(e.message)
            // Handle network error
            }
        }
        file.delete()
    }
}