package com.example.project

import ViewModels.Identified
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun HomePage(viewModel: Identified,
             onRecordingStarted: () -> Unit,
             navController: NavController
) {
    val current=1
    var isRecording by remember { mutableStateOf(false) }
    var recordingFilePath by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    Scaffold (modifier = Modifier.fillMaxSize(),
        bottomBar = { Navigationbar(current,navController)
        })
    {
            innerPadding -> println(innerPadding)
        Surface {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {

                Button(
                    shape = if (isRecording==false) CircleShape
                    else RectangleShape,
                    elevation = ButtonDefaults.buttonElevation(4.dp),
                    onClick = {
                        // 移动 isRecording 的更新到这里
                        if (recordingFilePath != null) {
                            if (isRecording==false) {
                                viewModel.startRecording(context) { filePath ->
                                    recordingFilePath = filePath
                                    onRecordingStarted()
                                }
                            } else {//true
                                viewModel.stopRecording(context, recordingFilePath!!)
                                if (viewModel.isUploadSuccess) {
                                    viewModel.deleteRecordingFile(context, recordingFilePath!!)
                                }
                            }
                        } else {
                            println("無文件可用")
                        }
                        isRecording = !isRecording
                    }
                    ,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red,
                        contentColor = Color.White // 设置按钮上文本的颜色为白色
                    ),
                    modifier = Modifier.size(96.dp)
                ) {
                    Text(text = if (isRecording==true) "Stop" else "Start")
                }
                if (recordingFilePath != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("file name: $recordingFilePath")

                    if (!viewModel.isUploadSuccess) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Row {
                            Button(onClick = {
                                // Retry uploading
                                viewModel.uploadRecordingFile(context, recordingFilePath!!) { success ->
                                    if (success) {
                                        viewModel.isUploadSuccess = true
                                    }
                                }
                            }) {
                                Text("try again")
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Button(onClick = {
                                // Delete recording
                                viewModel.deleteRecordingFile(context, recordingFilePath!!)
                            }) {
                                Text("delete")
                            }
                        }
                    }
                }
            }
        }
    }
}
