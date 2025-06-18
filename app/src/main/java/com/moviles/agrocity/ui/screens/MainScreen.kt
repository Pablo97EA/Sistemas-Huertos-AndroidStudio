package com.moviles.agrocity.ui.screens

import androidx.compose.runtime.Composable
import androidx.navigation.compose.*
import androidx.navigation.NavType
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.moviles.agrocity.viewmodel.PestViewModel

@Composable
fun MainScreen(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = "home") {

        composable("home") {
            HomeScreen(
                onGoToPests = { navController.navigate("pests") }
            )
        }

        composable("pests") {
            PestScreen()
        }


    }
}
