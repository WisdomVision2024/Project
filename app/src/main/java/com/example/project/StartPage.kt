package com.example.project

import DataStore.LoginDataStore
import DataStore.LoginState
import android.app.Application
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import assets.RetrofitInstance
import kotlinx.coroutines.delay

@Composable
fun StartPage(
    loginState: LoginState,
    navController: NavHostController
) {
    var time by remember { mutableStateOf(false) }

    val distance = if (loginState.isLoggedIn) {
        if (loginState.currentUser?.isVisuallyImpaired == true) {
            "HomePage"
        } else {
            "HelpListPage"
        }
    } else {
        "LoginPage"
    }

    LaunchedEffect(Unit) {
        delay(3000)
        time = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(2, 115, 115),
                        Color(169, 217, 208)
                    )
                )
            ),
        horizontalAlignment = Alignment.CenterHorizontally)
    {
        Spacer(modifier = Modifier.padding(40.dp))
        Text(
            text = "Wisdom", color = Color.White, fontSize = 72.sp,
            fontStyle = FontStyle.Italic, fontFamily = FontFamily.Cursive,
            fontWeight = FontWeight.ExtraBold
        )
        Text(
            text = "Vision", color = Color.White, fontSize = 72.sp,
            fontStyle = FontStyle.Italic, fontFamily = FontFamily.Cursive,
            fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(bottom = 20.dp)
        )
    }
    LaunchedEffect(time) {
        if (time) {
            navController.navigate(distance) {
                popUpTo("StartPage") { inclusive = true }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StartPagePreview() {
    val navController = rememberNavController()
    val loginDataStore = LoginDataStore(LocalContext.current)
    val loginStateFlow = loginDataStore.loadLoginState()
    val loginState by loginStateFlow.collectAsState(initial = LoginState(false, null))
    StartPage(loginState, navController)
}
