package com.example.project


import DataStore.LoginDataStore
import DataStore.LoginState
import ViewModels.Arduino
import ViewModels.Help
import ViewModels.HelpList
import ViewModels.Identified
import ViewModels.Login
import ViewModels.Setting
import ViewModels.Signup
import ViewModels.TTS
import android.app.Application
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import assets.ApiService
import assets.ArduinoApi

@Composable
fun Navigation(loginState: LoginState,
               navController: NavHostController,
               apiService: ApiService,
               arduinoApi: ArduinoApi,
               loginDataStore: LoginDataStore,
               app: Application
) {
    NavHost(navController = navController,
        startDestination = if (loginState.isLoggedIn) {
        if (loginState.currentUser?.isVisuallyImpaired == true) {
            "HomePage"
        } else {
            "HelpListPage"
        }
    } else {
        "LoginPage"
    }) {
        if (!loginState.isLoggedIn) {
            composable(route = "LoginPage") {
                LoginPage(
                    viewModel = Login(
                        apiService,
                        loginDataStore
                    ),
                    navController = navController,
                )
            }
            composable(route = "SignupPage") {
                SignupPage(viewModel = Signup(apiService,loginDataStore),
                    navController = navController)
            }
        }
        composable(route="StartPage"){
            StartPage(
                loginState = loginState,
                navController = navController
            )
        }
        composable(route = "HomePage") {
            HomePage(
                androidViewModel = Identified(app,  apiService),
                viewModel = Setting(apiService,loginDataStore),
                loginDataStore = loginDataStore,
                tts = TTS(app),
                arduino = Arduino(arduinoApi),
                navController = navController
            )
        }
        composable(route = "SettingPage")
        {
            SettingPage(
                viewModel = Setting(
                    apiService,loginDataStore
                ),
                loginDataStore,
                navController = navController
            )
        }
        composable(route = "HelpListPage") {
            HelpListPage(viewModel = HelpList(apiService),loginDataStore,navController = navController)
        }
        composable(
            route = "HelpPage/{id}/{name}/{description}/{address}",
            arguments = listOf(
                navArgument("id"){type= NavType.StringType},
                navArgument("name") { type = NavType.StringType },
                navArgument("description") { type = NavType.StringType },
                navArgument("address") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val id=backStackEntry.arguments?.getString("id")?:""
            val name = backStackEntry.arguments?.getString("name") ?: ""
            val description = backStackEntry.arguments?.getString("description") ?: ""
            val address = backStackEntry.arguments?.getString("address") ?: ""
            HelpPage(id=id,name = name, description = description, address = address,Help(),navController = navController)
        }
    }
}


