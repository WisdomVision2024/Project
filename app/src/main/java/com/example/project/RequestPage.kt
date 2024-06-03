package com.example.project

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController

@Composable
fun RequestPage(navController: NavHostController) {
    Scaffold(bottomBar ={Navigationbar(
        current=0,
        navController=navController,)
    }
    )
    {
        padding->
        LazyColumn (modifier = Modifier.padding(padding)){
            item {  }
        }
    }
}