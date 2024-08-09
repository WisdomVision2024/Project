package ViewModels

import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import assets.RetrofitInstance.apiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class CameraViewModel(application: Application) : AndroidViewModel(application) {

    private val _imageUri = MutableLiveData<Uri?>()
    val imageUri: LiveData<Uri?> = _imageUri

    fun takePhoto(context: Context):Uri?{
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmm_ss", Locale.US).format(Date())
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return try {
            val file = File.createTempFile(
                "JPEG_${timeStamp}_",
                ".jpg",
                storageDir
            )
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.file_provider", file)
            Log.d("takePhoto","$uri")
            _imageUri.value = uri
            uri
        } catch (ex: IOException) {
            Log.d("CameraViewModel", "Error creating file: $ex")
            null
        }
    }


    fun uploadPhoto(uri: Uri,context: Context) {
        val inputStream = context.contentResolver.openInputStream(uri)
        inputStream?.let {
            val byteArray = it.readBytes()
            val requestBody = byteArray.toRequestBody("image/jpeg".toMediaTypeOrNull(), 0, byteArray.size)
            Log.d("upload","$requestBody")
            val part = MultipartBody.Part.createFormData("file", "filename.jpg", requestBody)
            Log.d("upload","$part")
            val file = File(uri.path!!)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = apiService.uploadImage(part)
                if (response.isSuccessful) {
                    val status=response.body()?.status
                    Log.d("upload","$status")
                    val error=response.body()?.errorMessage
                    Log.d("upload","$error")
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