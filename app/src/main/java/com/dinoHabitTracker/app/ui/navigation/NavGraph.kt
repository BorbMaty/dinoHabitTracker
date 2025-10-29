package com.dinoHabitTracker.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.dinoHabitTracker.app.ui.screens.auth.LoginScreen
import com.dinoHabitTracker.app.ui.screens.home.HomeScreen
import com.dinoHabitTracker.app.ui.screens.splash.SplashScreen

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") { SplashScreen(navController) }
        composable("login")  { LoginScreen(navController) }
        composable("home")   { HomeScreen() }
    }
}
