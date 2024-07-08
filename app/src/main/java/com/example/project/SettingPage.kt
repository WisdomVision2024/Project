package com.example.project

import DataStore.LoginDataStore
import DataStore.LoginState
import ViewModels.Setting
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun SettingPage(viewModel:Setting,
                loginDataStore: LoginDataStore,
                navController: NavController)
{
    val context = LocalContext.current
    val loginStateFlow = loginDataStore.loadLoginState()
    val loginState by loginStateFlow.collectAsState(initial = LoginState(true))
    val individualised = loginState.currentUser?.isVisuallyImpaired
    var isLanguageChangeScreenVisible by remember { mutableStateOf(false) }
    var nameChangeScreenVisible by remember { mutableStateOf(false) }
    var passwordChangeScreenVisible by remember { mutableStateOf(false) }
    var emailChangeScreenVisible by remember { mutableStateOf(false) }
    var logOutScreenVisible by remember { mutableStateOf(false) }
    val username by remember { mutableStateOf("") }
    val old by remember { mutableStateOf("") }
    val password by remember { mutableStateOf("") }
    val email by remember { mutableStateOf("") }

    if (isLanguageChangeScreenVisible) {
        LanguageChangeScreen(
            onClose = {isLanguageChangeScreenVisible=false}
        )
        }
    if (nameChangeScreenVisible){
        NameChangeScreen(
            viewModel = viewModel,
            name =username,
            onClose = { nameChangeScreenVisible = false }
        )
    }
    if (passwordChangeScreenVisible){
    PasswordChangeScreen(viewModel = viewModel, old = old,
        new = password,
        onClose = {passwordChangeScreenVisible=false})
    }
    if (emailChangeScreenVisible){
        EmailChangeScreen(viewModel = viewModel,
            email=email,
            onClose = {emailChangeScreenVisible=false})
    }
    if (logOutScreenVisible){
        LogOutScreen(viewModel = viewModel,
            navController = navController,
            onClose = {logOutScreenVisible=false})
    }
    Scaffold (modifier = Modifier.fillMaxSize(),
        bottomBar = { if (individualised==true)Navigationbar(2,navController)
        else Navigationbar2(1, navController)
        })
    { padding ->
        Surface()
        {
            LazyColumn(
                modifier = Modifier
                    .background(color = Color(242, 231, 220))
                    .padding(padding)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Spacer(modifier = Modifier.padding(20.dp))
                    Text(
                        stringResource(id = R.string.user_setting),
                        fontSize = 28.sp,
                        textAlign = TextAlign.Start
                    )
                    Spacer(modifier = Modifier.padding(8.dp))
                    Button(shape = RoundedCornerShape(8.dp),
                        elevation = ButtonDefaults.buttonElevation(4.dp),
                        colors = ButtonDefaults.buttonColors(Color(3,140,127)),
                        modifier = Modifier.size(300.dp, 40.dp),
                        onClick = {nameChangeScreenVisible=true})
                    {
                        Text(
                            text = stringResource(id = R.string.change_user_name),
                            fontSize = 18.sp
                        )
                    }
                    Spacer(modifier = Modifier.padding(12.dp))
                    Button(shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(Color(3,140,127)),
                        elevation = ButtonDefaults.buttonElevation(4.dp),
                        modifier = Modifier
                            .size(300.dp, 44.dp),
                        onClick = { passwordChangeScreenVisible=true})
                    {
                        Text(
                            text = stringResource(id = R.string.change_password),
                            fontSize = 18.sp
                        )
                    }
                    Spacer(modifier = Modifier.padding(12.dp))
                    Button(shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(Color(3,140,127)),
                        elevation = ButtonDefaults.buttonElevation(4.dp),
                        modifier = Modifier
                            .size(300.dp, 44.dp),
                        onClick = { emailChangeScreenVisible=true})
                    {
                        Text(
                            text = stringResource(id = R.string.change_email),
                            fontSize = 18.sp
                        )
                    }
                    Spacer(modifier = Modifier.padding(30.dp))
                    Text(
                        stringResource(id = R.string.other_set),
                        fontSize = 28.sp,
                        textAlign = TextAlign.Start
                    )
                    Spacer(modifier = Modifier.padding(12.dp))
                    Button(
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(Color(3,140,127)),
                        elevation = ButtonDefaults.buttonElevation(4.dp),
                        modifier = Modifier
                            .size(300.dp, 44.dp),
                        onClick = {isLanguageChangeScreenVisible=true})
                    {
                        Text(
                            text = stringResource(id = R.string.change_language),
                            fontSize = 18.sp
                        )
                    }
                    Spacer(modifier = Modifier.padding(12.dp))
                    Button(
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(Color(3,140,127)),
                        elevation = ButtonDefaults.buttonElevation(4.dp),
                        onClick = { /*TODO*/ },
                        modifier = Modifier
                            .size(300.dp, 44.dp)
                    )
                    {
                        Text(text = stringResource(id = R.string.help),
                            fontSize = 18.sp)
                    }
                    Spacer(modifier = Modifier.padding(32.dp))
                    Button(
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(Color(3,140,127)),
                        elevation = ButtonDefaults.buttonElevation(4.dp),
                        onClick = { /*TODO*/ },
                        modifier = Modifier
                            .size(300.dp, 44.dp)
                    )
                    {
                        Text(text = stringResource(id = R.string.close_account),
                            fontSize = 18.sp)
                    }
                    Spacer(modifier = Modifier.padding(24.dp))
                    Button(
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(Color(3,140,127)),
                        elevation = ButtonDefaults.buttonElevation(4.dp),
                        onClick = { logOutScreenVisible=true },
                        modifier = Modifier
                            .size(300.dp, 44.dp)
                    )
                    {
                        Text(text = stringResource(id = R.string.log_out),
                            fontSize = 18.sp)
                    }
                }
            }
        }
    }
}
