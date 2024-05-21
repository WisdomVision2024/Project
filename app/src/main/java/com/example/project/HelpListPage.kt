package com.example.project

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun HelpListPage(navController: NavController) {
    val current = 0
    val isvisualimpired=true
    Scaffold (modifier = Modifier.fillMaxSize(),
        bottomBar = { if (isvisualimpired)Navigationbar(current,navController)
        else Navigationbar2(current, navController)
        })
    { innerPadding ->
        println(innerPadding)
        Box(
            modifier = Modifier
                .background(Color(8, 79, 209))
                .fillMaxWidth()
                .padding(15.dp),
            contentAlignment = Alignment.Center,
        )
        {
        }
    }
}
