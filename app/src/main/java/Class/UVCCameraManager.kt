package Class

import ViewModels.CameraState
import android.content.Context
import android.graphics.Bitmap
import android.graphics.SurfaceTexture
import android.hardware.usb.UsbDevice
import android.util.Log
import android.view.Surface
import assets.ApiService
import assets.RetrofitInstance
import com.serenegiant.usb.USBMonitor
import com.serenegiant.usb.UVCCamera
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer

class UvcCameraManager(private val context: Context,private val apiService: ApiService) {

    private var mUSBMonitor: USBMonitor? = null
    private var mUVCCamera: UVCCamera? = null
    private var mPreviewSurface: Surface? = null
    private var mSurfaceTexture: SurfaceTexture? = null

    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    private val _cameraState= MutableStateFlow<CameraState>(CameraState.Initial)
    val cameraState: StateFlow<CameraState> = _cameraState

    init {
        mUSBMonitor = USBMonitor(context, object : USBMonitor.OnDeviceConnectListener {
            override fun onAttach(device: UsbDevice?) {
                device?.let {
                    mUSBMonitor?.requestPermission(it)
                }
            }

            override fun onDettach(device: UsbDevice?) {
                mUVCCamera?.close()
            }

            override fun onConnect(device: UsbDevice?, ctrlBlock: USBMonitor.UsbControlBlock?, createNew: Boolean) {
                ctrlBlock?.let {
                    mUVCCamera = UVCCamera()
                    mUVCCamera?.open(it)
                    try {
                        mUVCCamera?.setPreviewSize(UVCCamera.DEFAULT_PREVIEW_WIDTH, UVCCamera.DEFAULT_PREVIEW_HEIGHT)
                    } catch (e: IllegalArgumentException) {
                        e.printStackTrace()
                    }

                    mSurfaceTexture = SurfaceTexture(10)
                    mPreviewSurface = Surface(mSurfaceTexture)
                    mUVCCamera?.setPreviewDisplay(mPreviewSurface)
                    mUVCCamera?.startPreview()
                }
            }

            override fun onDisconnect(device: UsbDevice?, ctrlBlock: USBMonitor.UsbControlBlock?) {
                mUVCCamera?.close()
            }

            override fun onCancel(device: UsbDevice?) {
                // Handle cancel
            }
        })
        mUSBMonitor?.register()
    }

    fun setPreviewDisplay(surface: Surface) {
        mUVCCamera?.setPreviewDisplay(surface)
    }

    fun captureImage() {
        mUVCCamera?.let { camera ->
            camera.setFrameCallback({ frame ->
                // Convert frame to byte array
                val bitmap = convertFrameToBitmap(frame)
                val stream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                val imageData = stream.toByteArray()
                Log.d("imageData","$imageData")

            }, UVCCamera.PIXEL_FORMAT_RGBX)
        } ?: run {
            // Handle the case where the camera is not ready
            Log.e("UvcCameraManager", "Camera is not ready for capturing image")
        }
    }

    private fun convertFrameToBitmap(frame: ByteBuffer): Bitmap {
        frame.rewind()
        val bitmap = Bitmap.createBitmap(UVCCamera.DEFAULT_PREVIEW_WIDTH,
            UVCCamera.DEFAULT_PREVIEW_HEIGHT, Bitmap.Config.ARGB_8888)
        bitmap.copyPixelsFromBuffer(frame)
        Log.d("bitmap","convertFrameToBitmap Success")
        return bitmap
    }

    private fun uploadImage(imageData: ByteArray) {
        scope.launch {
            val requestBody =
                imageData.toRequestBody("image/jpeg".toMediaTypeOrNull(), 0, imageData.size)
        }
    }

    fun release() {
        mUVCCamera?.destroy()
        mUSBMonitor?.unregister()
        mUSBMonitor?.destroy()
        mUSBMonitor = null
        mPreviewSurface?.release()
        mSurfaceTexture?.release()
        job.cancel()
    }
}