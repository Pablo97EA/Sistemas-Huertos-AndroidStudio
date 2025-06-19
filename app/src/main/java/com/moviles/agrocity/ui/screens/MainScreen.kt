package com.moviles.agrocity.ui.screens

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.moviles.agrocity.viewmodel.GardenViewModel
import com.moviles.agrocity.viewmodel.PestViewModel
import com.moviles.agrocity.ui.screens.GardenScreen

@Composable
fun MainScreen(navController: NavHostController = rememberNavController()) {
    val gardenViewModel: GardenViewModel = viewModel()

    NavHost(navController = navController, startDestination = "home") {

        // Home
        composable("home") {
            HomeScreen(
                onGoToPests = { navController.navigate("pests") },
                onGoToGardens = { navController.navigate("gardens") }
            )
        }

        // Pest
        composable("pests") {
            PestScreen()
        }

        // Garden
        composable("gardens") {
            GardenScreen(viewModel = gardenViewModel, userId = 1)
        }
    }
}
