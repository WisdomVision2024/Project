package com.example.project

import ViewModels.Help
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay

@Composable
fun HelpPage(id:String ,name: String, description: String, address: String,
             viewModel:Help,navController:NavController) {
    val showFinishScreen by viewModel.showFinishScreen.observeAsState(false)
    if(showFinishScreen) {
        FinishScreen(onClose = {viewModel.closeScreen()})
        LaunchedEffect(Unit) {
            delay(3000)
            viewModel.closeScreen()
            navController.navigate("HelpListPage")
        }
    }
    Scaffold (modifier = Modifier.fillMaxSize(),
    topBar ={
        Box(modifier = Modifier.fillMaxWidth(),
            contentAlignment= Alignment.TopEnd)
        {
            IconButton(onClick = { navController.navigate("SettingPage") }
            ) {
                Icon(imageVector = Icons.Filled.Settings,
                    contentDescription = stringResource(id = R.string.setting_page),
                    tint = Color(2,115,115),
                    modifier = Modifier.size(30.dp))
            }
        }
    }
    ){
        padding->
        Column(modifier = Modifier
            .padding(padding)
            .background(color = Color(169, 217, 208))
            .fillMaxSize(),
            ) {
            Box(modifier = Modifier
                .fillMaxWidth()
                .padding(40.dp),
                contentAlignment = Alignment.CenterStart) {
                Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
                    Row (modifier = Modifier.padding(bottom = 12.dp)){
                        Text(text = stringResource(id = R.string.Client)+":  ",
                            fontSize = 20.sp, color = Color.Black,
                            )
                        Box(modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(242, 231, 220))
                            .fillMaxWidth()
                            .size(35.dp)){
                            Text(text = name,
                                fontSize = 20.sp, color = Color.Black,
                                modifier = Modifier.padding(start = 12.dp))
                        }
                    }
                    Text(
                        text = stringResource(id = R.string.Description) + ":",
                        fontSize = 20.sp, color = Color.Black,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Box(modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(242, 231, 220))
                        .fillMaxWidth()
                        .size(150.dp)) {
                        Text(
                            text = description,
                            fontSize = 20.sp, color = Color.Black,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                    Spacer(modifier = Modifier.padding(bottom = 12.dp))
                    Text(text = stringResource(id = R.string.Location)+": ",
                        fontSize = 20.sp, color = Color.Black,
                        modifier = Modifier.padding(bottom = 4.dp))
                    Box(modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(242, 231, 220))
                        .fillMaxWidth()
                        .size(150.dp)){
                        Text(text = address,
                            fontSize = 20.sp, color = Color.Black,
                            modifier = Modifier.padding(start = 8.dp))
                    }
                }
            }
            Row (modifier = Modifier.fillMaxWidth(),
                horizontalArrangement=Arrangement.Center){
                Button(onClick = { },
                    colors =ButtonDefaults.buttonColors( Color(2,115,115)),
                    modifier = Modifier.size(100.dp,60.dp)
                    ) {
                    Text(text = stringResource(id = R.string.cancel),
                        fontSize = 16.sp, textAlign = TextAlign.Center)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HelpPagePreview(){
    val navController = rememberNavController()
    HelpPage("1","Lily","find my pen", "MeetingRoom No.3"
        , Help(),navController)
}