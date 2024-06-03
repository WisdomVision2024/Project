package com.example.project

import Data.LoginState
import DataStore.LanguageSettingsStore
import DataStore.LoginDataStore
import Language.Language
import Language.LanguageSetting
import Language.LanguageManager
import ViewModels.Setting
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController

@Composable
fun SettingPage(viewModel:Setting,
                languageSettingsStore: LanguageSettingsStore,
                loginDataStore: LoginDataStore,
                navController: NavHostController,
                initialScreen: String? = null)
{
    val context = LocalContext.current
    val dataStore = loginDataStore.createLoginDataStore(context)
    val loginStateFlow = loginDataStore.loadLoginState(dataStore)
    val loginState by loginStateFlow.collectAsState(initial = LoginState(true))
    val individualised = loginState.currentUser?.isVisuallyImpaired
    var isLanguageChangeScreenVisible by remember { mutableStateOf(false) }
    var nameChangeScreenVisible by remember { mutableStateOf(false) }
    var passwordChangeScreenVisible by remember { mutableStateOf(false) }
    var emailChangeScreenVisible by remember { mutableStateOf(false) }
    val username by remember { mutableStateOf("") }
    val old by remember { mutableStateOf("") }
    val password by remember { mutableStateOf("") }
    val email by remember { mutableStateOf("") }
    val languageStore = languageSettingsStore.createLanguageSettingsStore(context)
    val languageSetting by languageSettingsStore.loadLanguageSettings(languageStore).collectAsState(
        initial = LanguageSetting(Language.English)
    )
    var currentLanguage by remember { mutableStateOf(languageSetting.language) }

    LaunchedEffect(Unit) {
        viewModel.initialize(context)
        initialScreen?.let {
            when (it) {
                "name" -> nameChangeScreenVisible = true
                "password" -> passwordChangeScreenVisible = true
                "email" -> emailChangeScreenVisible = true
                "language"->isLanguageChangeScreenVisible=true
            }
        }
    }

    CompositionLocalProvider(LocalContext provides LanguageManager.wrap(context, currentLanguage.locale)) {
        if (isLanguageChangeScreenVisible) {
            LanguageChangeScreen(
                onLanguageSelected = { selectedLanguage ->
                    viewModel.saveLanguageSettings(
                        languageSettingsStore, selectedLanguage,context
                    )
                    currentLanguage=selectedLanguage
                    isLanguageChangeScreenVisible = false
                },
                currentLanguage = currentLanguage
            )
        }
        else{
            SettingContent(
                navController = navController,
                { isLanguageChangeScreenVisible = true },
                { nameChangeScreenVisible = true },
                {passwordChangeScreenVisible=true},
                {emailChangeScreenVisible=true},
                individualised)
        }
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
}


@Composable
fun SettingContent(
    navController: NavHostController,
    onLanguageChangeRequested: () -> Unit,
    onNameChangeRequested:()->Unit,
    onPasswordChangeRequested:()->Unit,
    onEmailChangeRequested:()->Unit,
    individualised:Boolean?
){
    Scaffold (modifier = Modifier.fillMaxSize(),
        bottomBar = { if (individualised==true)Navigationbar(2,navController)
        else Navigationbar2(1, navController)
        })
    { padding ->
        Surface()
        {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.padding(20.dp))
                Text(
                    stringResource(id = R.string.user_setting),
                    fontSize = 24.sp,
                    textAlign = TextAlign.Start
                )
                Spacer(modifier = Modifier.padding(8.dp))
                Button(shape = RoundedCornerShape(8.dp),
                    elevation = ButtonDefaults.buttonElevation(4.dp),
                    modifier = Modifier.size(300.dp, 40.dp),
                    onClick = {onNameChangeRequested() })
                {
                    Text(
                        text = stringResource(id = R.string.change_user_name),
                        fontSize = 18.sp
                    )
                }
                Spacer(modifier = Modifier.padding(12.dp))
                Button(shape = RoundedCornerShape(8.dp),
                    elevation = ButtonDefaults.buttonElevation(4.dp),
                    modifier = Modifier.size(300.dp, 40.dp),
                    onClick = { onPasswordChangeRequested()})
                {
                    Text(
                        text = stringResource(id = R.string.change_password),
                        fontSize = 18.sp
                    )
                }
                Spacer(modifier = Modifier.padding(12.dp))
                Button(shape = RoundedCornerShape(8.dp),
                    elevation = ButtonDefaults.buttonElevation(4.dp),
                    modifier = Modifier.size(300.dp, 40.dp),
                    onClick = { onEmailChangeRequested()})
                {
                    Text(
                        text = stringResource(id = R.string.change_email),
                        fontSize = 18.sp
                    )
                }
                Spacer(modifier = Modifier.padding(30.dp))
                Text(
                    stringResource(id = R.string.other_set),
                    fontSize = 24.sp,
                    textAlign = TextAlign.Start
                )
                Spacer(modifier = Modifier.padding(12.dp))
                Button(
                    shape = RoundedCornerShape(8.dp),
                    elevation = ButtonDefaults.buttonElevation(4.dp),
                    modifier = Modifier.size(300.dp, 40.dp),
                    onClick = { onLanguageChangeRequested()})
                {
                    Text(
                        text = stringResource(id = R.string.change_language),
                        fontSize = 18.sp
                    )
                }
                Spacer(modifier = Modifier.padding(20.dp))
                Row(
                    modifier = Modifier

                        .padding(12.dp)
                )
                {
                    Button(
                        shape = RoundedCornerShape(8.dp),
                        elevation = ButtonDefaults.buttonElevation(4.dp),
                        onClick = { /*TODO*/ },
                        modifier = Modifier
                            .size(150.dp, 40.dp)
                    )
                    {
                        Text(text = stringResource(id = R.string.close_account))
                    }
                    Spacer(modifier = Modifier.padding(12.dp))
                    Button(
                        shape = RoundedCornerShape(8.dp),
                        elevation = ButtonDefaults.buttonElevation(4.dp),
                        onClick = { /*TODO*/ },
                        modifier = Modifier
                            .size(150.dp, 40.dp)
                    )
                    {
                        Text(text = stringResource(id = R.string.help))
                    }
                }
                Spacer(modifier = Modifier.padding(12.dp))
                Row(
                    modifier = Modifier
                        .padding(12.dp)
                )
                {
                    Button(
                        shape = RoundedCornerShape(8.dp),
                        elevation = ButtonDefaults.buttonElevation(4.dp),
                        onClick = { /*TODO*/ },
                        modifier = Modifier
                            .size(150.dp, 40.dp)
                    )
                    {
                        Text(text = stringResource(id = R.string.log_out))
                    }
                    Spacer(modifier = Modifier.padding(12.dp))
                    Button(
                        shape = RoundedCornerShape(8.dp),
                        elevation = ButtonDefaults.buttonElevation(4.dp),
                        onClick = { /*TODO*/ },
                        modifier = Modifier
                            .size(150.dp, 40.dp)
                    )
                    {
                        Text(text = stringResource(id = R.string.connect))
                    }
                }
            }
        }
    }
}
