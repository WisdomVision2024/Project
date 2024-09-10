package com.example.project


import DataStore.LoginDataStore
import DataStore.LoginState
import ViewModels.TTS
import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay


@Composable
fun IntroducePage_1(tts: TTS,navController: NavController){
    val i1=stringResource(id = R.string.introduce1)
    val i2=stringResource(id = R.string.introduce_isV2)
    val i3=stringResource(id = R.string.introduce_isV3)
    val i4=stringResource(id = R.string.introduce_isV4)
    val i5= stringResource(id = R.string.introduce_isV5)
    val i6= stringResource(id = R.string.introduce_isV6)
    val i7= stringResource(id = R.string.introduce_isV7)
    val introduce=i1+i2+i3+i4+i6+i7
    val firstIntroduce= stringResource(id = R.string.introduce3)
    var isSpeaking1 by remember { mutableStateOf(false) }
    var isSpeaking2 by remember { mutableStateOf(false) }
    var isSpeaking3 by remember { mutableStateOf(false) }
    var isSpeaking4 by remember { mutableStateOf(false) }
    var isSpeaking5 by remember { mutableStateOf(false) }
    var isSpeaking6 by remember { mutableStateOf(false) }
    var nav  by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        tts.setOnInitListener {
            tts.speak(firstIntroduce)
        }
    }
    LaunchedEffect(nav) {
        if (nav){
            navController.navigate("HomePage")
        }
        nav=false
    }
    Surface(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(255, 255, 255),
                            Color(169, 217, 208)
                        )
                    )
                )
        ) {
            Spacer(modifier = Modifier.padding(12.dp))
            Text(stringResource(R.string.help ), fontSize = 20.sp, color = Color(2,115,115))
            Spacer(modifier = Modifier.padding(12.dp))
            Row (modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround){
                Button(
                    onClick = {
                        isSpeaking1 = if (isSpeaking1){
                            tts.stop()
                            false
                        } else{
                            tts.speak(introduce)
                            true
                        }
                              },
                    colors = ButtonDefaults.buttonColors(Color(2,115,115)),
                    elevation = ButtonDefaults.buttonElevation(12.dp),
                    shape = CircleShape,
                    modifier = Modifier
                        .size(80.dp)
                ) {
                    if (isSpeaking1){
                        androidx.compose.material3.Icon(
                            painter = painterResource(id = R.drawable.square_foreground),
                            contentDescription = "Stop"
                        )
                    }else{
                        Icon(
                            imageVector = Icons.Filled.PlayArrow,
                            contentDescription = stringResource(R.string.all_I),
                            tint = Color.White
                        )
                    }
                }
                Button(
                    onClick = {
                        isSpeaking2 =
                            if (isSpeaking2){
                                tts.stop()
                                false
                            } else{
                                tts.speak(i2)
                                true
                            }
                         },
                    colors = ButtonDefaults.buttonColors(Color(2,115,115)),
                    elevation = ButtonDefaults.buttonElevation(12.dp),
                    shape = CircleShape,
                    modifier = Modifier
                        .size(80.dp)
                ) {
                    if (isSpeaking2){
                        androidx.compose.material3.Icon(
                            painter = painterResource(id = R.drawable.square_foreground),
                            contentDescription = "Stop"
                        )
                    }
                    else{
                        Icon(
                            imageVector = Icons.Filled.PlayArrow,
                            contentDescription = stringResource(R.string.button_I),
                            tint = Color.White
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.padding(12.dp))
            Row (modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround) {
                Button(
                    onClick = {
                        isSpeaking3 =
                            if (isSpeaking3){
                                tts.stop()
                                false
                            } else{
                                tts.speak(i3)
                                true
                            }
                              },
                    colors = ButtonDefaults.buttonColors(Color(2,115,115)),
                    elevation = ButtonDefaults.buttonElevation(12.dp),
                    shape = CircleShape,
                    modifier = Modifier
                        .size(80.dp)
                ) {
                    if (isSpeaking3){
                        androidx.compose.material3.Icon(
                            painter = painterResource(id = R.drawable.square_foreground),
                            contentDescription = "Stop"
                        )
                    }else{
                        Icon(
                            imageVector = Icons.Filled.PlayArrow,
                            contentDescription = stringResource(id = R.string.common_I),
                            tint = Color.White
                        )
                    }
                }
                Button(
                    onClick = {
                        isSpeaking4 =
                            if (isSpeaking4){
                                tts.stop()
                                false
                            } else{
                                tts.speak(i4)
                                true
                            }
                              },
                    colors = ButtonDefaults.buttonColors(Color(2,115,115)),
                    elevation = ButtonDefaults.buttonElevation(12.dp),
                    shape = CircleShape,
                    modifier = Modifier
                        .size(80.dp)
                ) {
                    if (isSpeaking4){
                        androidx.compose.material3.Icon(
                            painter = painterResource(id = R.drawable.square_foreground),
                            contentDescription = "Stop"
                        )
                    }else{
                        Icon(
                            imageVector = Icons.Filled.PlayArrow,
                            contentDescription = stringResource(id = R.string.continue_I),
                            tint = Color.White
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.padding(12.dp))
            Row (modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround) {
                Button(
                    onClick = {
                        isSpeaking5 =
                            if (isSpeaking5){
                                tts.stop()
                                false
                            } else{
                                tts.speak(i5)
                                true
                            }
                         },
                    colors = ButtonDefaults.buttonColors(Color(2,115,115)),
                    elevation = ButtonDefaults.buttonElevation(12.dp),
                    shape = CircleShape,
                    modifier = Modifier
                        .size(80.dp)
                ) {
                    if (isSpeaking5){
                    androidx.compose.material3.Icon(
                        painter = painterResource(id = R.drawable.square_foreground),
                        contentDescription = "Stop")
                    }
                    else{
                        Icon(
                            imageVector = Icons.Filled.PlayArrow,
                            contentDescription = stringResource(id = R.string.emergency_I),
                            tint = Color.White
                        )
                    }
                }
                Button(
                    onClick = {
                        isSpeaking6 =
                            if (isSpeaking6){
                                tts.stop()
                                false
                            } else{
                                tts.speak(i7)
                                true
                            }
                       },
                    colors = ButtonDefaults.buttonColors(Color(2,115,115)),
                    elevation = ButtonDefaults.buttonElevation(12.dp),
                    shape = CircleShape,
                    modifier = Modifier
                        .size(80.dp)
                ) {
                    if (isSpeaking6){
                        androidx.compose.material3.Icon(
                            painter = painterResource(id = R.drawable.square_foreground),
                            contentDescription = "Stop"
                        )
                    }else{
                        Icon(
                            imageVector = Icons.Filled.PlayArrow,
                            contentDescription = stringResource(id = R.string.questionI),
                            tint = Color.White
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.padding(24.dp))
            Box(modifier = Modifier
                .fillMaxWidth()
                .padding(end = 8.dp),
                contentAlignment = Alignment.BottomEnd){
                Button(
                    onClick = { nav=true },
                    colors = ButtonDefaults.buttonColors(Color(2,115,115)),
                    elevation = ButtonDefaults.buttonElevation(12.dp),
                    shape = CircleShape,
                    modifier = Modifier
                        .size(96.dp)
                ) {
                    Icon(painter = painterResource(id = R.drawable.arrowforward_foreground),
                        contentDescription = "go to home page",
                        tint = Color.White)
                }
            }
        }
    }
}

@Composable
fun IntroducePage_2(navController: NavController){
    var back  by remember { mutableStateOf(false) }
    var nav by remember { mutableStateOf(false) }
    LaunchedEffect(nav) {
        if (nav){
            navController.navigate("HelpListPage")
        }
        nav=false
    }
    LaunchedEffect(back) {
        if (back){
            navController.navigate("LoginPage")
        }
        back=false
    }
    Surface(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(169, 217, 208),
                            Color(255, 255, 255)
                        )
                    )
                )
        ) {
            LazyColumn {
                item {
                    Text(
                        text = stringResource(id = R.string.introduce1),
                        fontSize = 20.sp,
                        modifier = Modifier.padding(12.dp)
                    )
                    Text(
                        text = stringResource(id = R.string.introduce2),
                        fontSize = 20.sp,
                        modifier = Modifier.padding(12.dp)
                    )
                    Text(
                        text = stringResource(id = R.string.introduce4),
                        fontSize = 20.sp,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 8.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Button(
                    onClick = {back=true},
                    colors = ButtonDefaults.buttonColors(Color(2, 115, 115)),
                    elevation = ButtonDefaults.buttonElevation(12.dp),
                    shape = CircleShape,
                    modifier = Modifier
                        .size(96.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.arrowback_foreground),
                        contentDescription = "go to login page",
                        tint = Color.White
                    )
                }
                Button(
                    onClick = { nav=true },
                    colors = ButtonDefaults.buttonColors(Color(2, 115, 115)),
                    elevation = ButtonDefaults.buttonElevation(12.dp),
                    shape = CircleShape,
                    modifier = Modifier
                        .size(96.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.arrowforward_foreground),
                        contentDescription = "go to home page",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Introduce_1Preview() {
    val navController= rememberNavController()
    IntroducePage_1(tts = TTS(Application()), navController = navController)
}
@Preview(showBackground = true)
@Composable
fun Introduce_2Preview() {
    val loginDataStore=LoginDataStore(LocalContext.current)
    val navController= rememberNavController()
    IntroducePage_2(navController = navController)
}


