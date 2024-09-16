package com.example.project

import Class.CameraManager
import Class.HelpRepository
import ViewModels.Signup
import DataStore.LoginDataStore
import DataStore.LoginState
import ViewModels.Arduino
import ViewModels.CameraViewModel
import ViewModels.HelpList
import ViewModels.Identified
import ViewModels.Login
import ViewModels.PermissionState
import provider.IdentifiedFactory
import ViewModels.Setting
import ViewModels.TTS
import android.app.AlertDialog
import android.app.Application
import android.graphics.ImageFormat
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHost
import androidx.navigation.compose.rememberNavController
import assets.ArduinoInstance
import assets.RetrofitInstance
import com.example.project.ui.theme.ProjectTheme
import kotlinx.coroutines.launch
import java.util.Locale

class MainActivity : ComponentActivity() {
    private val apiService by lazy { RetrofitInstance.apiService }
    private val arduinoApi by lazy { ArduinoInstance.arduinoApi }

    private val identifiedViewModel: Identified by viewModels {
        IdentifiedFactory(
            application,
           apiService
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestMultiplePermissions()
        val navigateToHelpList = intent?.getBooleanExtra(
            "navigate_to_help_list", false) ?: false

        setContent {
            val context = applicationContext
            val loginDataStore= remember { LoginDataStore(context)}
            val loginStateFlow = loginDataStore.loadLoginState()
            val loginState by loginStateFlow.collectAsState(
                initial = LoginState(false, null))
            val navController = rememberNavController()

            ProjectTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val cameraManager =
                        CameraManager(
                            context,
                            imageFormat = ImageFormat.JPEG
                        )
                    Navigation(
                        context=context,
                        activity=this@MainActivity,
                        cameraManager=cameraManager,
                        loginState=loginState,
                        navController=navController,
                        apiService=apiService,
                        arduinoApi=arduinoApi,
                        loginDataStore=loginDataStore,
                        app=application
                    )
                    LaunchedEffect(navigateToHelpList) {
                        if (navigateToHelpList) {
                            navController.navigate("HelpListPage")
                        }
                    }
                }
            }
        }

        identifiedViewModel.showPermissionRationale.observe(this) { show ->
            if (show == true) {
                showPermissionRationaleDialog()
            }
        }
       lifecycleScope.launch {
            identifiedViewModel.permissions.collect{ permissions->
               when(permissions){
                    PermissionState.RequestPermissionsAgain->{
                        requestMultiplePermissions()
                    }
                    else->{Unit}
                }
            }
        }
    }

    private val multiplePermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            permissions ->
            val grantedPermissions = permissions.filterValues { it }
            val deniedPermissions = permissions.filterValues { !it }
            identifiedViewModel.checkPermissions(grantedPermissions, deniedPermissions)
        }

    private fun requestMultiplePermissions() {
        val permissionsToRequest = mutableListOf(
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.READ_MEDIA_VIDEO,
            android.Manifest.permission.RECORD_AUDIO,
            android.Manifest.permission.READ_MEDIA_IMAGES,
            android.Manifest.permission.POST_NOTIFICATIONS
        )
        multiplePermissions.launch(permissionsToRequest.toTypedArray())
    }

    private fun showPermissionRationaleDialog() {
        val systemLocale = Locale.getDefault().toLanguageTag()
        val title:String
        val message:String
        val positive:String

        when (systemLocale) {
            "en" -> {
                title = "Permission Request"
                message = "The app needs some permissions to provide full functionality. " +
                        "Please grant the necessary permissions."
                positive = "Retry"
            }
            "fr" -> {
                title = "Demande d'autorisation"
                message = "L'application a besoin de certaines autorisations pour " +
                        "fournir toutes les fonctionnalités. Veuillez accorder les " +
                        "autorisations nécessaires."
                positive = "Réessayer"
            }
            "ja" -> {
                title = "許可リクエスト"
                message = "アプリは完全な機能を提供するためにいくつかの許可が必要です。必要な許可を与えてください。"
                positive = "再試行"
            }
            "ko" -> {
                title = "허가 요청"
                message = "앱이 전체 기능을 제공하기 위해 몇 가지 권한이 필요합니다. 필요한 권한을 부여하세요."
                positive = "다시 시도"
            }
            "zh-TW" -> {
                title = "權限請求"
                message = "應用程序需要一些權限來提供完整功能。請授予必要的權限。"
                positive = "重試"
            }
            else -> {
                title = "Permission Request"
                message = "The app needs some permissions to provide full functionality. " +
                        "Please grant the necessary permissions."
                positive = "Retry"
            }
        }

        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(positive) { _, _ ->
                identifiedViewModel.onPermissionRationaleShown()
                identifiedViewModel.requestPermissionsAgain()
            }
            .setNegativeButton(R.string.cancel) { _, _ ->
                identifiedViewModel.onPermissionRationaleShown()
            }
            .show()
            }
    }

@Preview(showBackground = true)
@Composable
fun StartPreview() {
    val loginDataStore = LoginDataStore(LocalContext.current)
    val navController = rememberNavController()
    val loginStateFlow = loginDataStore.loadLoginState()
    val loginState by loginStateFlow.collectAsState(initial = LoginState(false))
    StartPage(loginState = loginState, navController =navController )
}

@Preview(showBackground = true)
@Composable
fun LoginPagePreview(){
    val loginDataStore = LoginDataStore(LocalContext.current)
    val navController = rememberNavController()
    LoginPage(viewModel = Login(
        RetrofitInstance.apiService,loginDataStore
    ), TTS(Application()),navController = navController)
}

@Preview(showBackground = true)
@Composable
fun SignupPagePreview() {
    val loginDataStore = LoginDataStore(LocalContext.current)
    val navController = rememberNavController()
    SignupPage(viewModel = Signup(RetrofitInstance.apiService,
        loginDataStore), tts = TTS(Application()),navController)
}

@Preview(showBackground = true)
@Composable
fun HomePagePreview() {
    val context = LocalContext.current
    val navController = rememberNavController()
    val loginDataStore=LoginDataStore(context)
    val loginStateFlow = loginDataStore.loadLoginState()
    val loginState by loginStateFlow.collectAsState(initial = LoginState(true))
    val apiService=RetrofitInstance.apiService
    val cameraManager =
        CameraManager(
            context,
            imageFormat = ImageFormat.JPEG
        )
    val application=Application()
    HomePage(
        context=context,
        activity = MainActivity(),
        androidViewModel = Identified(application = Application(),
            apiService = RetrofitInstance.apiService),
        loginDataStore = loginDataStore,
        viewModel = Setting(apiService = RetrofitInstance.apiService,loginDataStore),
        arduino = Arduino(ArduinoInstance.arduinoApi),
        tts = TTS(Application()),
        cameraViewModel = CameraViewModel(application,loginState,cameraManager),
        navController = navController )
}

@Preview(showBackground = true)
@Composable
fun SettingPagePreview() {
    val context=LocalContext.current
    val navController = rememberNavController()
    val loginDataStore=LoginDataStore(context)
    SettingPage(
        viewModel = Setting(RetrofitInstance.apiService,loginDataStore),
        loginDataStore = loginDataStore,
        onClose = {},
        tts = TTS(Application()),
        navController = navController
    )
}
@Preview(showBackground = true)
@Composable
fun HelpListPagePreview(){
    val context = LocalContext.current
    val loginDataStore=LoginDataStore(context)
    val navController = rememberNavController()
    HelpListPage(
        context,
        viewModel = HelpList(HelpRepository(apiService = RetrofitInstance.apiService)),
        activity = MainActivity(),
        setting = Setting(apiService = RetrofitInstance.apiService,loginDataStore),
        loginDataStore = loginDataStore,
        tts = TTS(Application()),
        navController = navController)
}

@Preview(showBackground = true)
@Composable
fun Introduce2Preview(){
    IntroducePage_2(onClose = {})
}

