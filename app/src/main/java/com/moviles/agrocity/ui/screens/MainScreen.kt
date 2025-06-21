package com.moviles.agrocity.ui.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.moviles.agrocity.models.Garden
import com.moviles.agrocity.viewmodel.GardenViewModel
import com.moviles.agrocity.viewmodel.PestViewModel
import com.moviles.agrocity.ui.screens.GardenScreen
import com.moviles.agrocity.viewmodels.CommentViewModel

@Composable
fun MainScreen(navController: NavHostController = rememberNavController()) {
    val gardenViewModel: GardenViewModel = viewModel()

    NavHost(navController = navController, startDestination = "home") {


        // Home
        composable("home") {
            HomeScreen(
                onGoToPests = { navController.navigate("pests") },
                onGoToPlants = { navController.navigate("plants") },
                onGoToGardens = { navController.navigate("gardens") },
                onGoToGemini = { navController.navigate("gemini") },
                onGoToComment = { gardenId -> navController.navigate("comment/$gardenId") }
            )
        }

        // Pest
        composable("pests") {
            PestScreen()
        }

        // Plants
        composable("plants") {
            PlantScreen()
        }

        // Garden
        composable("gardens") {
            GardenScreen(viewModel = gardenViewModel)
        }

        composable("gemini") {
            GeminiScreen()
        }

        composable(
            "comment/{gardenId}",
            arguments = listOf(navArgument("gardenId") { type = NavType.IntType })
        ) { backStackEntry ->
            val gardenId = backStackEntry.arguments?.getInt("gardenId") ?: return@composable
            val commentViewModel: CommentViewModel = viewModel()
            val gardenViewModel: GardenViewModel = viewModel()

            CommentScreen(
                gardenId = gardenId,
                commentViewModel = commentViewModel,
                gardenViewModel = gardenViewModel
            )
        }




    }
}
