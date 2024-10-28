package Class

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
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
import android.hardware.display.DisplayManager
import android.icu.text.SimpleDateFormat
import android.media.Image
import android.media.ImageReader
import android.os.Environment
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.view.Display
import android.view.Surface
import android.view.WindowManager
import java.io.ByteArrayOutputStream
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
                    ) {

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
    private var cameraThread = HandlerThread("CameraThread").apply { start() }
    private var cameraHandler = Handler(cameraThread.looper)

    private lateinit var camera: CameraDevice
    private lateinit var session: CameraCaptureSession

    private val imageReaderThread = HandlerThread("imageReaderThread").apply { start() }
    private val imageReaderHandler = Handler(imageReaderThread.looper)

    private var previewSurface: Surface? = null
    private var isPreviewEnabled = false

    suspend fun initializeCamera() {
        Log.d("CameraManager", "Initializing camera")
        // 打開後置相機

        cameraThread = HandlerThread("CameraThread").apply { start() }
        cameraHandler = Handler(cameraThread.looper)

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
            var image1 = imageReader.acquireNextImage()
            while (image1 != null) {
                image1.close()
                image1 = imageReader.acquireNextImage()
            }

            // 創建一個新的圖像隊列
            val imageQueue = ArrayBlockingQueue<Image>(1)
            imageReader.setOnImageAvailableListener({ reader ->
                val image = reader.acquireNextImage()
                imageQueue.add(image)
                Log.d("CameraManager", "Image available")
            }, imageReaderHandler)

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
        session.close()
        cameraThread.quitSafely()
        imageReaderThread.quitSafely()
    }


    private fun rotateImageIfRequired(image: ByteArray): ByteArray {
        val matrix = Matrix()

        // 获取相机的传感器方向
        val sensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) ?: 0

        val rotation =getDeviceRotation()

        val deviceRotation = when (rotation) {
            Surface.ROTATION_0 -> 0
            Surface.ROTATION_90 -> 90
            Surface.ROTATION_180 -> 180
            Surface.ROTATION_270 -> 270
            else -> 0
        }

        // 计算需要的旋转角度
        val rotationInDegrees = (sensorOrientation - deviceRotation + 360) % 360
        matrix.postRotate(rotationInDegrees.toFloat())

        // 使用 BitmapFactory 读取图像并应用旋转
        val bitmap = BitmapFactory.decodeByteArray(image, 0, image.size)
        val rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)

        // 将旋转后的图像转换为字节数组并返回
        val outputStream = ByteArrayOutputStream()
        rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        return outputStream.toByteArray()
    }

    private fun getDeviceRotation(): Int {
        val displayManager = context.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
        val display = displayManager.getDisplay(Display.DEFAULT_DISPLAY)
        return display?.rotation ?: Surface.ROTATION_0
    }

    private fun saveImage(bytes: ByteArray) :File{
        val rotatedBytes = rotateImageIfRequired(bytes)
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmm_ss", Locale.US).format(Date())
        val storageDir: File = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        val file=File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        )
        FileOutputStream(file).use { it.write(rotatedBytes) }
        Log.d("CameraManager", "Image saved: ${file.absolutePath}")
        return file
    }
}