package com.example.project

import Data.LoginState
import DataStore.LanguageSettingsStore
import DataStore.LoginDataStore
import ViewModels.Identified
import ViewModels.Login
import ViewModels.Setting
import ViewModels.Signup
import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import assets.ApiService

@Composable
fun Navigation(loginState: LoginState,
               navController: NavHostController,
               apiService: ApiService,
               loginDataStore: LoginDataStore,
               languageSettingsStore: LanguageSettingsStore,
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
                        LocalContext.current.applicationContext,
                        loginDataStore
                    ),
                    navController = navController
                )
            }
            composable(route = "SignupPage") {
                SignupPage(viewModel = Signup(apiService,languageSettingsStore),
                    languageSettingsStore,
                    navController = navController)
            }
        }
        composable(route = "HomePage") {
            HomePage(
                androidViewModel = Identified(app, apiService),
                languageSettingsStore = languageSettingsStore,
                navController = navController
            )
        }
        composable(
            route = "SettingPage?screen={screen}",
            arguments = listOf(navArgument("screen") { defaultValue = "" })
        ) { backStackEntry ->
            val screen = backStackEntry.arguments?.getString("screen")
            SettingPage(
                viewModel = Setting(
                    apiService,
                    languageSettingsStore
                ),
                languageSettingsStore,
                loginDataStore,
                navController = navController,
                initialScreen = screen
            )
        }
        composable(route = "HelpListPage") {
            HelpListPage(navController = navController)
        }
        composable(route = "RequestPage") {
            RequestPage(navController = navController)
        }
    }
}


