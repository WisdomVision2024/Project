package com.example.project

import DataStore.LanguageSettingsStore
import Language.Language
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
import androidx.compose.runtime.LaunchedEffect
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

@Composable
fun SettingPage(viewModel:Setting,
                languageSettingsStore: LanguageSettingsStore,
                navController: NavController)
{
    val context = LocalContext.current
    var isLanguageChangeScreenVisible by remember { mutableStateOf(false) }
    var currentLanguage by remember { mutableStateOf<Language?>(Language.English) }
    LaunchedEffect(Unit) {
        viewModel.initialize(context)
    }
    if (isLanguageChangeScreenVisible) {
        currentLanguage?.let {
            LanguageChangeScreen(
                onLanguageSelected = { selectedLanguage ->
                    viewModel.SaveLanguageSettings(
                        languageSettingsStore, selectedLanguage
                    )
                    isLanguageChangeScreenVisible = false
                },
                currentLanguage = currentLanguage
            )
        }
    }
    else{
        SettingContent(
            language = currentLanguage ?: Language.English,
            navController = navController,
            onLanguageChangeRequested = { isLanguageChangeScreenVisible=true})
    }
}


@Composable
fun SettingContent(
    language: Language,
    navController: NavController,
    onLanguageChangeRequested: () -> Unit
){
    val isvisualimpired = false
    Scaffold (modifier = Modifier.fillMaxSize(),
        bottomBar = { if (isvisualimpired)Navigationbar(2,navController)
        else Navigationbar2(1, navController)
        })
    { innerPadding ->
        println(innerPadding)
        Surface()
        {
            Column(
                modifier = Modifier.fillMaxSize(),
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
                    onClick = { /*TODO*/ })
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
                    onClick = { /*TODO*/ })
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
                    onClick = { /*TODO*/ })
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
