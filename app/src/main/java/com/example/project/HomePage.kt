
package com.example.project

import ViewModels.HandleResult
import ViewModels.Identified
import ViewModels.Setting
import ViewModels.UploadState
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
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
    viewModel: Setting,
    navController: NavController
) {
    val upLoadResponseState = androidViewModel.uploadState.collectAsState().value
    val handleState by androidViewModel.handleResult.collectAsState()
    val state by androidViewModel.state.collectAsState()
    var response by remember { mutableStateOf("")  }
    var isLanguageChangeScreenVisible by remember { mutableStateOf(false) }
    var nameChangeScreenVisible by remember { mutableStateOf(false) }
    var passwordChangeScreenVisible by remember { mutableStateOf(false) }
    var emailChangeScreenVisible by remember { mutableStateOf(false) }
    var uploadResponseState by remember { mutableStateOf(false) }
    val text=state.spokenText.ifEmpty { "" }
    LaunchedEffect(handleState) {
        when (handleState) {
            is HandleResult.LanguageChange -> isLanguageChangeScreenVisible = true
            is HandleResult.NameChange -> nameChangeScreenVisible = true
            is HandleResult.PasswordChange -> passwordChangeScreenVisible = true
            is HandleResult.EmailChange -> emailChangeScreenVisible = true
            is HandleResult.Upload -> uploadResponseState = true
            else -> { Unit }
        }
    }

    DisposableEffect(handleState) {
        when (handleState) {
            is HandleResult.NavigateSetting -> {
                navController.navigate("SettingPage")
                androidViewModel.resetHandleState()
            }
            is HandleResult.NavigateRequest -> {/* No-op */ }
            // 其他情況需要處理
            is HandleResult.LanguageChange -> { /* No-op */ }
            is HandleResult.NameChange -> { /* No-op */ }
            is HandleResult.PasswordChange -> { /* No-op */ }
            is HandleResult.EmailChange -> { /* No-op */ }
            is HandleResult.Upload -> { /* No-op */ }
            is HandleResult.Initial -> { /* No-op */ }
            is HandleResult.Loading -> { /* No-op */ }
        }
        onDispose {
            // 清理操作，如果需要
        }
    }
    LaunchedEffect(upLoadResponseState) {
        when(upLoadResponseState){
            is UploadState.Success->{
                response= (upLoadResponseState as UploadState.Success).result.toString()
            }
            is UploadState.Error->{
                response= (upLoadResponseState as UploadState.Error).message
            }
            else->{
                Unit
            }
        }
    }
    if (isLanguageChangeScreenVisible) {
        LanguageChangeScreen(
            onClose = {isLanguageChangeScreenVisible=false}
        )
    }
    if (nameChangeScreenVisible){
        NameChangeScreen(
            viewModel = viewModel,
            onClose = { nameChangeScreenVisible = false }
        )
    }
    if (passwordChangeScreenVisible){
        PasswordChangeScreen(viewModel = viewModel,
            onClose = {passwordChangeScreenVisible=false})
    }
    if (emailChangeScreenVisible){
        EmailChangeScreen(viewModel = viewModel,
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
                            .width(320.dp)
                            .height(320.dp)
                    ) {
                        item {
                            if (uploadResponseState) {
                                Spacer(modifier = Modifier.padding(8.dp))
                                Text(text = response ,
                                    fontSize = 20.sp,
                                    textAlign = TextAlign.Justify,
                                    modifier = Modifier.padding(20.dp)
                                )
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
