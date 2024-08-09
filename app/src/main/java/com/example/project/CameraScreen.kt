package com.example.project

import ViewModels.Camera
import android.content.Context
import android.hardware.usb.UsbDevice
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.serenegiant.usb.USBMonitor
import com.serenegiant.usbcameracommon.UVCCameraHandler

@Composable
fun CameraScreen(
    mCameraHandler:UVCCameraHandler,
    viewModel:Camera
){
    val context = LocalContext.current
    Log.d("CameraScreen", "Context retrieved")
    val mUSBMonitor = monitor(context, mCameraHandler)
    Log.d("CameraScreen", "USB Monitor initialized")
    DisposableEffect(Unit) {
        if (mUSBMonitor != null) {
            mUSBMonitor.register()
            Log.d("CameraScreen", "Registering USB Monitor")
        }
        onDispose {
            if (mUSBMonitor != null) {
                mUSBMonitor.unregister()
                Log.d("CameraScreen", "Unregistering USB Monitor")
            }
        }
    }
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center) {
        Button(onClick = {
            Log.d("CameraScreen", "Capture button clicked")
            viewModel.captureImage()
        })
        {
            Text(text = "capture")
        }
    }
}
fun monitor(context: Context,mCameraHandler:UVCCameraHandler): USBMonitor?
{
    var mUSBMonitor: USBMonitor? = null
    try {
        mUSBMonitor = USBMonitor(context, object : USBMonitor.OnDeviceConnectListener {
            override fun onAttach(device: UsbDevice?) {
                Log.d("USBMonitor", "Device attached: ${device?.deviceName}")
                mUSBMonitor?.requestPermission(device)
            }

            override fun onDettach(device: UsbDevice?) {
                Log.d("USBMonitor", "Device detached: ${device?.deviceName}")
                mCameraHandler.close()
            }
            override fun onConnect(
                device: UsbDevice?,
                ctrlBlock: USBMonitor.UsbControlBlock?,
                createNew: Boolean
            ) {
                Log.d("USBMonitor", "Device connected: ${device?.deviceName}")
                ctrlBlock.let {
                    mCameraHandler.open(it)
                }
            }
            override fun onDisconnect(device: UsbDevice?, ctrlBlock: USBMonitor.UsbControlBlock?) {
                Log.d("USBMonitor", "Device disconnected: ${device?.deviceName}")
                mCameraHandler.close()
            }

            override fun onCancel(device: UsbDevice?) {
                Log.d("USBMonitor", "Permission canceled: ${device?.deviceName}")
                mCameraHandler.close()
            }
        })
        Log.d("USBMonitor","$mUSBMonitor")
        return mUSBMonitor
    }catch (e:Exception){
        Log.d("","")
    }
    return null
}
