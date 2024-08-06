package com.example.project

import ViewModels.UsbCamera
import android.app.Application
import android.graphics.BitmapFactory
import android.graphics.SurfaceTexture
import android.util.Log
import android.view.Surface
import android.view.TextureView
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import assets.ApiService
import com.serenegiant.usb.UVCCamera
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import provider.UsbCameraFactory

@OptIn(DelicateCoroutinesApi::class)
@Composable
fun UsbTest(viewModel: UsbCamera,apiService: ApiService) {
    val context = LocalContext.current
    val cameraViewModel: UsbCamera = viewModel(factory = UsbCameraFactory(context.applicationContext as Application,
        apiService))
    var isCameraInitialized by remember { mutableStateOf(false) }

    // Initialize camera when the composable is first loaded
    LaunchedEffect(Unit) {
        if (!isCameraInitialized) {
            cameraViewModel.initializeCamera()
            isCameraInitialized = true
        }
    }

    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {
        AndroidView(factory = { ctx ->
            TextureView(ctx).apply {
                surfaceTextureListener = object : TextureView.SurfaceTextureListener {
                    override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
                        val previewSurface = Surface(surface)
                        GlobalScope.launch(Dispatchers.IO) {
                            cameraViewModel.uvcCameraManager.setPreviewDisplay(previewSurface)
                        }
                    }

                    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {}

                    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
                        return true
                    }

                    override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {}
                }
            }
        }, modifier = Modifier
            .fillMaxWidth()
            .height(400.dp)
            .background(Color.Black))

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            cameraViewModel.captureAndUploadImage()
        }) {
            Text(text = "Capture Image")
        }
    }
}