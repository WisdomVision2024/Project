package ViewModels

import Class.CameraManager
import DataStore.LoginState
import android.app.Application
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import assets.RetrofitInstance.apiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class CameraViewModel(val app: Application,private val loginState: LoginState,
                      private val cameraManager: CameraManager) : AndroidViewModel(app) {

    private var timerJob: Job? = null

    private val _imageUri = MutableLiveData<Uri?>()
    val imageUri: LiveData<Uri?> = _imageUri

    fun initialize(){
        viewModelScope.launch {
            try {
                // 初始化相機
                cameraManager.initializeCamera()
            } catch (e: Exception) {
                Log.e("CameraViewModel", "Failed to initialize camera: $e")
            }
        }
    }

    fun focusTakingPhotos(interval: Long = 1000L) {
        val context= app.applicationContext
        timerJob = viewModelScope.launch {
            while (isActive) {
                try {
                    // 每秒拍照並上傳
                    val photo = cameraManager.photo()
                    uploadPhoto(photo, context)
                } catch (e: Exception) {
                    Log.e("CameraViewModel", "Error during photo capture/upload: $e")
                }
                delay(interval)
            }
        }
    }

    fun commonTakingPhotos(interval: Long = 1000L, duration: Long = 5000L) {
        val context = app.applicationContext
        timerJob = viewModelScope.launch {
            val endTime = System.currentTimeMillis() + duration
            while (isActive && System.currentTimeMillis() < endTime) {
                try {
                    // 每秒拍照並上傳
                    val photo = cameraManager.photo()
                    uploadPhoto(photo, context)
                } catch (e: Exception) {
                    Log.e("CameraViewModel", "Error during photo capture/upload: $e")
                }
                delay(interval)
            }
            cancel()
        }
    }

    fun stopTakingPhotos() {
        timerJob?.cancel()
        viewModelScope.launch {
            cameraManager.closeCamera()
        }
    }

    fun cancel(){
        timerJob?.cancel()
    }

    private fun uploadPhoto(file: File, context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val uri = FileProvider.getUriForFile(context, "com.example.project.file_provider", file)
            val timeStamp: String = android.icu.text.SimpleDateFormat("yyyyMMdd_HHmm_ss", Locale.US)
                .format(Date())
            val userName:String=loginState.currentUser.toString()
            val fileName=userName+"_"+timeStamp+".jpg"
            val inputStream = uri?.let { context.contentResolver.openInputStream(it) }
            inputStream?.let {
                val byteArray = it.readBytes()
                val requestBody = byteArray.toRequestBody("image/jpeg".toMediaTypeOrNull(), 0, byteArray.size)
                Log.d("upload","$requestBody")
                val part = MultipartBody.Part.createFormData("file", fileName, requestBody)
                Log.d("upload","$part")
                try {
                    val response = apiService.uploadImage(part)
                    if (response.isSuccessful) {
                        val status=response.body()?.status
                        val error=response.body()?.errorMessage
                        val result=response.body()?.results
                        val count=response.body()?.counts
                        Log.d("upload","$status,$error,$result,$count")
                        // 上传成功，删除本地文件
                        file.delete()
                    }
                } catch (e: Exception) {
                    Log.d("upload","$e")
                    file.delete()
                }
            }
        }
    }
}