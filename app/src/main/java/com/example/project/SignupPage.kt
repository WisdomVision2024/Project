package com.example.project

import ViewModels.Signup
import ViewModels.SignupUiState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun SignupPage(viewModel: Signup,
               navController: NavHostController
) {
    var account by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var repeatpassword by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var isVisuallyImpaired by remember { mutableStateOf(false) }
    val scaffoldState = rememberScaffoldState()
    val state = viewModel.registerState.collectAsState().value
    val isCorrect: Boolean = repeatpassword == password
    Surface(modifier = Modifier.fillMaxSize()) {
        Row {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.padding(12.dp))
                Text(
                    text = stringResource(R.string.input_phone_num),
                    fontSize = 20.sp, textAlign = TextAlign.Left
                )
                EditInputField(
                    value = account,
                    onValueChanged = { account = it },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next
                    ),
                    label = R.string.account,
                    modifier = Modifier
                )
                Spacer(modifier = Modifier.padding(8.dp))
                Text(
                    text = stringResource(R.string.input_user_name),
                    fontSize = 20.sp, textAlign = TextAlign.Left
                )
                EditInputField(
                    value = username,
                    onValueChanged = { username = it },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text, imeAction = ImeAction.Next
                    ),
                    label = R.string.username,
                    modifier = Modifier
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
                    modifier = Modifier
                )
                Spacer(modifier = Modifier.padding(8.dp))
                Text(
                    text = stringResource(R.string.repeat),
                    fontSize = 20.sp, textAlign = TextAlign.Left
                )
                PasswordInputField(
                    value = repeatpassword,
                    onValueChanged = { repeatpassword = it },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Next
                    ),
                    label = R.string.repeat,
                    modifier = Modifier
                )
                if (!isCorrect && repeatpassword.isNotEmpty()) {
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
                EditInputField(
                    value = email,
                    onValueChanged = { email = it },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Done
                    ),
                    label = R.string.phone_number,
                    modifier = Modifier
                )
                Spacer(modifier = Modifier.padding(12.dp))
                Text(
                    text = stringResource(R.string.isVisuallyImpaired),
                    fontSize = 20.sp, textAlign = TextAlign.Left
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    SingleSelectCheckbox(
                        isChecked = isVisuallyImpaired,
                        onCheckedChange = {isChecked->
                            isVisuallyImpaired = isChecked
                        },
                        text = stringResource(R.string.yes)
                    )
                    SingleSelectCheckbox(
                        isChecked = !isVisuallyImpaired,
                        onCheckedChange = {isChecked->
                            isVisuallyImpaired = isChecked
                        },
                        text = stringResource(R.string.no)
                    )
                }
                Spacer(modifier = Modifier.padding(30.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                    ,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = { navController.popBackStack() },
                        shape = CircleShape,
                        elevation = ButtonDefaults.buttonElevation(4.dp),
                        modifier = Modifier.size(96.dp)
                    )
                    {
                        Text(text = stringResource(R.string.back))
                    }
                    Button(
                        onClick = {
                            viewModel.signup(account, username, password, email, isVisuallyImpaired)
                        },
                        elevation = ButtonDefaults.buttonElevation(4.dp),
                        shape = CircleShape,
                        modifier = Modifier.size(96.dp)
                    ) {
                        Text(stringResource(R.string.sign_up))
                    }
                }
                LaunchedEffect(state) {
                    when (state) {
                        is SignupUiState.Success -> {
                            navController.navigate("LoginPage") {
                                popUpTo("SignupPage") {
                                    inclusive = true
                                }
                            }
                        }
                        is SignupUiState.Error -> {
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
}

