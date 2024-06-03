package com.example.project

import DataStore.LanguageSettingsStore
import Language.Language
import Language.LanguageSetting
import ViewModels.Identified
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun HomePage(
    androidViewModel: Identified,
    languageSettingsStore: LanguageSettingsStore,
    navController: NavHostController
) {
    val context = LocalContext.current
    val languageStore = languageSettingsStore.createLanguageSettingsStore(context)
    val languageSetting by languageSettingsStore.loadLanguageSettings(languageStore)
        .collectAsState(initial = LanguageSetting(Language.English))
    val identifiedResponse by androidViewModel.uploadState.collectAsState()
    val state by androidViewModel.state.collectAsState()
    val text=state.spokenText.ifEmpty { "" }

    Scaffold(modifier = Modifier.fillMaxSize(),
        bottomBar = {
            Navigationbar(1, navController)
        })
    { padding ->
        Surface {
            Column(
                Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    Column(
                        Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.padding(12.dp))
                        Text(
                            text = text,
                            fontSize = 20.sp,
                            textAlign = TextAlign.Justify,
                            modifier = Modifier.padding(20.dp)
                        )
                        LazyColumn(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .width(320.dp)
                                .height(320.dp)
                        ) {
                            items(identifiedResponse) { item ->
                                item?.let {
                                    Text(
                                        text = it,
                                        fontSize = 20.sp,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Button(
                            shape = if (!state.isSpeaking) CircleShape else RectangleShape,
                            elevation = ButtonDefaults.buttonElevation(4.dp),
                            onClick = {
                                if (state.isSpeaking) {
                                    androidViewModel.stopListening()
                                    androidViewModel.processRecognizedText(text = text,navController)
                                } else {
                                    androidViewModel.startListening(languageSetting.locale.toString())
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Red,
                                contentColor = Color.White
                            ),
                            modifier = Modifier.size(96.dp)
                        ) {
                            Text(text = if (state.isSpeaking) "Stop" else "Start")
                        }
                    }
                }
            }
        }
    }
}