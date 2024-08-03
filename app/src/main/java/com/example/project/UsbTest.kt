package com.example.project

import ViewModels.UsbCamera
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun UsbTest(viewModel: UsbCamera) {
    val image by viewModel.imageLiveData.observeAsState()

    var isInitialized by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        // 等待设备初始化完成
        delay(2000) // 可以根据实际需要调整等待时间
        isInitialized = true
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        if (isInitialized) {
            image?.let {
                val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
                Image(bitmap = bitmap.asImageBitmap(), contentDescription = null, modifier = Modifier.size(200.dp))
                Log.d("CameraScreen", "Image displayed. Image size: ${it.size} bytes")
            } ?: Log.d("CameraScreen", "No image data available")

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                viewModel.captureImage()
                Log.d("CameraScreen", "Capture Image button clicked")
            }) {
                Text(text = "Capture Image")
            }

            image?.let {
                Spacer(modifier = Modifier.height(16.dp))
                Row {
                    Button(onClick = { /* Save Image Logic */ }) {
                        Text(text = "Save Image")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = { /* Delete Image Logic */ }) {
                        Text(text = "Delete Image")
                    }
                }
            }
        } else {
            Text(text = "Initializing device...")
        }
    }
}