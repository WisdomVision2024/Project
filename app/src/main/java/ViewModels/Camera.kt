package ViewModels

import android.app.Application
import android.content.Context
import android.net.Uri
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

class Camera(
    private val application: Application,
    private val uvcCameraHandler: UVCCameraHandler,
    private val apiService: ApiService
): AndroidViewModel(application) {

    private val _cameraState= MutableStateFlow<CameraState>(CameraState.Initial)
    val cameraState: StateFlow<CameraState> = _cameraState

    fun captureImage(){
        val timestamp = System.currentTimeMillis().toString()
        Log.d("Camera", "Capturing image with timestamp: $timestamp")
        val tempFile = File(application.cacheDir, "captured_image_$timestamp.png")
        uvcCameraHandler.captureStill(tempFile.absolutePath)
        Log.d("Camera","$tempFile")

    }
    private fun uploadImage(uri:Uri,context:Context){
        val inputStream = context.contentResolver.openInputStream(uri)
        inputStream?.let {
            val byteArray = it.readBytes()
            val requestBody = byteArray.toRequestBody("image/jpeg".toMediaTypeOrNull(), 0, byteArray.size)
            val part = MultipartBody.Part.createFormData("file", "filename.jpg", requestBody)
            val file = File(uri.path!!)
            viewModelScope.launch {
            val response = apiService.uploadImage(part)
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
}}