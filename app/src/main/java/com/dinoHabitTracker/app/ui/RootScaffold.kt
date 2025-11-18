package com.dinoHabitTracker.app.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.dinoHabitTracker.app.ui.components.BottomNavBar

@Composable
fun RootScaffold(
    navController: NavHostController,
    NavGraph: @Composable (Modifier) -> Unit
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    // Itt sorold, ahol NE legyen alsó sáv
    val noBottomBarRoutes = setOf("splash", "login")
    val showBottomBar = currentRoute !in noBottomBarRoutes

    Scaffold(
        bottomBar = { if (showBottomBar) BottomNavBar(navController) }
    ) { innerPadding ->
        NavGraph(Modifier.padding(innerPadding))
    }
}
