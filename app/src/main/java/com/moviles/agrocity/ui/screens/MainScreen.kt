package com.moviles.agrocity.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.moviles.agrocity.ui.components.TabSelector
import com.moviles.agrocity.viewmodels.CommentViewModel
import com.moviles.agrocity.viewmodel.GardenViewModel
import com.moviles.agrocity.viewmodel.PestViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainScreen(navController: NavHostController = rememberNavController()) {
    val tabTitles = listOf("Inicio", "Plagas", "Jardines", "Gemini", "Calendario")
    val tabIcons = listOf(
        Icons.Filled.Home,
        Icons.Filled.Warning,
        Icons.Filled.Star,
        Icons.Filled.Search,
        Icons.Filled.Email // Cambia según tu preferencia
    )
    val tabRoutes = listOf("home", "pests", "gardens", "gemini", "calendar")

    val navBackStackEntry by navController.currentBackStackEntryFlow
        .collectAsState(initial = navController.currentBackStackEntry)

    val currentRoute = navBackStackEntry?.destination?.route
    val selectedTabIndex = tabRoutes.indexOfFirst { currentRoute?.startsWith(it) == true }.coerceAtLeast(0)

    Column(modifier = Modifier.fillMaxSize()) {
        TabSelector(
            selectedIndex = selectedTabIndex,
            onTabSelected = { index -> navController.navigate(tabRoutes[index]) },
            tabTitles = tabTitles,
            tabIcons = tabIcons
        )

        Spacer(modifier = Modifier.height(8.dp))

        NavHost(navController = navController, startDestination = "home") {

            composable("home") {
                HomeScreen(
                    onGoToPests = { navController.navigate("pests") },
                    onGoToGardens = { navController.navigate("gardens") },
                    onGoToGemini = { navController.navigate("gemini") },
                    onGoToComment = { gardenId -> navController.navigate("comment/$gardenId") }
                )
            }

            composable("pests") {
                PestScreen()
            }

            composable("gardens") {
                val gardenViewModel: GardenViewModel = viewModel()
                GardenScreen(viewModel = gardenViewModel)
            }

            composable("gemini") {
                GeminiScreen()
            }

            composable("calendar") {
                CalendarScreen()
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
}
