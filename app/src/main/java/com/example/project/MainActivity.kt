package com.example.project

import ViewModels.Signup
import DataStore.LoginDataStore
import DataStore.LoginState
import ViewModels.HelpList
import ViewModels.Identified
import ViewModels.PermissionState
import provider.IdentifiedFactory
import ViewModels.Setting
import ViewModels.TTS
import ViewModels.UsbCamera
import android.app.AlertDialog
import android.app.Application
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import assets.ArduinoInstance
import assets.RetrofitInstance
import com.example.project.ui.theme.ProjectTheme
import kotlinx.coroutines.launch
import provider.UsbCameraFactory


class MainActivity : ComponentActivity() {
    private val apiService by lazy { RetrofitInstance.apiService }
    private val arduinoApi by lazy { ArduinoInstance.arduinoApi }
    private val identifiedViewModel: Identified by viewModels {
        IdentifiedFactory(
            application,
            apiService
        )
    }
    private val usbCamera:UsbCamera by viewModels {
        UsbCameraFactory(application,apiService)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestMultiplePermissions()
        setContent {
            val context=applicationContext
            val loginDataStore= remember { LoginDataStore(context)}
            val loginStateFlow = loginDataStore.loadLoginState()
            val loginState by loginStateFlow.collectAsState(initial = LoginState(false, null))
            val navController = rememberNavController()
            ProjectTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    UsbTest(viewModel = usbCamera, apiService = apiService)
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
            android.Manifest.permission.BLUETOOTH_SCAN,
            android.Manifest.permission.BLUETOOTH_ADVERTISE,
            android.Manifest.permission.BLUETOOTH_CONNECT,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE){
            permissionsToRequest.addAll(
                listOf(
                    android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED,
                    android.Manifest.permission.MANAGE_DEVICE_POLICY_USB_FILE_TRANSFER,
                    android.Manifest.permission.MANAGE_DEVICE_POLICY_USB_DATA_SIGNALLING
                )
            )
        }
        else{
            permissionsToRequest.addAll(
                listOf(
                    android.Manifest.permission.READ_MEDIA_VIDEO,
                    android.Manifest.permission.RECORD_AUDIO,
                    android.Manifest.permission.READ_MEDIA_IMAGES
                )

                )
        }
        multiplePermissions.launch(permissionsToRequest.toTypedArray())
    }

    private fun showPermissionRationaleDialog() {
        AlertDialog.Builder(this)
            .setTitle("權限請求")
            .setMessage("app需要一些權限来提供完整的功能。請授予所需的權限。")
            .setPositiveButton("重試") { _, _ ->
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
fun LoginPagePreview() {
    val navController = rememberNavController()
    val loginDataStore = LoginDataStore(LocalContext.current)
    val loginStateFlow = loginDataStore.loadLoginState()
    val loginState by loginStateFlow.collectAsState(initial = LoginState(false, null))
    Navigation(
        loginState = loginState,
        navController = navController,
        apiService = RetrofitInstance.apiService,
        loginDataStore =loginDataStore,
        app = Application()
    )
}

@Preview(showBackground = true)
@Composable
fun SignupPagePreview() {
    val loginDataStore = LoginDataStore(LocalContext.current)
    val navController = rememberNavController()
    SignupPage(viewModel = Signup(RetrofitInstance.apiService,
        loginDataStore),navController)
}

@Preview(showBackground = true)
@Composable
fun HomePagePreview() {
    val context = LocalContext.current
    val navController = rememberNavController()
    val loginDataStore=LoginDataStore(context)
    HomePage(
        androidViewModel = Identified(application = Application(),
            apiService = RetrofitInstance.apiService),
        loginDataStore = loginDataStore,
        viewModel = Setting(apiService = RetrofitInstance.apiService,loginDataStore),
        tts = TTS(Application()),
        navController = navController )
}

@Preview(showBackground = true)
@Composable
fun SettingPagePreview() {
    val context=LocalContext.current
    val navController = rememberNavController()
    val loginDataStore=LoginDataStore(context)
    SettingPage(viewModel = Setting(RetrofitInstance.apiService,loginDataStore)
        ,loginDataStore,navController)
}
@Preview(showBackground = true)
@Composable
fun HelpListPagePreview(){
    val context = LocalContext.current
    val navController = rememberNavController()
    val loginDataStore=LoginDataStore(context)
    HelpListPage(viewModel = HelpList(RetrofitInstance.apiService),loginDataStore,navController = navController)
}
