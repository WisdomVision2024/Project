
package com.example.project

import DataStore.LoginDataStore
import DataStore.LoginState
import ViewModels.Arduino
import ViewModels.ArduinoUi
import ViewModels.CameraViewModel
import ViewModels.FocusState
import ViewModels.HandleResult
import ViewModels.Identified
import ViewModels.Setting
import ViewModels.TTS
import ViewModels.UploadState
import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController

@Composable
fun HomePage(
    context: Context,
    activity: Activity,
    androidViewModel: Identified,
    loginDataStore:LoginDataStore,
    arduino: Arduino,
    tts:TTS,
    viewModel: Setting,
    cameraViewModel: CameraViewModel,
    navController: NavController
) {
    var isSettingPageVisibility by remember { mutableStateOf(false) }
    val handleState by androidViewModel.handleResult.collectAsState()
    val uploadState =androidViewModel.uploadState.collectAsState().value
    val state by androidViewModel.state.collectAsState()
    val focus = cameraViewModel.uploadState.collectAsState().value
    val loginStateFlow = loginDataStore.loadLoginState()
    val loginState by loginStateFlow.collectAsState(initial = LoginState(isLoggedIn = true))
    val account = loginState.currentUser?.account

    var nameChangeScreenVisible by remember { mutableStateOf(false) }
    var errorScreen by remember { mutableStateOf(false) }

    var isFocus by remember { mutableStateOf(false) }
    val common by remember { mutableStateOf(false) }
    var needHelp by remember { mutableStateOf(false) }
    var responseState by remember { mutableStateOf(false) }
    var buttonClick by remember { mutableStateOf(true) }

    var response by remember { mutableStateOf("")  }

    val distance=arduino.arduinoState.collectAsState().value
    val hint1= stringResource(id = R.string.hint1)
    val hint2= stringResource(id = R.string.hint2)
    val wait= stringResource(id = R.string.wait)
    val text=state.spokenText.ifEmpty { "" }

    DisposableEffect(Unit) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(activity, // 当前的活动
                arrayOf(Manifest.permission.CAMERA), // 要请求的权限列表
                0)}
        else{
            cameraViewModel.initialize()
        }
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(activity, // 当前的活动
                arrayOf(Manifest.permission.RECORD_AUDIO), // 要请求的权限列表
                1)}
        onDispose {
            if (isFocus){
                cameraViewModel.stopTakingPhotos()
                isFocus=false
            }
        }
    }

    LaunchedEffect(handleState) {
        when (handleState) {
            is HandleResult.NameChange -> nameChangeScreenVisible = true
            is HandleResult.PasswordChange -> nameChangeScreenVisible = true
            is HandleResult.EmailChange -> nameChangeScreenVisible = true
            is HandleResult.Focus->{
                isFocus=true
            }
            is HandleResult.Upload->{
                cameraViewModel.commonTakingPhotos()
                tts.speak(wait)
            }
            is HandleResult.NeedHelp->{
                tts.speak(hint2)
                needHelp=true
            }
            is HandleResult.Arduino->{
                arduino.getDistance()
            }
            else -> { Unit }
        }
    }

    LaunchedEffect(focus) {
        when(focus){
            is FocusState.Success->{
                response=(focus as FocusState.Success).result.toString()
                responseState=true
                tts.speak(response)
            }
            else->{Unit}
        }
    }

    LaunchedEffect(uploadState) {
        when(uploadState){
            is UploadState.Success->{
                response=(uploadState as UploadState.Success).result.toString()
                responseState=true
                tts.speak(response)
                buttonClick=true
            }
            is UploadState.Error->{
                errorScreen=true
                response=(uploadState as UploadState.Error).message
                buttonClick=true
            }
            is UploadState.Loading->{
                buttonClick=false
            }
            else->{
                buttonClick=true
            }
        }
    }

    LaunchedEffect(distance){
        when(distance){
            is ArduinoUi.Success->{
                responseState=true
                response=(distance as ArduinoUi.Success).message.toString()
                tts.speak(response)
            }
            else->{responseState=false}
        }
    }

    LaunchedEffect(isFocus) {
        if(isFocus){
            Log.d("HomePage","Focus true")
            cameraViewModel.focusTakingPhotos()
        }
    }
    LaunchedEffect (common){
        if (common){
            Log.d("HomePage","Common true")
        }
    }

    LaunchedEffect (needHelp){
        if (needHelp){
            tts.speak(hint2)
            Log.d("HomePage","needHelp true")
            cameraViewModel.helpTakingPhotos()
            tts.speak(wait)
        }
    }

    if (isSettingPageVisibility){
        SettingPage(viewModel = viewModel,
            loginDataStore,
            onClose = {isSettingPageVisibility=false},
            tts,
            navController )
    }

    if (errorScreen){
        ErrorMessageScreen(errorMessage = response, tts
            ,onClose = {errorScreen=false})
    }

    if (nameChangeScreenVisible){
        NameChangeScreen(
            viewModel = viewModel,
            account,
            onClose = { nameChangeScreenVisible = false }
        )
    }

    Scaffold(modifier = Modifier.fillMaxSize(),
        topBar ={
            Box(modifier = Modifier
                .fillMaxWidth()
                .background(Color(255, 255, 255)),
                contentAlignment= Alignment.TopEnd)
            {
                IconButton(onClick = { isSettingPageVisibility=true })
                {
                    Icon(imageVector = Icons.Filled.Settings,
                        contentDescription = stringResource(id = R.string.setting_page),
                        tint = Color(2,115,115),
                        modifier = Modifier.size(100.dp)
                    )
                }
            }
        }
    )
    {
        padding->
        Surface {
            Column(
                Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(255, 255, 255),
                                Color(255, 255, 255),
                                Color(169, 217, 208)
                            )
                        )
                    ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = text,
                        fontSize = 20.sp,
                        textAlign = TextAlign.Justify,
                        color = Color.Black
                    )
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .width(320.dp)
                        .height(440.dp)
                )
                {
                    LazyColumn(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .size(320.dp, 360.dp)
                            .background(color = Color(242, 231, 220))
                    ) {
                        item {
                            if (responseState){
                                Text(text = response, fontSize = 20.sp,
                                    color = Color.Black)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.padding(12.dp))
                    Button(
                        onClick = {
                            if (state.isSpeaking) {
                                androidViewModel.stopListening()
                                Log.d("voiceToTextState", "Stop")
                            } else {
                                androidViewModel.startListening()
                                Log.d("VoiceToTextState", "Start")
                                if (isFocus){
                                    cameraViewModel.cancel()
                                }
                            }
                                  },
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(Color(255, 0, 0)),
                        elevation = ButtonDefaults.buttonElevation(4.dp),
                        modifier = Modifier.size(96.dp),
                        enabled = buttonClick
                    )
                    {
                        if (!state.isSpeaking) {
                            Icon(
                                painter = painterResource(id = R.drawable.mic_foreground),
                                contentDescription = "Start"
                            )
                        }
                        else {
                            Icon(
                                painter = painterResource(id = R.drawable.square_foreground),
                                contentDescription = "Stop"
                            )
                        }
                    }
                }
                Spacer(modifier =Modifier.padding(40.dp))
            }
        }
    }
}
