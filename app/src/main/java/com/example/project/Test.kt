package com.example.project

import ViewModels.Identified
import ViewModels.UploadState
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
fun Test(viewModel:Identified){
    var success by remember {
        mutableStateOf(false)
    }
    var t by remember {
        mutableStateOf("")
    }
    var x by remember {
        mutableStateOf("")
    }
    val state=viewModel.uploadState.collectAsState().value
    LaunchedEffect(state) {
        when(state){
            is UploadState.Success->{
                success=true
                x=state.result.toString()}
            else->{Unit}
        }
    }
    Column (modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center){
        Box (modifier = Modifier
            .size(200.dp)
            .background(Color(2, 115, 115))){
            if (success){
                Text(text = x, fontSize = 20.sp)
            }
        }
        EditInputField(label = R.string.Description,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            value = t,
            onValueChanged = { t = it })
        Spacer(modifier = Modifier.padding(20.dp))
        Button(onClick = { viewModel.upLoad(t) }) {
            Text(text = stringResource(id = R.string.confirm))
        }
    }
}