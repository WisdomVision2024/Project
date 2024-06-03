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
import androidx.navigation.NavHostController

@Composable
fun HelpListPage(navController: NavHostController) {
    Scaffold (modifier = Modifier.fillMaxSize(),
        bottomBar = { Navigationbar2(0, navController)
        }
    )
    { padding->
        Box(
            modifier = Modifier
                .background(Color(8, 79, 209))
                .fillMaxWidth()
                .padding(padding)
                .padding(15.dp),
            contentAlignment = Alignment.Center,
        )
        {
        }
    }
}
