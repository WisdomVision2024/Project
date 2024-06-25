package com.example.project

import DataStore.LanguageSettingsStore
import Language.Language
import Language.LanguageManager
import Language.LanguageSetting
import ViewModels.Signup
import ViewModels.SignupUiState
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
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun SignupPage(
    viewModel: Signup,
    languageSettingsStore: LanguageSettingsStore,
    navController: NavHostController
) {
    var account by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var repeatPassword by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var isVisuallyImpaired by remember { mutableStateOf(false) }
    val scaffoldState = rememberScaffoldState()
    val state = viewModel.registerState.collectAsState().value
    val isCorrect: Boolean = repeatPassword == password
    val context = LocalContext.current
    val languageStore = languageSettingsStore.createLanguageSettingsStore(context)
    val languageSetting by languageSettingsStore.loadLanguageSettings(languageStore)
        .collectAsState(initial = LanguageSetting(Language.English))
    var currentLanguage by remember { mutableStateOf(languageSetting.language) }
    var isLanguageChangeScreenVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.initialize(context)
    }

    CompositionLocalProvider(
        LocalContext provides LanguageManager.wrap(
            context,
            currentLanguage.locale
        )
    ) {
        if (isLanguageChangeScreenVisible) {
            LanguageChangeScreen(
                onLanguageSelected = { selectedLanguage ->
                    viewModel.saveLanguageSettings(
                        languageSettingsStore, selectedLanguage, context
                    )
                    currentLanguage = selectedLanguage
                    isLanguageChangeScreenVisible = false
                },
                currentLanguage = currentLanguage
            )
        } else {
            SignUpContent(
                account = account,
                onAccountChange = { account = it },
                username = username,
                onUsernameChange = { username = it },
                password = password,
                onPasswordChange = { password = it },
                repeatPassword = repeatPassword,
                onRepeatPasswordChange = { repeatPassword = it },
                email = email,
                onEmailChange = { email = it },
                isVisuallyImpaired = isVisuallyImpaired,
                onVisuallyImpairedChange = { isVisuallyImpaired = it },
                onSignupClick = {
                    viewModel.signup(
                        account,
                        username,
                        password,
                        email,
                        isVisuallyImpaired
                    )
                },
                onBackClick = { navController.popBackStack() },
                onLanguageChangeClick = { isLanguageChangeScreenVisible = true },
                isCorrect = isCorrect
            )

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

@Composable
fun SignUpContent(
    account: String,
    onAccountChange: (String) -> Unit,
    username: String,
    onUsernameChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    repeatPassword: String,
    onRepeatPasswordChange: (String) -> Unit,
    email: String,
    onEmailChange: (String) -> Unit,
    isVisuallyImpaired: Boolean,
    onVisuallyImpairedChange: (Boolean) -> Unit,
    onSignupClick: () -> Unit,
    onBackClick: () -> Unit,
    onLanguageChangeClick: () -> Unit,
    isCorrect: Boolean
) {
    Surface(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            item {
                Spacer(modifier = Modifier.padding(12.dp))
                Button(
                    shape = RoundedCornerShape(8.dp),
                    elevation = ButtonDefaults.buttonElevation(4.dp),
                    modifier = Modifier.size(300.dp, 40.dp),
                    onClick = onLanguageChangeClick
                ) {
                    Text(
                        text = stringResource(id = R.string.change_language),
                        fontSize = 18.sp
                    )
                }
                Spacer(modifier = Modifier.padding(12.dp))
                Text(
                    text = stringResource(R.string.input_phone_num),
                    fontSize = 20.sp, textAlign = TextAlign.Left
                )
                EditInputField(
                    value = account,
                    onValueChanged = onAccountChange,
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
                    onValueChanged = onUsernameChange,
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
                    onValueChanged = onPasswordChange,
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
                    value = repeatPassword,
                    onValueChanged = onRepeatPasswordChange,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password, imeAction = ImeAction.Next
                    ),
                    label = R.string.repeat,
                    modifier = Modifier
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
                EditInputField(
                    value = email,
                    onValueChanged = onEmailChange,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email, imeAction = ImeAction.Done
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
                        onCheckedChange = { onVisuallyImpairedChange(true) },
                        text = stringResource(R.string.yes)
                    )
                    SingleSelectCheckbox(
                        isChecked = !isVisuallyImpaired,
                        onCheckedChange = { onVisuallyImpairedChange(false) },
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
                    Button(
                        onClick = onBackClick,
                        shape = CircleShape,
                        elevation = ButtonDefaults.buttonElevation(4.dp),
                        modifier = Modifier.size(96.dp)
                    ) {
                        Text(text = stringResource(R.string.back))
                    }
                    Button(
                        onClick = onSignupClick,
                        elevation = ButtonDefaults.buttonElevation(4.dp),
                        shape = CircleShape,
                        modifier = Modifier.size(96.dp)
                    ) {
                        Text(stringResource(R.string.sign_up))
                    }
                }
            }
        }
    }
}
