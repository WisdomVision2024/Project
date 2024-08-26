package Class

import android.content.Context
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CaptureFailure
import android.hardware.camera2.CaptureRequest
import android.hardware.camera2.TotalCaptureResult
import android.hardware.camera2.params.OutputConfiguration
import android.hardware.camera2.params.SessionConfiguration
import android.icu.text.SimpleDateFormat
import android.media.Image
import android.media.ImageReader
import android.os.Environment
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.view.Surface
import androidx.core.content.FileProvider
import assets.ApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import java.util.Date
import java.util.Locale
import java.util.concurrent.ArrayBlockingQueue
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class CameraManager(private val context: Context,
                    private val imageFormat: Int,
                    private val apiService: ApiService) {

    private val cameraManager: CameraManager by lazy {
        context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    }

    // 獲取後置相機ID
    private val cameraId: String by lazy {
        cameraManager.cameraIdList.first { id ->
            val characteristics = cameraManager.getCameraCharacteristics(id)
            characteristics.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_BACK
        }
    }

    private val characteristics: CameraCharacteristics by lazy {
        cameraManager.getCameraCharacteristics(cameraId)
    }

    private lateinit var imageReader: ImageReader
    private val cameraThread = HandlerThread("CameraThread").apply { start() }
    private val cameraHandler = Handler(cameraThread.looper)

    private lateinit var camera: CameraDevice
    private lateinit var session: CameraCaptureSession

    private val imageReaderThread = HandlerThread("imageReaderThread").apply { start() }
    private val imageReaderHandler = Handler(imageReaderThread.looper)

    suspend fun initializeCamera() {
        Log.d("CameraManager", "Initializing camera")
        // 打開後置相機
        camera = openCamera(cameraManager, cameraId, cameraHandler)
        Log.d("CameraManager", "Camera opened: $cameraId")

        // 初始化ImageReader
        val size = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)!!
            .getOutputSizes(imageFormat).maxByOrNull { it.height * it.width }!!
        imageReader = ImageReader.newInstance(size.width, size.height, imageFormat,2)
        Log.d("CameraManager", "ImageReader initialized with size: ${size.width}x${size.height}")

        // 不需要預覽Surface，只需設置ImageReader的Surface
        val targets = listOf(imageReader.surface)

        session = createCaptureSession(camera, targets, cameraHandler)
        Log.d("CameraManager", "Capture session created")
    }

    private suspend fun openCamera(
        cameraManager: CameraManager,
        cameraId: String,
        cameraHandler: Handler
    ): CameraDevice = suspendCoroutine { cont ->
        try {
            Log.d("CameraManager", "Attempting to open camera: $cameraId")
            cameraManager.openCamera(cameraId, object : CameraDevice.StateCallback() {
                override fun onOpened(camera: CameraDevice) {
                    // 相機成功打開，返回 CameraDevice
                    Log.d("CameraManager", "Camera opened successfully: $cameraId")
                    cont.resume(camera)
                }

                override fun onDisconnected(camera: CameraDevice) {
                    // 相機設備已經斷開連接
                    Log.e("CameraManager", "Camera disconnected: $cameraId")
                    cont.resumeWithException(CameraAccessException(CameraAccessException.CAMERA_DISCONNECTED))
                }

                override fun onError(camera: CameraDevice, error: Int) {
                    Log.e("CameraManager", "Error opening camera: $cameraId, error: $error")
                    // 處理相機打開時發生的錯誤
                    val exception = when (error) {
                        ERROR_CAMERA_IN_USE -> CameraAccessException(CameraAccessException.CAMERA_IN_USE)
                        ERROR_MAX_CAMERAS_IN_USE -> CameraAccessException(CameraAccessException.MAX_CAMERAS_IN_USE)
                        ERROR_CAMERA_DISABLED -> CameraAccessException(CameraAccessException.CAMERA_DISABLED)
                        ERROR_CAMERA_DEVICE -> CameraAccessException(CameraAccessException.CAMERA_ERROR)
                        ERROR_CAMERA_SERVICE -> CameraAccessException(CameraAccessException.CAMERA_DISCONNECTED)
                        else -> Exception("未知錯誤: $error")
                    }
                    cont.resumeWithException(exception)
                }
            }, cameraHandler)
        } catch (e: CameraAccessException) {
            Log.e("CameraManager", "Camera access exception: ${e.message}")
            cont.resumeWithException(e)
        } catch (e: SecurityException) {
            Log.e("CameraManager", "Security exception: ${e.message}")
            cont.resumeWithException(e)
        }
    }

    private suspend fun createCaptureSession(
        camera: CameraDevice,
        targets: List<Surface>,
        cameraHandler: Handler
    ):  CameraCaptureSession = suspendCoroutine { cont ->
        try {
            Log.d("CameraManager", "Creating capture session")
            val outputConfigurations = targets.map { OutputConfiguration(it) }
            val sessionConfiguration = SessionConfiguration(
                /* sessionType = */ SessionConfiguration.SESSION_REGULAR,
                /* outputs = */ outputConfigurations,
                /* executor = */ { command -> cameraHandler.post(command) },
                /* cb = */ object : CameraCaptureSession.StateCallback() {
                    override fun onConfigured(session: CameraCaptureSession) {
                        Log.d("CameraManager", "Capture session configured")
                        cont.resume(session)
                    }

                    override fun onConfigureFailed(session: CameraCaptureSession) {
                        Log.e("CameraManager", "Capture session configuration failed")
                        cont.resumeWithException(CameraAccessException(CameraAccessException.CAMERA_ERROR))
                    }
                }
            )

            camera.createCaptureSession(sessionConfiguration)
        } catch (e: CameraAccessException) {
            Log.e("CameraManager", "Camera access exception: ${e.message}")
            cont.resumeWithException(e)
        }
    }


    private suspend fun takePhoto(): File = suspendCoroutine { cont ->

        try {
            Log.d("CameraManager", "Taking photo")
            // 清空 ImageReader 的緩衝區
            while (imageReader.acquireNextImage() != null) {
                imageReader.acquireNextImage().close()
            }

            // 創建一個新的圖像隊列
            val imageQueue = ArrayBlockingQueue<Image>(1)
            imageReader.setOnImageAvailableListener({ reader ->
                val image = reader.acquireNextImage()
                imageQueue.add(image)
                Log.d("CameraManager", "Image available")
            }, cameraHandler)

            // 創建捕獲請求，用來捕獲靜態圖像
            val captureRequest =
                camera.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE).apply {
                addTarget(imageReader.surface)
            }

            // 執行捕獲操作
            session.capture(captureRequest.build(), object : CameraCaptureSession.CaptureCallback() {
                override fun onCaptureCompleted(
                    session: CameraCaptureSession,
                    request: CaptureRequest,
                    result: TotalCaptureResult
                ) {
                    super.onCaptureCompleted(session, request, result)
                    Log.d("CameraManager", "Capture completed")

                    // 從隊列中取出圖像
                    val image = imageQueue.take()

                    // 保存圖像為文件
                    val buffer = image.planes[0].buffer
                    val bytes = ByteArray(buffer.remaining())
                    buffer.get(bytes)
                    val photoFile = saveImage(bytes)
                    Log.d("CameraManager", "Image saved: ${photoFile.absolutePath}")
                    // 釋放圖像資源
                    image.close()
                    // 將結果返回
                    cont.resume(photoFile)
                }

                override fun onCaptureFailed(
                    session: CameraCaptureSession,
                    request: CaptureRequest,
                    failure: CaptureFailure
                ) {
                    super.onCaptureFailed(session, request, failure)
                    Log.e("CameraManager", "Capture failed")
                    cont.resumeWithException(RuntimeException("Capture failed"))
                }
            }, cameraHandler)

        } catch (e: Exception) {
            Log.e("CameraManager", "Exception during capture: ${e.message}")
            cont.resumeWithException(e)
        }
    }

    suspend fun photo():File{
        return takePhoto()
    }

    fun closeCamera() {
        camera.close()
        cameraThread.quitSafely()
        imageReaderThread.quitSafely()
    }

    private fun saveImage(bytes: ByteArray) :File{
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmm_ss", Locale.US).format(Date())
        val storageDir: File = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        val file=File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        )
        FileOutputStream(file).use { it.write(bytes) }
        Log.d("CameraManager", "Image saved: ${file.absolutePath}")
        return file
    }
}