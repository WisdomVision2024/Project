package Class

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageFormat
import android.graphics.SurfaceTexture
import android.media.ImageReader
import android.util.Log
import android.view.Surface
import android.view.View
import com.serenegiant.encoder.IVideoEncoder
import com.serenegiant.widget.CameraViewInterface

class CameraView(
    context: Context
) :View(context),CameraViewInterface{

    private var callback: CameraViewInterface.Callback? = null
    private var surface: Surface? = null
    private var imageReader: ImageReader? = null
    private var aspectRatio: Double = 4.0 / 3.0

    init {
        val width = 640
        val height = 480
        imageReader = ImageReader.newInstance(width, height, ImageFormat.YUV_420_888, 2)
        surface = imageReader?.surface
        Log.d("CameraView", "Initialized with width: $width, height: $height")
    }

    override fun setAspectRatio(p0: Double) {
        Log.d("CameraView", "Setting aspect ratio: $p0")
        aspectRatio = p0
    }

    override fun setAspectRatio(p0: Int, p1: Int) {
        Log.d("CameraView", "Setting aspect ratio: $p0/$p1")
        aspectRatio = p0.toDouble() / p1.toDouble()
    }

    override fun getAspectRatio(): Double {
        return aspectRatio
    }

    override fun onPause() {
        Log.d("CameraView", "OnPause called, closing image reader")
        imageReader?.close()
        imageReader=null
    }

    override fun onResume() {
        Log.d("CameraView", "OnResume called, reinitializing image reader")
        // 重新初始化 ImageReader
        val width = 640
        val height = 480
        imageReader = ImageReader.newInstance(width, height, ImageFormat.YUV_420_888, 2)
        surface = imageReader?.surface
    }

    override fun setCallback(callback: CameraViewInterface.Callback?) {
        Log.d("CameraView", "Callback set")
        this.callback = callback
    }

    override fun getSurfaceTexture(): SurfaceTexture ?{
        return null
    }

    override fun getSurface(): Surface {
        return surface!!
    }

    override fun hasSurface(): Boolean {
        return surface != null
    }

    override fun setVideoEncoder(encoder: IVideoEncoder?) {
        Log.d("CameraView", "setVideoEncoder not yet implemented")
        TODO("Not yet implemented")
    }

    override fun captureStillImage(): Bitmap? {
        Log.d("CameraView", "Capturing still image")
        val image = imageReader?.acquireLatestImage() ?: return null
        val planes = image.planes
        val buffer = planes[0].buffer
        val pixelStride = planes[0].pixelStride
        val rowStride = planes[0].rowStride
        val rowPadding = rowStride - pixelStride * image.width

        val bitmap = Bitmap.createBitmap(
            image.width + rowPadding / pixelStride,
            image.height,
            Bitmap.Config.ARGB_8888
        )

        bitmap.copyPixelsFromBuffer(buffer)
        image.close()

        return bitmap
    }
}