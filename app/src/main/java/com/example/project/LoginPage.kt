package com.example.project

import Data.LoginState
import DataStore.LoginDataStore
import ViewModels.Login
import ViewModels.LoginUiState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
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

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        )
        {
            Text(
                stringResource(R.string.name),
                fontSize = 100.sp,
                lineHeight = 80.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(8.dp)
            )
            Spacer(modifier = Modifier.padding(30.dp))
            EditInputField(
                value = account,
                onValueChanged = { account = it },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next
                ),
                label = R.string.account, modifier = Modifier
            )
            Spacer(modifier = Modifier.padding(16.dp))
            PasswordInputField(
                value = password,
                onValueChanged = { password = it },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password, imeAction = ImeAction.Done
                ),
                label = R.string.password,
                modifier = Modifier
            )
            Spacer(modifier = Modifier.padding(30.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = { navController.navigate("SignupPage") },
                    shape = CircleShape,
                    elevation = ButtonDefaults.buttonElevation(4.dp),
                    modifier = Modifier.size(96.dp)
                )
                {
                    Text(text = stringResource(R.string.sign_up))
                }
                Button(
                    onClick = { viewModel.login(account, password) },
                    shape = CircleShape,
                    elevation = ButtonDefaults.buttonElevation(4.dp),
                    modifier = Modifier.size(96.dp)
                ) {
                    Text(stringResource(R.string.log_in))
                }
            }
            LaunchedEffect(state) {
                when (state) {
                    is LoginUiState.Success -> {
                        val destination =
                            if (state.user?.isVisuallyImpaired == true) "HomePage" else "HelpListPage"
                        navController.navigate(route = destination) {
                                // 设置 popUpTo 以确保用户不能返回到 LoginPage
                                popUpTo("LoginPage") { inclusive = true }
                            }
                    }
                    is LoginUiState.Error -> {
                        val message = state.message
                        scaffoldState.snackbarHostState.showSnackbar(message)
                    }
                    else -> {
                        Unit
                    }
                }
            }
        }
    }
}
