package com.example.project

import ViewModels.Camera
import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import assets.ApiService
import com.serenegiant.usb.USBMonitor
import com.serenegiant.usbcameracommon.UVCCameraHandler
import com.serenegiant.widget.CameraViewInterface

@Composable
fun CameraScreen(useSurfaceEncoder: Boolean,
           previewWidth: Int,
           previewHeight: Int,
           previewMode: Int,
           onDeviceConnectListener: USBMonitor.OnDeviceConnectListener,
           activity: MainActivity, application: Application,
                 apiService: ApiService
){
    val context = LocalContext.current

    // 這裡可以創建和初始化mUVCCameraView、mUSBMonitor和mCameraHandler
    val mUVCCameraView = remember { mutableStateOf<CameraViewInterface?>(null) }
    val mUSBMonitor = remember { USBMonitor(context, onDeviceConnectListener) }
    val mCameraHandler = remember {
        UVCCameraHandler.createHandler(
            activity,
            mUVCCameraView.value,
            if (useSurfaceEncoder) 0 else 1,
            previewWidth,
            previewHeight,
            previewMode
        )}
    Camera(application = application,mUSBMonitor,mCameraHandler,onDeviceConnectListener,apiService)
}