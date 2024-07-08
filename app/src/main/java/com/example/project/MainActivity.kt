package com.example.project

import ViewModels.Signup
import DataStore.LoginDataStore
import DataStore.LoginState
import ViewModels.HelpList
import ViewModels.Identified
import provider.IdentifiedFactory
import ViewModels.Setting
import android.app.Application
import android.app.LocaleConfig
import android.app.LocaleManager
import android.os.Bundle
import android.os.LocaleList
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
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import assets.RetrofitInstance
import com.example.project.ui.theme.ProjectTheme
import provider.FakeApi
import provider.FakeApplication

class MainActivity : ComponentActivity() {
    private val apiService by lazy { RetrofitInstance.apiService }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        multiplePermissions.launch(
            arrayOf(
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.RECORD_AUDIO,
                android.Manifest.permission.BLUETOOTH,
                android.Manifest.permission.BLUETOOTH_ADMIN,
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )
        )

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
                        Navigation(
                            loginState = loginState,
                            navController = navController,
                            apiService = apiService,
                            loginDataStore = loginDataStore,
                            application
                        )

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
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                val grantedPermissions = permissions.filterValues { it }
                val deniedPermissions = permissions.filterValues { !it }
                identifiedViewModel.checkPermissions(grantedPermissions, deniedPermissions)
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
    val fakeApplication = context.applicationContext as? Application ?: FakeApplication()
    val fakeApiService = FakeApi()
    val loginDataStore=LoginDataStore(context)
    val identifiedViewModel = Identified(fakeApplication, fakeApiService, isPreview = true)
    HomePage(androidViewModel =identifiedViewModel,
        viewModel = Setting(apiService = RetrofitInstance.apiService,loginDataStore),
        navController = navController )
}
@Preview(showBackground = true)
@Composable
fun RequestPagePreview() {
    val navController = rememberNavController()
    RequestPage(navController)
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
    HelpListPage(navController = navController)
}