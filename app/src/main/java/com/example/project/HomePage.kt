
package com.example.project

import ViewModels.HandleResult
import ViewModels.Identified
import ViewModels.Setting
import ViewModels.UploadState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun HomePage(
    androidViewModel: Identified,
    viewModel: Setting,
    navController: NavController
) {
    val upLoadResponseState = androidViewModel.uploadState.collectAsState().value
    val handleState by androidViewModel.handleResult.collectAsState()
    val state by androidViewModel.state.collectAsState()
    var response by remember { mutableStateOf("")  }
    var isLanguageChangeScreenVisible by remember { mutableStateOf(false) }
    var nameChangeScreenVisible by remember { mutableStateOf(false) }
    var passwordChangeScreenVisible by remember { mutableStateOf(false) }
    var emailChangeScreenVisible by remember { mutableStateOf(false) }
    val username by remember { mutableStateOf("") }
    val old by remember { mutableStateOf("") }
    val password by remember { mutableStateOf("") }
    val email by remember { mutableStateOf("") }
    var uploadResponseState by remember { mutableStateOf(false) }
    val text=state.spokenText.ifEmpty { "" }
    LaunchedEffect (handleState){
        when(handleState){
            is HandleResult.LanguageChange->isLanguageChangeScreenVisible=true
            is HandleResult.NameChange->nameChangeScreenVisible=true
            is HandleResult.PasswordChange->passwordChangeScreenVisible=true
            is HandleResult.EmailChange->emailChangeScreenVisible=true
            is HandleResult.NavigateSetting->{navController.navigate("SettingPage")}
            is HandleResult.NavigateRequest->{navController.navigate("RequestPage")}
            is HandleResult.Upload->uploadResponseState=true
            else->{Unit}
        }
    }
    LaunchedEffect(upLoadResponseState) {
        when(upLoadResponseState){
            is UploadState.Success->{
                response= (upLoadResponseState as UploadState.Success).result.toString()
            }
            is UploadState.Error->{
                response= (upLoadResponseState as UploadState.Error).message
            }
            else->{
                Unit
            }
        }
    }
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
    Scaffold(modifier = Modifier.fillMaxSize(),
        bottomBar = {
            Navigationbar(1, navController)
        })
    { padding ->
        Surface {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.padding(12.dp))
                if (uploadResponseState) {
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
                        item {
                        Text(text = response ,
                            fontSize = 20.sp,
                            textAlign = TextAlign.Justify,
                            modifier = Modifier.padding(20.dp)
                        )
                        }
                    }
                }
                Box(
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Button( onClick = {
                        if (state.isSpeaking) {
                            androidViewModel.stopListening()
                            androidViewModel.handle()
                        } else {
                            androidViewModel.startListening()
                        }
                    },
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(Color(255, 0, 0)),
                        elevation = ButtonDefaults.buttonElevation(4.dp),
                        modifier = Modifier.size(96.dp)
                        )
                    {
                        if (!state.isSpeaking) {
                            Icon(
                                painter =
                                painterResource(id = R.drawable.mic_foreground),
                                contentDescription = "Start"
                            )
                        } else {
                            Icon(
                                painter = painterResource(id = R.drawable.square_foreground),
                                contentDescription = "Stop"
                            )
                        }
                    }
                    Spacer(modifier =Modifier.padding(40.dp))
                }
            }
        }
    }
}