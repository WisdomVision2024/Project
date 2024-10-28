package ViewModels

import Class.CameraManager
import DataStore.LoginState
import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import assets.RetrofitInstance.apiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
sealed class FocusState {
    data object Initial : FocusState()
    data class Success(val result: String?) : FocusState()
    data class Error(val message: String) : FocusState()
}

class CameraViewModel(val app: Application,private val loginState: LoginState,
                      private val cameraManager: CameraManager) : AndroidViewModel(app) {

    private var timerJob: Job? = null

    private val _uploadState = MutableStateFlow<FocusState>(FocusState.Initial)
    val uploadState: StateFlow<FocusState> = _uploadState

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

    fun focusTakingPhotos(interval: Long = 10000L) {
        val context= app.applicationContext
        timerJob = viewModelScope.launch {
            while (isActive) {
                try {
                    // 每秒拍照並上傳
                    val photo = cameraManager.photo()
                    focusUploadPhoto(photo, context)
                    Log.d("CameraViewModel","focus")
                } catch (e: Exception) {
                    Log.e("CameraViewModel", "Error during photo capture/upload: $e")
                    e.printStackTrace()
                }
                delay(interval)
            }
        }
    }

    fun commonTakingPhotos() {
        val context = app.applicationContext
        viewModelScope.launch {
            try {
                val photo = cameraManager.photo()
                uploadPhoto(photo, context)
            }
            catch (e: Exception) {
                Log.e("CameraViewModel", "Error during photo capture/upload: $e")
            }
        }
    }

    fun helpTakingPhotos() {
        val context = app.applicationContext
        viewModelScope.launch {
            try {
                // 每秒拍照並上傳
                val photo = cameraManager.photo()
                helpUploadPhoto(photo, context)
                Log.d("CameraViewModel","help")
            }
            catch (e: Exception) {
                Log.e("CameraViewModel", "Error during photo capture/upload: $e")
            }
        }
    }

    fun uploadPhoto(uri: Uri?, name: String) {
        val context = app.applicationContext
        viewModelScope.launch(Dispatchers.IO) {
            try {
                uri?.let {
                    val file = uriToFile(context, it)
                    importPhoto(file, context, name)
                }
            } catch (e: Exception) {
                Log.d("upload", "Error uploading photo: $e")
            }
        }
    }

    // 将 Uri 转换为 File
    private fun uriToFile(context: Context, uri: Uri): File {
        val inputStream = context.contentResolver.openInputStream(uri)
        val file = File(context.cacheDir, "temp_image_from_uri.jpg")
        inputStream.use { input ->
            file.outputStream().use { output ->
                input?.copyTo(output)
            }
        }
        return file
    }


    fun importHuman(name:String){
        val context = app.applicationContext
        viewModelScope.launch {
            try {
                val photo=cameraManager.photo()
                importPhoto(photo,context, name)
                // 每秒拍照並上傳
                Log.d("CameraViewModel","import")
            }
            catch (e: Exception) {
                Log.e("CameraViewModel", "Error during photo capture/upload: $e")
            }
        }
    }

    fun stopTakingPhotos() {
        timerJob?.cancel()
        viewModelScope.launch {
            cameraManager.closeCamera()
        }
    }

    fun closeCamera(){
        viewModelScope.launch {
            cameraManager.closeCamera()
        }
    }

    fun cancel(){
        Log.d("CameraViewModel","cancel Success")
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

    private fun focusUploadPhoto(file: File, context: Context) {
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
                    val response = apiService.uploadFocusImage(part)
                    if (response.isSuccessful) {
                        val status=response.body()?.status
                        val message=response.body()?.ans
                        _uploadState.value=FocusState.Success(message)
                        Log.d("upload","$status,$message")
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

    private fun helpUploadPhoto(file: File, context: Context) {
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
                    val response = apiService.uploadHelpImage(part)
                    if (response.isSuccessful) {
                        Log.d("upload","success")
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

    private fun importPhoto(file: File, context: Context,name: String) {
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
                val nameRequestBody = name.toRequestBody("text/plain".toMediaTypeOrNull())
                try {
                    val response = apiService.importImage(nameRequestBody,part)
                    if (response.isSuccessful) {
                        val status=response.body()?.status
                        Log.d("upload","$status")
                    }
                } catch (e: Exception) {
                    Log.d("upload","$e")
                }
            }
        }
    }

    fun createImageUri(context: Context): Uri? {
        // 创建文件名
        val fileName = "photo_${System.currentTimeMillis()}.jpg"

        // 获取外部路径 (external)
        val externalDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val file = File(externalDir, fileName)

        return try {
            // 返回通过 FileProvider 获取的 Uri
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.file_provider", // 需要与清单文件中的 authorities 匹配
                file
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}