package com.example.project

import ViewModels.Signup
import DataStore.LoginDataStore
import DataStore.LoginState
import ViewModels.BlueTooth
import ViewModels.HelpList
import ViewModels.Identified
import provider.IdentifiedFactory
import ViewModels.Setting
import acitivity.getMacAddress
import android.app.AlertDialog
import android.app.Application
import android.content.pm.PackageManager
import provider.BluToothFactory
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
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import assets.RetrofitInstance
import com.example.project.ui.theme.ProjectTheme


class MainActivity : ComponentActivity() {
    private val apiService by lazy { RetrofitInstance.apiService }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestMultiplePermissions()

        setContent {
            val context=applicationContext
            val loginDataStore= remember { LoginDataStore(context)}
            val loginStateFlow = loginDataStore.loadLoginState()
            val loginState by loginStateFlow.collectAsState(initial = LoginState(false, null))
            val navController = rememberNavController()
            val bluetoothViewModel: BlueTooth = viewModel(factory = BluToothFactory(application))
            ProjectTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TestPage(blueTooth = bluetoothViewModel)
                }
            }
        }
    }
    private val identifiedViewModel: Identified by viewModels {
        IdentifiedFactory(
            application,
            apiService,
            navController = NavController(applicationContext)
        )
    }
    private val multiplePermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            permissions ->
            val grantedPermissions = permissions.filterValues { it }
            val deniedPermissions = permissions.filterValues { !it }
            if (deniedPermissions.isNotEmpty()) {
                // 处理用户拒绝权限的情况
                showPermissionDeniedDialog()
            } else {
                identifiedViewModel.checkPermissions(grantedPermissions, deniedPermissions)
            }
        }
    private fun requestMultiplePermissions() {
        multiplePermissions.launch(
            arrayOf(
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.RECORD_AUDIO,
                android.Manifest.permission.BLUETOOTH,
                android.Manifest.permission.BLUETOOTH_ADMIN,
                android.Manifest.permission.BLUETOOTH_CONNECT,
                android.Manifest.permission.BLUETOOTH_SCAN,
                android.Manifest.permission.BLUETOOTH_ADVERTISE,
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        )
    }
    private fun showPermissionDeniedDialog() {
        AlertDialog.Builder(this)
            .setTitle("權限請求")
            .setMessage("app運作需要這些權限，請授予這些權限以繼續。")
            .setPositiveButton(R.string.confirm) { dialog, _ ->
                dialog.dismiss()
                multiplePermissions.launch(
                    arrayOf(
                        android.Manifest.permission.CAMERA,
                        android.Manifest.permission.RECORD_AUDIO,
                        android.Manifest.permission.BLUETOOTH,
                        android.Manifest.permission.BLUETOOTH_ADMIN,
                        android.Manifest.permission.BLUETOOTH_SCAN,
                        android.Manifest.permission.BLUETOOTH_ADVERTISE,
                        android.Manifest.permission.BLUETOOTH_CONNECT,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION,
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    )
                )
            }
            .setNegativeButton(R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }
            .create()
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
        blueTooth = BlueTooth(Application()),
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
    HomePage(androidViewModel = Identified(application = Application(),
        blueTooth = BlueTooth(Application()),RetrofitInstance.apiService ),
        blueTooth = BlueTooth(Application()),
        viewModel = Setting(apiService = RetrofitInstance.apiService,loginDataStore),
        loginDataStore = loginDataStore,
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
    val navController = rememberNavController()
    HelpListPage(viewModel = HelpList(RetrofitInstance.apiService),navController = navController)
}
