package com.example.project

import DataStore.SpeedStore
import ViewModels.Signup
import ViewModels.SignupUiState
import ViewModels.TTS
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun SignupPage(
    viewModel: Signup,
    tts: TTS,
    speedStore: SpeedStore,
    navController: NavController
) {
    var account by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var repeatPassword by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var isVisuallyImpaired by remember { mutableStateOf(true) }
    val state = viewModel.registerState.collectAsState().value
    val isCorrect: Boolean = repeatPassword == password
    var errorMessageScreenVisible by remember { mutableStateOf(false) }
    var errorMessage by remember{ mutableStateOf("") }
    var isShowIntroduce1 by remember { mutableStateOf(false) }
    var isShowIntroduce2 by remember { mutableStateOf(false) }

    var nav1 by remember { mutableStateOf(false) }
    var nav2 by remember { mutableStateOf(false) }

    Surface(modifier = Modifier
        .fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color(242, 231, 220)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            item {
                Spacer(modifier = Modifier.padding(12.dp))
                Text(
                    text = stringResource(R.string.input_phone_num),
                    fontSize = 20.sp, textAlign = TextAlign.Left
                )
                EditInputField2(
                    value = account,
                    onValueChanged = { account = it },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next
                    ),
                    label = R.string.account,
                    modifier = Modifier.background(color = Color(169,217,208))
                )
                Spacer(modifier = Modifier.padding(8.dp))
                Text(
                    text = stringResource(R.string.input_user_name),
                    fontSize = 20.sp, textAlign = TextAlign.Left
                )
                EditInputField2(
                    value = username,
                    onValueChanged = { username = it },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text, imeAction = ImeAction.Next
                    ),
                    label = R.string.username,
                    modifier = Modifier.background(color = Color(169,217,208))
                )
                Spacer(modifier = Modifier.padding(8.dp))
                Text(
                    text = stringResource(R.string.input_password),
                    fontSize = 20.sp, textAlign = TextAlign.Left
                )
                PasswordInputField(
                    value = password,
                    onValueChanged = { password = it },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password, imeAction = ImeAction.Next
                    ),
                    label = R.string.password,
                    modifier = Modifier.background(color = Color(169,217,208))
                )
                Spacer(modifier = Modifier.padding(8.dp))
                Text(
                    text = stringResource(R.string.repeat),
                    fontSize = 20.sp, textAlign = TextAlign.Left
                )
                PasswordInputField(
                    value = repeatPassword,
                    onValueChanged = { repeatPassword = it },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password, imeAction = ImeAction.Next
                    ),
                    label = R.string.repeat,
                    modifier = Modifier.background(color = Color(169,217,208))
                )
                if (!isCorrect && repeatPassword.isNotEmpty()) {
                    Text(
                        text = "Different from Password!",
                        fontSize = 16.sp,
                        style = TextStyle(Color(255, 0, 0))
                    )
                }
                Spacer(modifier = Modifier.padding(8.dp))
                Text(
                    text = stringResource(R.string.input_email),
                    fontSize = 20.sp, textAlign = TextAlign.Left
                )
                EditInputField2(
                    value = email,
                    onValueChanged = { email = it },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email, imeAction = ImeAction.Done
                    ),
                    label = R.string.email,
                    modifier = Modifier.background(color = Color(169,217,208))
                )
                Spacer(modifier = Modifier.padding(12.dp))
                Text(
                    text = stringResource(R.string.isVisuallyImpaired),
                    fontSize = 20.sp, textAlign = TextAlign.Left
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    SingleSelectCheckbox(
                        isChecked = isVisuallyImpaired,
                        onCheckedChange = {
                            isVisuallyImpaired = true
                            Log.d("SignupPage", "isVisuallyImpaired set to true")
                                          },
                        text = stringResource(R.string.yes)
                    )
                    SingleSelectCheckbox(
                        isChecked = !isVisuallyImpaired,
                        onCheckedChange = {
                            isVisuallyImpaired = false
                            Log.d("SignupPage", "isVisuallyImpaired set to false")},
                        text = stringResource(R.string.no)
                    )
                }
                Spacer(modifier = Modifier.padding(20.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(onClick = { navController.popBackStack() },
                        colors = ButtonDefaults.buttonColors(Color(2,115,115)),
                        elevation = ButtonDefaults.buttonElevation(4.dp),
                        shape = CircleShape,
                        modifier = Modifier.size(96.dp)
                        )
                    {
                        Icon(painter = painterResource(id = R.drawable.arrowback_foreground),
                            contentDescription =stringResource(R.string.back),
                            tint = Color.White)
                    }
                    Button(
                        onClick =  {
                            viewModel.signup(
                                account,
                                username,
                                password,
                                email,
                                isVisuallyImpaired
                            ) },
                        colors = ButtonDefaults.buttonColors(Color(2,115,115)),
                        elevation = ButtonDefaults.buttonElevation(4.dp),
                        shape = CircleShape,
                        modifier = Modifier.size(96.dp)
                    ) {
                        Icon(painter = painterResource(id = R.drawable.arrowforward_foreground),
                            contentDescription =stringResource(R.string.sign_up),
                            tint = Color.White)
                    }
                }
            }
        }
    }
    LaunchedEffect(state) {
        when (state) {
            is SignupUiState.Success -> {
                if (isVisuallyImpaired)
                {
                    isShowIntroduce1=true
                }
                else {
                    isShowIntroduce2=true
                }

            }
            is SignupUiState.Error -> {
                val message = (state as SignupUiState.Error).message
                errorMessage=message
                errorMessageScreenVisible=true
            }
            else -> {
                Unit
            }
        }
    }
    if (errorMessageScreenVisible){
        ErrorMessageScreen(errorMessage,tts,
            onClose = {errorMessageScreenVisible=false})
    }
    if (isShowIntroduce1){
        IntroducePage_1(
            tts = tts,
            speedStore = speedStore,
            onClose = {
                isShowIntroduce1=false
                nav1=true
            }
        )
    }
    if (isShowIntroduce2){
        IntroducePage_2(
            onClose =
            {
                isShowIntroduce2=false
                nav2=true
            }
        )
    }
    LaunchedEffect(nav1) {
        if (nav1){
            navController.navigate("HomPage"){
                popUpTo("LoginPage"){ inclusive = true }
            }
            nav1=false
        }
    }
    LaunchedEffect(nav2) {
        if (nav2){
            navController.navigate("HelpListPage"){
                popUpTo("LoginPage"){ inclusive = true }
            }
            nav2=false
        }
    }
}