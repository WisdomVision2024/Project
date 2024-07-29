
package com.example.project

import DataStore.LoginDataStore
import DataStore.LoginState
import ViewModels.HandleResult
import ViewModels.Identified
import ViewModels.Setting
import ViewModels.TTS
import ViewModels.UploadState
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun HomePage(
    androidViewModel: Identified,
    loginDataStore:LoginDataStore,
    tts:TTS,
    viewModel: Setting,
    navController: NavController
) {
    val handleState by androidViewModel.handleResult.collectAsState()
    val uploadState =androidViewModel.uploadState.collectAsState().value
    val state by androidViewModel.state.collectAsState()

    val loginStateFlow = loginDataStore.loadLoginState()
    val loginState by loginStateFlow.collectAsState(initial = LoginState(isLoggedIn = true))
    val account = loginState.currentUser?.account

    var isLanguageChangeScreenVisible by remember { mutableStateOf(false) }
    var nameChangeScreenVisible by remember { mutableStateOf(false) }
    var passwordChangeScreenVisible by remember { mutableStateOf(false) }
    var emailChangeScreenVisible by remember { mutableStateOf(false) }
    var errorScreen by remember { mutableStateOf(false) }

    var uploadResponseState by remember { mutableStateOf(false) }
    var response by remember { mutableStateOf("")  }

    val text=state.spokenText.ifEmpty { "" }


    LaunchedEffect(handleState) {
        when (handleState) {
            is HandleResult.LanguageChange -> isLanguageChangeScreenVisible = true
            is HandleResult.NameChange -> nameChangeScreenVisible = true
            is HandleResult.PasswordChange -> passwordChangeScreenVisible = true
            is HandleResult.EmailChange -> emailChangeScreenVisible = true
            else -> { Unit }
        }
    }
    LaunchedEffect(uploadState) {
        when(uploadState){
            is UploadState.Success->{
                response=(uploadState as UploadState.Success).result.toString()
                uploadResponseState=true
                tts.speak(response)
            }
            is UploadState.Error->{
                errorScreen=true
                response=(uploadState as UploadState.Error).message
            }
            else->{Unit}
        }
    }

    if (errorScreen){
        ErrorMessageScreen(errorMessage = response, onClose = {errorScreen=false})
    }
    if (isLanguageChangeScreenVisible) {
        LanguageChangeScreen(
            onClose = {isLanguageChangeScreenVisible=false}
        )
    }
    if (nameChangeScreenVisible){
        NameChangeScreen(
            viewModel = viewModel,
            account,
            onClose = { nameChangeScreenVisible = false }
        )
    }
    if (passwordChangeScreenVisible){
        PasswordChangeScreen(viewModel = viewModel,
            account,
            onClose = {passwordChangeScreenVisible=false})
    }
    if (emailChangeScreenVisible){
        EmailChangeScreen(viewModel = viewModel,
            account,
            onClose = {emailChangeScreenVisible=false})
    }

    Scaffold(modifier = Modifier.fillMaxSize(),
        topBar ={
            Box(modifier = Modifier.fillMaxWidth(),
                contentAlignment= Alignment.TopEnd)
            {
                IconButton(onClick = { navController.navigate("SettingPage") })
                {
                    Icon(imageVector = Icons.Filled.Settings,
                        contentDescription = stringResource(id = R.string.setting_page),
                        tint = Color(2,115,115),
                        modifier = Modifier.size(30.dp)
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
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(modifier = Modifier.padding(20.dp))
                Text(
                    text = text,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Justify,
                    modifier = Modifier.padding(20.dp)
                )
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
                            .size(320.dp, 320.dp)
                            .background(color = Color(242, 231, 220))
                    ) {
                        item {
                            if (uploadResponseState){
                                Text(text = response)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.padding(12.dp))
                    Button(
                        onClick = {
                            if (state.isSpeaking) {
                                androidViewModel.stopListening()
                                Log.d("voiceToTextState","Stop")
                            } else {
                                androidViewModel.startListening()
                                Log.d("VoiceToTextState","Start")
                            }
                        },
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(Color(255, 0, 0)),
                        elevation = ButtonDefaults.buttonElevation(4.dp),
                        modifier = Modifier.size(96.dp)
                    )
                    {
                        if (!state.isSpeaking) {
                            Icon(
                                painter =
                                painterResource(id = R.drawable.mic_foreground),
                                contentDescription = "Start"
                            )
                        } else {
                            Icon(
                                painter = painterResource(id = R.drawable.square_foreground),
                                contentDescription = "Stop"
                            )
                        }
                    }
                    Spacer(modifier =Modifier.padding(40.dp))
                }
            }
        }
    }
}
