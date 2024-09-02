package com.example.project


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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController



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
    LaunchedEffect(Unit) {
        tts.speak(firstIntroduce)
    }
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(169, 217, 208),
                        Color(169, 217, 208),
                        Color(255, 255, 255)
                    )
                )
            )
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.padding(12.dp))
            Row (horizontalArrangement = Arrangement.SpaceBetween){
                Button(
                    onClick = { tts.speak(introduce) },
                    colors = ButtonDefaults.buttonColors(Color(2,115,115)),
                    elevation = ButtonDefaults.buttonElevation(4.dp),
                    shape = CircleShape,
                    modifier = Modifier
                        .size(96.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.PlayArrow,
                        contentDescription = stringResource(R.string.all_I)
                    )
                }

                Button(
                    onClick = { tts.speak(i2) },
                    colors = ButtonDefaults.buttonColors(Color(2,115,115)),
                    elevation = ButtonDefaults.buttonElevation(4.dp),
                    shape = CircleShape,
                    modifier = Modifier
                        .size(96.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.PlayArrow,
                        contentDescription = stringResource(R.string.button_I)
                    )
                }
            }
            Spacer(modifier = Modifier.padding(12.dp))
            Row (horizontalArrangement = Arrangement.SpaceBetween) {
                Button(
                    onClick = { tts.speak(i3) },
                    colors = ButtonDefaults.buttonColors(Color(2,115,115)),
                    elevation = ButtonDefaults.buttonElevation(4.dp),
                    shape = CircleShape,
                    modifier = Modifier
                        .size(96.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.PlayArrow,
                        contentDescription = stringResource(id = R.string.common_I)
                    )
                }

                Button(
                    onClick = { tts.speak(i4) },
                    colors = ButtonDefaults.buttonColors(Color(2,115,115)),
                    elevation = ButtonDefaults.buttonElevation(4.dp),
                    shape = CircleShape,
                    modifier = Modifier
                        .size(96.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.PlayArrow,
                        contentDescription = stringResource(id = R.string.continue_I)
                    )
                }
            }
            Row (horizontalArrangement = Arrangement.SpaceBetween) {
                Button(
                    onClick = { tts.speak(i5) },
                    colors = ButtonDefaults.buttonColors(Color(2,115,115)),
                    elevation = ButtonDefaults.buttonElevation(4.dp),
                    shape = CircleShape,
                    modifier = Modifier
                        .size(96.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.PlayArrow,
                        contentDescription = stringResource(id = R.string.emergency_I)
                    )
                }
                Button(
                    onClick = { tts.speak(i7) },
                    colors = ButtonDefaults.buttonColors(Color(2,115,115)),
                    elevation = ButtonDefaults.buttonElevation(4.dp),
                    shape = CircleShape,
                    modifier = Modifier
                        .size(96.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.PlayArrow,
                        contentDescription = stringResource(id = R.string.questionI)
                    )
                }
            }
            Box(contentAlignment = Alignment.BottomEnd){
                Button(
                    onClick = { navController.navigate("HomePage") },
                    colors = ButtonDefaults.buttonColors(Color(2,115,115)),
                    elevation = ButtonDefaults.buttonElevation(4.dp),
                    shape = CircleShape,
                    modifier = Modifier
                        .size(96.dp)
                ) {
                    Icon(imageVector = Icons.Filled.PlayArrow, contentDescription = "go to home page")
                }
            }
        }
    }
}

@Composable
fun IntroducePage_2(navController: NavController){
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(169, 217, 208),
                        Color(169, 217, 208),
                        Color(255, 255, 255)
                    )
                )
            )
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Box(modifier = Modifier.padding(top = 20.dp, start = 20.dp, end = 20.dp)) {
                Text(text = stringResource(id = R.string.introduce1), fontSize = 12.sp)
                Text(text = stringResource(id = R.string.introduce2), fontSize = 12.sp)
            }
        }
        Column(
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.End,
            modifier = Modifier.fillMaxSize()
        ) {
            Button(
                onClick = { navController.navigate("HelpListPage") },
                colors = ButtonDefaults.buttonColors(Color(2,115,115)),
                elevation = ButtonDefaults.buttonElevation(4.dp),
                shape = CircleShape,
                modifier = Modifier
                    .size(96.dp)
            ) {
                Icon(painter = painterResource(id = R.drawable.arrowforward_foreground),
                    contentDescription = "")
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
    val navController= rememberNavController()
    IntroducePage_2(navController = navController)
}


