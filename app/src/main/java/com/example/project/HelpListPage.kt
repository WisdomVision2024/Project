package com.example.project

import Data.HelpRequest
import ViewModels.HelpList
import ViewModels.HelpUiState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@Composable
fun HelpListPage(viewModel:HelpList,navController: NavController) {
    val helpListState =viewModel.helpListState.collectAsState().value
    var state by remember { mutableStateOf(false) }
    LaunchedEffect (helpListState){
        when(helpListState){
            is HelpUiState.Success->{
                state=true
            }
            else ->{Unit}
        }
    }
    if (state){
        SuccessScreen(viewModel = viewModel, navController = navController)
    }
    else{
        ErrorScreen(viewModel = viewModel, navController = navController)
    }
}
@Composable
fun SuccessScreen(viewModel:HelpList,navController: NavController){
    val helpListState = viewModel.helpListState.collectAsState().value as HelpUiState.Success
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
    )
    { padding->
        LazyColumn(modifier = Modifier.padding(padding))
        {
            helpListState.helpList?.let { helpList ->
                items(helpList) { helpRequest ->
                    HelpItem(helpRequest, navController)
                }
            }
        }
    }
}
@Composable
fun ErrorScreen(viewModel:HelpList,navController: NavController){
    Scaffold (modifier = Modifier.fillMaxSize(),
        topBar ={
            Box(modifier = Modifier.fillMaxWidth()
                .background(color = Color(242, 231, 220)),
                contentAlignment= Alignment.TopEnd)
            {
                IconButton( onClick = { navController.navigate("SettingPage") }
                ) {
                    Icon(imageVector = Icons.Filled.Settings,
                        contentDescription = stringResource(id = R.string.setting_page),
                        tint = Color(2,115,115),
                        modifier = Modifier.size(30.dp))
                }
            }
        }
    )
    { padding->
        Box(modifier = Modifier
            .padding(padding)
            .fillMaxSize()
            .background(color = Color(242, 231, 220)),
            contentAlignment = Alignment.Center)
        {
            IconButton(onClick = { viewModel.getHelpList() }) {
                Icon(imageVector = Icons.Filled.Refresh,
                    contentDescription = "refresh",
                    tint = Color.Black,
                    modifier = Modifier.size(50.dp))
            }
        }
    }
}
@Composable
fun HelpItem(helpRequest: HelpRequest,navController: NavController) {
    var confirmScreenVisible by remember { mutableStateOf(false) }
    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = AbsoluteAlignment.Left,
        modifier = Modifier
            .fillMaxWidth()
            .border(width = 4.dp, color = Color(2, 115, 115))
            .background(Color(169, 217, 208))
            .padding(8.dp)
            .clickable { confirmScreenVisible = true }
    ) {
        Text(
            text = helpRequest.name,
            fontSize = 16.sp, color = Color.Black,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Box(modifier = Modifier
            .fillMaxWidth()
            .background(Color(242, 231, 220), shape = RoundedCornerShape(12.dp))
            .size(50.dp)
            .clip(RoundedCornerShape(12.dp)),
            ) {
            Column (
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = AbsoluteAlignment.Left,
                modifier = Modifier.padding(4.dp)
            )
            {
                Text(
                    text = stringResource(id = R.string.Description)+" :", fontSize = 8.sp,
                )
                Text(
                    text = helpRequest.description, fontSize = 12.sp
                )
            }

        }
    }
    if (confirmScreenVisible){
        ConfirmScreen(helpRequest = helpRequest, navController,onClose = {confirmScreenVisible=false})
    }
}
@Composable
fun ConfirmScreen(
    helpRequest: HelpRequest,
    navController: NavController,
    onClose: () -> Unit
) {
    Dialog(onDismissRequest = {onClose() }) {
        Column(modifier = Modifier
            .width(320.dp)
            .height(400.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(Color(242, 231, 220))
            .border(width = 8.dp, color = Color(2, 115, 115), shape = RoundedCornerShape(4.dp)),
            horizontalAlignment=Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        )
        {
            val id=helpRequest.id
            val name=helpRequest.name
            val description=helpRequest.description
            val address=helpRequest.address
            Row {
                Text(text = ":")
                Text(text = name)
            }
            Text(text = ":")
            Text(text = description)
            Row {
                Text(text =":")
                Text(text = address)
            }
            Button(onClick = {
                onClose()
                navController.navigate("HelpPage/${id}/${name}/${description}/${address}") },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(Color.Red),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp))
            {
                Text(text = stringResource(id = R.string.confirm), color = Color.White)
            }
        }
    }
}
@Preview(showBackground = true)
@Composable
fun HelpItemPreview(){
    val helpRequest=HelpRequest("1","Lily","find my pen"," MeetingRoom no.3")
    val navController= rememberNavController()
    HelpItem(helpRequest=helpRequest,navController)
}