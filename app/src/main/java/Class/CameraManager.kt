package Class

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.ImageFormat
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.hardware.camera2.params.OutputConfiguration
import android.hardware.camera2.params.SessionConfiguration
import android.icu.text.SimpleDateFormat
import android.media.ImageReader
import android.net.Uri
import android.os.Environment
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import assets.ApiService
import assets.RetrofitInstance
import com.example.project.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import java.util.Date
import java.util.Locale
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class CameraManager(private val context: Context, private val activity: MainActivity,
                    private val apiService: ApiService) {

    private lateinit var cameraDevice: CameraDevice
    private lateinit var captureSession: CameraCaptureSession
    private lateinit var imageReader: ImageReader
    private var handler: Handler

    private val _imageUri = MutableLiveData<Uri?>()
    val imageUri: LiveData<Uri?> = _imageUri

    init {
        val handlerThread = HandlerThread("CameraThread")
        handlerThread.start()
        handler = Handler(handlerThread.looper)
    }

    fun startCamera() {
        val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            val cameraId = cameraManager.cameraIdList[0] // 使用後置相機
            val cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraId)

            imageReader = ImageReader.newInstance(640, 480, ImageFormat.JPEG, 2)
            imageReader.setOnImageAvailableListener({ reader ->
                val image = reader.acquireLatestImage()
                val buffer = image.planes[0].buffer
                val bytes = ByteArray(buffer.capacity())
                buffer.get(bytes)
                saveImage(bytes)
                image.close()
            }, handler)

            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
               ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.CAMERA),0)
                return
            }
            cameraManager.openCamera(cameraId, object : CameraDevice.StateCallback() {
                override fun onOpened(camera: CameraDevice) {
                    cameraDevice = camera
                    startCaptureSession()
                }

                override fun onDisconnected(camera: CameraDevice) {
                    cameraDevice.close()
                }

                override fun onError(camera: CameraDevice, error: Int) {
                    cameraDevice.close()
                }
            }, handler)

        } catch (e: CameraAccessException) {
            Log.e("CameraCaptureManager", "CameraAccessException: ${e.message}")
        }
    }

    private fun startCaptureSession() {
        val captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE)
        captureRequestBuilder.addTarget(imageReader.surface)

        val outputConfiguration = OutputConfiguration(imageReader.surface)

        // 创建一个单线程的 Executor
        val executor: Executor = Executors.newSingleThreadExecutor()

        val sessionConfiguration = SessionConfiguration(
            SessionConfiguration.SESSION_REGULAR,
            listOf(outputConfiguration),
            executor,  // 替换为 Executor
            object : CameraCaptureSession.StateCallback() {
                override fun onConfigured(session: CameraCaptureSession) {
                    captureSession = session
                    scheduleRepeatingCapture()
                }

                override fun onConfigureFailed(session: CameraCaptureSession) {
                    Log.e("CameraCaptureManager", "onConfigureFailed: Configuration failed")
                }
            }
        )

        cameraDevice.createCaptureSession(sessionConfiguration)
    }

    private fun scheduleRepeatingCapture() {
        val captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE)
        captureRequestBuilder.addTarget(imageReader.surface)

        handler.post(object : Runnable {
            override fun run() {
                try {
                    captureSession.capture(captureRequestBuilder.build(), object : CameraCaptureSession.CaptureCallback() {},
                        handler)
                } catch (e: CameraAccessException) {
                    Log.e("CameraCaptureManager", "CameraAccessException during capture: ${e.message}")
                }
                handler.postDelayed(this, 1000) // 每秒捕捉一次
            }
        })
    }

    private fun saveImage(bytes: ByteArray) {
        val file = createImageFile()
        FileOutputStream(file).use { it.write(bytes) }
        Log.d("CameraCaptureManager", "Image saved: ${file.absolutePath}")
    }

    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmm_ss", Locale.US).format(Date())
        val storageDir: File = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        val file=File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        )
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.file_provider", file)
        Log.d("takePhoto","$uri")
        _imageUri.value = uri
        return file
    }

    fun stopCamera() {
        captureSession.close()
        cameraDevice.close()
        imageReader.close()
    }

    fun uploadPhoto(context: Context) {
        val uri= imageUri.value
        val inputStream = uri?.let { context.contentResolver.openInputStream(it) }
        inputStream?.let {
            val byteArray = it.readBytes()
            val requestBody = byteArray.toRequestBody("image/jpeg".toMediaTypeOrNull(), 0, byteArray.size)
            Log.d("upload","$requestBody")
            val part = MultipartBody.Part.createFormData("file", "filename.jpg", requestBody)
            Log.d("upload","$part")
            val file = File(uri.path!!)
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = RetrofitInstance.apiService.uploadImage(part)
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