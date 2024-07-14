package com.example.project


import DataStore.LoginDataStore
import DataStore.LoginState
import ViewModels.Identified
import ViewModels.Login
import ViewModels.Setting
import ViewModels.Signup
import android.app.Application
import androidx.compose.runtime.Composable
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
        composable(route = "HomePage") {
            HomePage(
                androidViewModel = Identified(app, apiService),
                viewModel = Setting(apiService,loginDataStore),
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
            HelpListPage(navController = navController)
        }
    }
}


