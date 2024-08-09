package com.example.project

import ViewModels.CameraViewModel
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.MediaStore
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


@Composable
fun PhoneCameraTest(context: Context,activity: MainActivity,cameraViewModel: CameraViewModel) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Button(onClick = {
            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA)
                !=PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(activity, // 当前的活动
                    arrayOf(android.Manifest.permission.CAMERA), // 要请求的权限列表
                    0)
            }
            else{
                val uri = cameraViewModel.takePhoto(context)
                if (uri != null) {
                    activity.takePhotoLauncher.launch(uri)
                } else {
                    Log.d("PhoneCameraTest", "Failed to prepare photo capture")
                }
            }
        }) {
            Text("Take Photo")
        }
    }
}