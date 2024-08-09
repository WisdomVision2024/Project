package com.example.project

import DataStore.LoginDataStore
import DataStore.LoginState
import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
        navController.navigate(distance) {
            // 设置 popUpTo 以确保用户不能返回到 LoginPage
            popUpTo("StartPage") { inclusive = true }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(2,115,115),
                        Color(169, 217, 208)
                    )
                )
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.padding(40.dp))
        Text(
            text = "Wisdom", color = Color.White, fontSize = 72.sp,
            fontStyle = FontStyle.Italic, fontFamily = FontFamily.Cursive,
            fontWeight = FontWeight.ExtraBold
        )
        Text(
            text = "Vision", color = Color.White, fontSize = 72.sp,
            fontStyle = FontStyle.Italic, fontFamily = FontFamily.Cursive,
            fontWeight = FontWeight.ExtraBold
        )
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
