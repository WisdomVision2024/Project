package com.example.project

import Class.DataCheckWorker
import DataStore.LoginDataStore
import ViewModels.HelpList
import ViewModels.HelpUiState
import ViewModels.Setting
import ViewModels.TTS
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

@Composable
fun HelpListPage(context: Context,
                 viewModel:HelpList,
                 activity: Activity,
                 setting: Setting,
                 loginDataStore: LoginDataStore,
                 tts: TTS,
                 navController: NavController) {
    DisposableEffect (Unit){
        requestPermissionsIfNeeded(context, activity)
        setupWorkManager(context)
        viewModel.fetchHelpData()
        onDispose {
        }
    }
    SuccessScreen(viewModel = viewModel,
        setting=setting,
        loginDataStore = loginDataStore,
        tts = tts,
        navController =navController )
}

private fun setupWorkManager(context: Context) {
    // 創建 PeriodicWorkRequest，設置為每分鐘檢查一次新數據
    val workRequest = PeriodicWorkRequestBuilder<DataCheckWorker>(1, TimeUnit.MINUTES)
        .build()
    // 啟動 WorkManager 任務
    WorkManager.getInstance(context).enqueue(workRequest)
}

private fun requestPermissionsIfNeeded(context: Context, activity: Activity) {
    val permissionsToRequest = mutableListOf<String>()

    if (ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.POST_NOTIFICATIONS
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        permissionsToRequest.add(android.Manifest.permission.POST_NOTIFICATIONS)
    }
    if (permissionsToRequest.isNotEmpty()) {
        ActivityCompat.requestPermissions(activity, permissionsToRequest.toTypedArray(), 0)
    }
}
@Composable
fun SuccessScreen(viewModel:HelpList,
                  setting: Setting,
                  loginDataStore: LoginDataStore,
                  tts: TTS,
                  navController: NavController){
    var isSettingPageVisibility by remember { mutableStateOf(false) }
    val helpState = viewModel.helpListState.collectAsState().value
    var isErrorScreenVisible by remember { mutableStateOf(false) }

    if (isSettingPageVisibility){
        SettingPage(viewModel = setting,
            loginDataStore,
            onClose = {isSettingPageVisibility=false}, tts = tts,navController )
    }
    if (isErrorScreenVisible) {
        // 當狀態切換時顯示 ErrorScreen
        ErrorScreen(
            viewModel = viewModel,
            setting = setting,
            loginDataStore = loginDataStore,
            tts = tts,
            navController = navController
        )
    } else{
        Scaffold (modifier = Modifier.fillMaxSize(),
            topBar ={
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(242, 231, 220)),
                    contentAlignment= Alignment.TopEnd)
                {
                    IconButton(onClick = { isSettingPageVisibility=true }
                    ) {
                        Icon(imageVector = Icons.Filled.Settings,
                            contentDescription = stringResource(id = R.string.setting_page),
                            tint = Color(2,115,115),
                            modifier = Modifier.size(30.dp))
                    }
                }
            }
        )
        {  padding ->
            when (helpState) {
                is HelpUiState.Success -> {
                    isErrorScreenVisible=false
                    val name = (helpState as HelpUiState.Success).helpResponse?.message.toString()
                    val position=(helpState as HelpUiState.Success).helpResponse?.position.toString()
                    Column(
                        modifier = Modifier
                            .padding(padding)
                            .fillMaxSize()
                            .background(
                                color = Color(242, 231, 220)
                            ),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = name,
                            fontSize = 24.sp,
                            color = Color.Black)
                        Spacer(modifier = Modifier.padding(bottom = 8.dp))
                        Text(text = position,
                            fontSize = 24.sp,
                            color = Color.Black)
                        Spacer(modifier = Modifier.padding(bottom = 8.dp))
                        Button(
                            onClick = {isErrorScreenVisible=true}
                        ) { Text(stringResource(R.string.confirm)) }
                    }
                }
                else->{
                    ErrorScreen(viewModel = viewModel,
                        setting = setting,
                        loginDataStore = loginDataStore,
                        tts = tts,
                        navController = navController)
                }
            }
        }
    }
}

@Composable
fun ErrorScreen(viewModel:HelpList,
                setting: Setting,
                loginDataStore: LoginDataStore,
                tts: TTS,
                navController: NavController){
    var isSettingPageVisibility by remember { mutableStateOf(false) }

    if (isSettingPageVisibility){
        SettingPage(viewModel = setting,
            loginDataStore,
            onClose = {isSettingPageVisibility=false},
            tts =tts ,
            navController )
    }

    Scaffold (modifier = Modifier.fillMaxSize(),
        topBar ={
            Box(modifier = Modifier
                .fillMaxWidth()
                .background(color = Color(242, 231, 220)),
                contentAlignment= Alignment.TopEnd)
            {
                IconButton( onClick = { navController.navigate("SettingPage") }
                ) {
                    Icon(imageVector = Icons.Filled.Settings,
                        contentDescription = stringResource(id = R.string.setting_page),
                        tint = Color(2,115,115),
                        modifier = Modifier.size(30.dp))
                }
            }
        }
    )
    { padding->
        Column(modifier = Modifier
            .padding(padding)
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(242, 231, 220),
                        Color(255, 255, 255)
                    )
                )
            ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center)
        {
            Text(text = stringResource(id = R.string.no_help),
                fontSize = 24.sp,
                fontFamily = FontFamily.Serif,
                fontStyle = FontStyle.Normal,
                color = Color.Black,
                modifier = Modifier.padding(12.dp)
            )
            IconButton(onClick = { viewModel.fetchHelpData() }) {
                    Icon(imageVector = Icons.Filled.Search,
                        contentDescription = "refresh",
                        tint = Color.Black,
                        modifier = Modifier.size(50.dp)
                    )
            }
        }
    }
}


