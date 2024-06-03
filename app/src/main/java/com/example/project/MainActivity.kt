package com.example.project

import ViewModels.Signup
import Data.LoginState
import DataStore.LanguageSettingsStore
import DataStore.LoginDataStore
import ViewModels.Identified
import provider.IdentifiedFactory
import ViewModels.Setting
import android.app.Application
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import assets.RetrofitInstance
import com.example.project.ui.theme.ProjectTheme
import provider.FakeApi
import provider.FakeApplication

class MainActivity : ComponentActivity() {
    private val languageSettingsStore = LanguageSettingsStore()
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
            val loginDataStore = LoginDataStore(LocalContext.current.applicationContext)
            val dataStore = loginDataStore.createLoginDataStore(LocalContext.current)
            val loginStateFlow = loginDataStore.loadLoginState(dataStore)
            val loginState by loginStateFlow.collectAsState(initial = LoginState(false, null))
            val navController = rememberNavController()
            ProjectTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    HomePage(
                        androidViewModel =Identified(application,apiService),
                        languageSettingsStore = languageSettingsStore,
                        navController = navController
                    )

                }
            }
        }
    }
        private val identifiedViewModel: Identified by viewModels {
            IdentifiedFactory(
                application,
                apiService
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
    val loginDataStore = LoginDataStore(LocalContext.current.applicationContext)
    val dataStore = loginDataStore.createLoginDataStore(LocalContext.current)
    val loginStateFlow = loginDataStore.loadLoginState(dataStore)
    val loginState by loginStateFlow.collectAsState(initial = LoginState(false, null))
    Navigation(
        loginState = loginState,
        navController = navController,
        apiService = RetrofitInstance.apiService,
        loginDataStore =loginDataStore,
        languageSettingsStore=LanguageSettingsStore(),
        app = Application()
    )
}

@Preview(showBackground = true)
@Composable
fun SignupPagePreview() {
    val languageSettingsStore = LanguageSettingsStore()
    val navController = rememberNavController()
    SignupPage(viewModel = Signup(RetrofitInstance.apiService,
        languageSettingsStore = languageSettingsStore),
        languageSettingsStore,navController)
}

@Preview(showBackground = true)
@Composable
fun HomePagePreview() {
    val context = LocalContext.current
    val navController = rememberNavController()
    val languageSettingsStore = LanguageSettingsStore()
    val fakeApplication = context.applicationContext as? Application ?: FakeApplication()
    val fakeApiService = FakeApi()
    val identifiedViewModel = Identified(fakeApplication, fakeApiService, isPreview = true)
    HomePage(androidViewModel =identifiedViewModel,
        navController = navController, languageSettingsStore =languageSettingsStore )
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
    val navController = rememberNavController()
    val languageSettingsStore = LanguageSettingsStore()
    val loginDataStore=LoginDataStore(LocalContext.current.applicationContext)
    SettingPage(viewModel = Setting(RetrofitInstance.apiService, languageSettingsStore)
        ,languageSettingsStore,loginDataStore,navController)
}