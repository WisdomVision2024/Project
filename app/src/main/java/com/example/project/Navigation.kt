package com.example.project


import Class.CameraManager
import DataStore.LoginDataStore
import DataStore.LoginState
import ViewModels.Arduino
import ViewModels.CameraViewModel
import ViewModels.Help
import ViewModels.HelpList
import ViewModels.Identified
import ViewModels.Login
import ViewModels.Setting
import ViewModels.Signup
import ViewModels.TTS
import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.IntOffset
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import assets.ApiService
import assets.ArduinoApi

@Composable
fun Navigation(
    context: Context,
    activity: Activity,
    cameraManager: CameraManager,
    loginState: LoginState,
    navController: NavHostController,
    apiService: ApiService,
    arduinoApi: ArduinoApi,
    loginDataStore: LoginDataStore,
    app: Application
) {
    val animationSpec: FiniteAnimationSpec<IntOffset> = spring(
        stiffness = Spring.StiffnessMediumLow,
        visibilityThreshold = IntOffset.VisibilityThreshold
    )
    NavHost(navController = navController,
        startDestination ="StartPage") {
        if (!loginState.isLoggedIn) {
            composable(route = "LoginPage",
                enterTransition = {  slideInHorizontally(
                    initialOffsetX = { fullWidth -> fullWidth },
                    animationSpec = animationSpec
                ) },
                exitTransition = { slideOutHorizontally(
                    targetOffsetX = { fullWidth -> -fullWidth },
                    animationSpec = animationSpec
                )
                }
            ) {
                LoginPage(
                    viewModel = Login(
                        apiService,
                        loginDataStore
                    ),
                    navController = navController,
                )
            }
            composable(route = "SignupPage",
                enterTransition = {  slideInHorizontally(
                    initialOffsetX = { fullWidth -> fullWidth },
                    animationSpec = animationSpec
                ) },
                exitTransition = { slideOutHorizontally(
                    targetOffsetX = { fullWidth -> -fullWidth },
                    animationSpec = animationSpec
                )
                }
            )  {
                SignupPage(viewModel = Signup(apiService,loginDataStore),
                    navController = navController)
            }
        }
        composable(route="StartPage",
            enterTransition = {  slideInHorizontally(
                initialOffsetX = { fullWidth -> fullWidth },
                animationSpec = animationSpec
            ) },
            exitTransition = { slideOutHorizontally(
                targetOffsetX = { fullWidth -> -fullWidth },
                animationSpec = animationSpec
            )
            }
        )
        {
            StartPage(
                loginState = loginState,
                navController = navController
            )
        }
        composable(route = "HomePage",
            enterTransition = {  slideInHorizontally(
                initialOffsetX = { fullWidth -> fullWidth },
                animationSpec = animationSpec
            ) },
            exitTransition = { slideOutHorizontally(
                targetOffsetX = { fullWidth -> -fullWidth },
                animationSpec = animationSpec
            )
            }
        ) {
            HomePage(
                context = context,
                activity=activity,
                androidViewModel = Identified(app,  apiService),
                viewModel = Setting(apiService,loginDataStore),
                loginDataStore = loginDataStore,
                tts = TTS(app),
                arduino = Arduino(arduinoApi),
                cameraViewModel = CameraViewModel(app,loginState,cameraManager),
                navController = navController
            )
        }
        composable(route = "SettingPage",
            enterTransition = {  slideInHorizontally(
                initialOffsetX = { fullWidth -> fullWidth },
                animationSpec = animationSpec
            ) },
            exitTransition = { slideOutHorizontally(
                targetOffsetX = { fullWidth -> -fullWidth },
                animationSpec = animationSpec
            )
            }
        )
        {
            SettingPage(
                viewModel = Setting(
                    apiService,loginDataStore
                ),
                loginDataStore,
                navController = navController
            )
        }
        composable(route = "HelpListPage",
            enterTransition = {  slideInHorizontally(
                initialOffsetX = { fullWidth -> fullWidth },
                animationSpec = animationSpec
            ) },
            exitTransition = { slideOutHorizontally(
                targetOffsetX = { fullWidth -> -fullWidth },
                animationSpec = animationSpec
            )
            }
        )  {
            HelpListPage(context,viewModel = HelpList(apiService),
                activity = activity,navController = navController)
        }
        composable("Introduce1", enterTransition = {  slideInHorizontally(
            initialOffsetX = { fullWidth -> fullWidth },
            animationSpec = animationSpec
        ) },
            exitTransition = { slideOutHorizontally(
                targetOffsetX = { fullWidth -> -fullWidth },
                animationSpec = animationSpec
            )
            }
        )  {
            IntroducePage_1(tts = TTS(app), navController = navController)
        }
        composable("Introduce2", enterTransition = {  slideInHorizontally(
            initialOffsetX = { fullWidth -> fullWidth },
            animationSpec = animationSpec
        ) },
            exitTransition = { slideOutHorizontally(
                targetOffsetX = { fullWidth -> -fullWidth },
                animationSpec = animationSpec
            )
            }
        )  {
            IntroducePage_2( navController = navController)
        }
    }
}


