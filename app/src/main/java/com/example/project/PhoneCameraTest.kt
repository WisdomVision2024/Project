package com.example.project

import Class.CameraManager
import ViewModels.CameraViewModel
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import provider.PhoneCameraFactor


@Composable
fun PhoneCameraTest(context: Context,activity: Activity,
                    cameraViewModel: CameraViewModel) {
    DisposableEffect(Unit) {

        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA)
            !=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(activity, // 当前的活动
                arrayOf(android.Manifest.permission.CAMERA), // 要请求的权限列表
                0)
        }
        else{
            cameraViewModel.initialize()
        }

        onDispose {
            cameraViewModel.stopTakingPhotos()
        }
    }
    Column(modifier = Modifier.background(Color.LightGray)) {
        Button(onClick = { cameraViewModel.focusTakingPhotos()}) {
        }
    }
}