package com.example.project

import Data.HelpRequest
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun HelpPage(id:String ,name: String, description: String, address: String,navController:NavController) {
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
            .fillMaxSize()
            .background(color = Color(242, 231, 220)),
            horizontalAlignment=Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
            ) {
            Row {
                Text(text = stringResource(id = R.string.Client)+":",
                    fontSize = 16.sp, color = Color.Black,
                    modifier = Modifier.padding(bottom = 4.dp))
                Text(text = name,
                    fontSize = 16.sp, color = Color.Black,
                    modifier = Modifier.padding(bottom = 4.dp))
            }
            Spacer(modifier = Modifier.padding(20.dp))
            Text(text = stringResource(id = R.string.Description)+":",
                fontSize = 16.sp, color = Color.Black,
                modifier = Modifier.padding(bottom = 4.dp))
            Text(text = description,
                fontSize = 16.sp, color = Color.Black,
                modifier = Modifier.padding(bottom = 4.dp))
            Spacer(modifier = Modifier.padding(20.dp))
            Row {
                Text(text = stringResource(id = R.string.Location)+":",
                    fontSize = 16.sp, color = Color.Black,
                    modifier = Modifier.padding(bottom = 4.dp))
                Text(text = address,
                    fontSize = 16.sp, color = Color.Black,
                    modifier = Modifier.padding(bottom = 4.dp))
            }
        }
    }
}