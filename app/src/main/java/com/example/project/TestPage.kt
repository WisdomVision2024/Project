package com.example.project

import ViewModels.BlueTooth
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType

@Composable
fun TestPage(blueTooth: BlueTooth){
    val context= LocalContext.current
    val blueToothState by blueTooth.newMessageReceived.observeAsState()
    var distance by remember { mutableStateOf("")  }
    var input by remember {
        mutableStateOf("")
    }
    LaunchedEffect(Unit) {
        blueTooth.startDeviceScan()
    }
    LaunchedEffect(blueToothState) {
        blueToothState?.let {
            distance=it
        }
    }
    val value = distance
    val floatValue = if (value.isNotEmpty()) {
        distance.toFloat()
    } else {
        // 設置一個默認值或處理空字符串的情況
        0.0f
    }
    if (distance.toFloat()<80.0){
        blueTooth.playAlertSound(context)
    }
    Column(modifier = Modifier.fillMaxSize()) {
        Text(text = "距離: $distance")
        EditInputField(
            label = R.string.help,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            value = input,
            onValueChanged = { input = it }
        )
        Button(onClick = { blueTooth.sendData(input) }) {
            Text(text = stringResource(id = R.string.confirm))
        }
    }
}