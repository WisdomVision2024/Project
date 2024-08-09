package com.example.project

import ViewModels.Arduino
import ViewModels.ArduinoUi
import ViewModels.Require
import ViewModels.TTS
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Text
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun Test(arduino: Arduino,tts: TTS){
    var success by remember {
        mutableStateOf(false)
    }
    var t by remember {
        mutableStateOf("")
    }
    var x by remember {
        mutableStateOf("")
    }
    var d by remember {
        mutableStateOf("")
    }
    var errorScreen by remember {
        mutableStateOf(false)
    }

    val distance=arduino.arduinoState.collectAsState().value

    LaunchedEffect(distance) {
        when(distance){
            is ArduinoUi.Success->{
                success=true
                d=(distance as ArduinoUi.Success).message.toString()
                tts.speak(d)
            }
            else->{success=false}
        }
    }
    if (errorScreen){
        ErrorMessageScreen(errorMessage = t, onClose = { errorScreen=false })
    }

    Column (modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center){
        Box (modifier = Modifier
            .size(200.dp)
            .background(Color(2, 115, 115))){
            if (success){
                Text(text = d, fontSize = 20.sp)
            }
        }
        EditInputField(label  =R.string.Description,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            value = t,
            onValueChanged = { t = it })
        Spacer(modifier = Modifier.padding(20.dp))
        Button(onClick = { arduino.getDistance()}){
            Text(text = stringResource(id = R.string.confirm))
        }
    }
}