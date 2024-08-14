package com.example.project

import ViewModels.Login
import ViewModels.LoginUiState
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun LoginPage(viewModel: Login,
              navController: NavController
) {
    var account by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val scaffoldState = rememberScaffoldState()
    val state = viewModel.loginState.collectAsState().value
    var loginTriggered by remember { mutableStateOf(false) }
    var errorMessageScreenVisible by remember { mutableStateOf(false) }
    var errorMessage by remember{ mutableStateOf("") }

    LoginContent(
        account = account,
        onAccountChange = { account = it },
        password = password,
        onPasswordChange = { password = it },
        onLoginClick = {
            loginTriggered = true
            viewModel.login(account, password)
        },
        onSignupClick = { navController.navigate("SignupPage") }
    )
    LaunchedEffect(state, loginTriggered) {
        if (loginTriggered) {
            Log.d("state", "Connect")
            when (state) {
                is LoginUiState.OtherError -> {
                    val message = (state as LoginUiState.OtherError).message
                    scaffoldState.snackbarHostState.showSnackbar(message)
                    Log.d("Error", message)
                }

                is LoginUiState.Success -> {
                    val destination =
                        if ((state as LoginUiState.Success).isVisuallyImpaired == true)
                            "HomePage" else "HelpListPage"
                    navController.navigate(route = destination) {
                        // 设置 popUpTo 以确保用户不能返回到 LoginPage
                        popUpTo("LoginPage") { inclusive = true }
                    }
                    Log.d("Login success", "navigate success")
                }

                is LoginUiState.Error -> {
                    val message = (state as LoginUiState.Error).message
                    errorMessage=message
                    errorMessageScreenVisible=true
                    Log.d("Login failed", message)
                }

                else -> {
                    Unit
                }
            }
            // 重置 loginTriggered 状态，以避免重复触发登录操作
            loginTriggered = false
        }
    }
    if (errorMessageScreenVisible){
        ErrorMessageScreen(errorMessage =errorMessage,
            onClose = {errorMessageScreenVisible=false}
        )
    }
}

@Composable
fun LoginContent(
    account: String,
    onAccountChange: (String) -> Unit,
    password:String,
    onPasswordChange:(String)->Unit,
    onLoginClick:()->Unit,
    onSignupClick: () -> Unit,
) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color(169, 217, 208)),
            horizontalAlignment = Alignment.CenterHorizontally,
        )
        {
            Spacer(modifier = Modifier.padding(32.dp))
            Text(
                stringResource(R.string.name),
                fontFamily = FontFamily.Serif,
                fontSize = 72.sp,
                lineHeight = 80.sp,
                color = Color(2,115,115),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(8.dp)
            )
            Spacer(modifier = Modifier.padding(30.dp))
            EditInputField2(
                value = account,
                onValueChanged = onAccountChange,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next
                ),
                label = R.string.account,
                modifier = Modifier
                    .background(color = Color(242, 231, 220))
                    .border(
                        4.dp,
                        color = Color(3, 140, 127)
                    )
            )
            Spacer(modifier = Modifier.padding(16.dp))
            PasswordInputField(
                value = password,
                onValueChanged = onPasswordChange,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password, imeAction = ImeAction.Done
                ),
                label = R.string.password,
                modifier = Modifier
                    .background(color = Color(242, 231, 220))
                    .border(
                        4.dp,
                        color = Color(3, 140, 127),
                    )
            )
            Spacer(modifier = Modifier.padding(30.dp))
            Button(
                onClick = onLoginClick,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor=Color(2,115,115)),
                elevation = ButtonDefaults.buttonElevation(4.dp),
                modifier = Modifier
                    .width(300.dp)
                    .height(44.dp)
            ) {
                Text(stringResource(R.string.log_in),
                    color = Color.White)
            }
            Spacer(modifier = Modifier.padding(16.dp))
            Button(
                onClick = onSignupClick,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor=Color(2,115,115)),
                elevation = ButtonDefaults.buttonElevation(4.dp),
                modifier = Modifier
                    .width(300.dp)
                    .height(44.dp)
            )
            {
                Text(text = stringResource(R.string.sign_up),
                    color = Color.White)
            }
        }
    }
}
