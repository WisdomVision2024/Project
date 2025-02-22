package com.example.project

import DataStore.LoginDataStore
import DataStore.LoginState
import DataStore.Speed
import DataStore.SpeedStore
import ViewModels.LogOutState
import ViewModels.Setting
import ViewModels.TTS
import ViewModels.UpdateUiState
import android.app.Activity
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.zIndex
import androidx.navigation.NavController

@Composable
fun SettingPage(viewModel:Setting,
                loginDataStore: LoginDataStore,
                speedStore: SpeedStore,
                onClose: () -> Unit,
                tts: TTS,
                navController: NavController)
{
    val loginStateFlow = loginDataStore.loadLoginState()
    val loginState by loginStateFlow.collectAsState(initial = LoginState(true))

    val updateState = viewModel.updateState.collectAsState().value
    val logOutState=viewModel.logOutState.collectAsState().value
    val account = loginState.currentUser?.account
    val isVisuallyImpaired=loginState.currentUser?.isVisuallyImpaired

    val speedFlow = speedStore.loadSpeedState().collectAsState(initial = Speed(1.0f))
    val savedSpeed = speedFlow.value.ttsSpeed ?: 1.0f

    var speechRate by remember { mutableFloatStateOf(savedSpeed) } // 使用加载的语速
    var sliderPosition by remember { mutableFloatStateOf(savedSpeed) }

    var nameChangeScreenVisible by remember { mutableStateOf(false) }
    var logOutScreenVisible by remember { mutableStateOf(false) }
    var errorScreen by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }
    var isShowIntroduce1 by remember { mutableStateOf(false) }
    var isShowIntroduce2 by remember { mutableStateOf(false) }
    var isSuccessScreen by remember { mutableStateOf(false) }

    LaunchedEffect(savedSpeed) {
        speechRate = savedSpeed
        sliderPosition = savedSpeed
        tts.tts?.setSpeechRate(speechRate)
    }

    LaunchedEffect(updateState) {
        when(updateState){
            is UpdateUiState.Error->{
                message=(updateState as UpdateUiState.Error).message
                errorScreen=true
            }
            is UpdateUiState.Success->{
                isSuccessScreen=true
            }
            else->{Unit}
        }
    }

    LaunchedEffect(logOutState) {
        when(logOutState){
            is LogOutState.Error->{
                message=(logOutState as LogOutState.Error).message
                errorScreen=true
            }
            else->{Unit}
        }
    }

    LaunchedEffect(loginState) {
        if (!loginState.isLoggedIn){
            navController.navigate("LoginPage"){
                popUpTo(navController.graph.startDestinationId) { inclusive = true }
            }
        }
    }
    if (errorScreen){
        ErrorMessageScreen(message,tts) { errorScreen=false}
    }

    if (nameChangeScreenVisible){
        NameChangeScreen(
            viewModel = viewModel,
            account=account,
            onClose = { nameChangeScreenVisible = false }
        )
    }
    if (isSuccessScreen){
        FinishScreen(tts, onClose = { isSuccessScreen=false})
    }

    if (logOutScreenVisible){
        Dialog(onDismissRequest = {logOutScreenVisible=false}) {
            Column(modifier = Modifier
                .width(320.dp)
                .height(400.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color(242, 231, 220))
                .border(width = 8.dp, color = Color(2, 115, 115), shape = RoundedCornerShape(4.dp)),
                horizontalAlignment=Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            )
            {
                Spacer(modifier = Modifier.padding(20.dp))
                Text(text = stringResource(id = R.string.check_of_Log_out),
                    fontSize = 24.sp )
                Spacer(modifier = Modifier.padding(40.dp))
                Row(modifier = Modifier.width(300.dp),
                    horizontalArrangement = Arrangement.SpaceAround) {
                    Button(
                        colors = ButtonDefaults.buttonColors(Color.Red),
                        onClick = {
                        logOutScreenVisible=false
                        viewModel.logOut()
                    }
                    ) {
                        Text(text = stringResource(id = R.string.confirm))
                    }
                    Button(onClick = { logOutScreenVisible=false }) {
                        Text(text = stringResource(id = R.string.cancel))
                    }
                }
            }
        }
    }
    if (isShowIntroduce1){
        IntroducePage_1(
            tts = tts,
            speedStore = speedStore,
            onClose = {
                isShowIntroduce1=false
            }
        )
    }
    if (isShowIntroduce2){
        IntroducePage_2(
            onClose =
            {
                isShowIntroduce2=false
            }
        )
    }
    Dialog(onDismissRequest = {}) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .border(width = 8.dp, color = Color(2, 115, 115),
                    shape = RoundedCornerShape(4.dp))
                .background(color = Color(242, 231, 220))
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.TopEnd
            ) {
                IconButton(onClick = { onClose()})
                {
                    Icon(
                        painter = painterResource(R.drawable.clear_foreground),
                        contentDescription = stringResource(id = R.string.close),
                        tint = Color(2, 115, 115),
                        modifier = Modifier.size(100.dp)
                    )
                }
            }
            LazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Spacer(modifier = Modifier.padding(8.dp))
                    Text(
                        text = "調整語音撥放速度",
                        fontSize = 28.sp,
                        textAlign = TextAlign.Start,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.padding(8.dp))
                    Slider(
                        value = sliderPosition,
                        onValueChange = { newValue ->
                            sliderPosition = newValue
                            speechRate = sliderPosition
                            tts.saveSpeed(newValue)// 保存调整后的语速
                            tts.speak("當前語速是 $speechRate 倍") // 播放调整后的语速
                        },
                        valueRange = 0.1f..2.0f, // 可调整范围为 0.1 到 2.0
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Spacer(modifier = Modifier.padding(8.dp))
                    Text(
                        stringResource(id = R.string.user_setting),
                        fontSize = 28.sp,
                        textAlign = TextAlign.Start,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.padding(8.dp))
                    Button(shape = RoundedCornerShape(8.dp),
                        elevation = ButtonDefaults.buttonElevation(4.dp),
                        colors = ButtonDefaults.buttonColors(Color(3, 140, 127)),
                        modifier = Modifier.size(280.dp, 40.dp),
                        onClick = { nameChangeScreenVisible = true })
                    {
                        Text(
                            text = stringResource(id = R.string.change_user_data),
                            fontSize = 18.sp, color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.padding(12.dp))
                    Text(
                        stringResource(id = R.string.other_set),
                        fontSize = 28.sp,
                        textAlign = TextAlign.Start,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.padding(8.dp))
                    Button(
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(Color(3, 140, 127)),
                        elevation = ButtonDefaults.buttonElevation(4.dp),
                        onClick = {
                            if (isVisuallyImpaired==true)
                            {
                               isShowIntroduce1=true
                            }
                            else
                            {
                                isShowIntroduce2=true
                            }
                                  },
                        modifier = Modifier
                            .size(280.dp, 44.dp)
                    )
                    {
                        Text(
                            text = stringResource(id = R.string.help),
                            fontSize = 18.sp, color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.padding(24.dp))
                    Button(
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(Color(3, 140, 127)),
                        elevation = ButtonDefaults.buttonElevation(4.dp),
                        onClick = { logOutScreenVisible = true },
                        modifier = Modifier
                            .size(280.dp, 44.dp)
                    )
                    {
                        Text(
                            text = stringResource(id = R.string.log_out),
                            fontSize = 18.sp, color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.padding(24.dp))
                }
            }
        }
    }
}
