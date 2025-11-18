package com.dinoHabitTracker.app.ui.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

import com.dinoHabitTracker.app.ui.screens.auth.LoginScreen
import com.dinoHabitTracker.app.ui.screens.habit.AddHabitScreen
import com.dinoHabitTracker.app.ui.screens.home.HomeScreen
import com.dinoHabitTracker.app.ui.screens.profile.ProfileScreen
import com.dinoHabitTracker.app.ui.screens.profile.EditProfileScreen
import com.dinoHabitTracker.app.ui.screens.schedule.AddScheduleScreen
import com.dinoHabitTracker.app.ui.screens.schedule.ScheduleDetailsScreen
import com.dinoHabitTracker.app.ui.screens.schedule.EditScheduleRoute
import com.dinoHabitTracker.app.ui.screens.splash.SplashScreen

import com.dinoHabitTracker.app.viewmodel.ProfileViewModel
import com.dinoHabitTracker.app.viewmodel.ScheduleDetailsViewModel

@Composable
fun AppNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val appContext = LocalContext.current

    // 🔹 KÖZÖS ProfileViewModel az egész grafhoz
    val profileVm: ProfileViewModel = viewModel(
        factory = ProfileViewModel.Factory(context = appContext)
    )

    NavHost(
        navController = navController,
        startDestination = "splash",
        modifier = modifier
    ) {

        // ---------- SPLASH ----------
        composable(route = "splash") {
            SplashScreen(nav = navController)
        }

        // ---------- LOGIN ----------
        composable(route = "login") {
            LoginScreen(nav = navController)
        }

        // ---------- HOME ----------
        composable(route = "home") {
            HomeScreen(
                appContext = appContext,
                onAdd = { navController.navigate("add_schedule") },
                onOpenDetails = { id ->
                    navController.navigate("schedule_details/$id")
                }
            )
        }

        // ---------- ADD HABIT ----------
        composable(route = "add_habit") {
            AddHabitScreen(
                onCreated = { navController.popBackStack() }
            )
        }

        // ---------- ADD SCHEDULE ----------
        composable(route = "add_schedule") {
            AddScheduleScreen(
                onCreated = { navController.popBackStack() },
                onAddHabit = { navController.navigate("add_habit") }
            )
        }

        // ---------- SCHEDULE DETAILS ----------
        composable(
            route = "schedule_details/{scheduleId}",
            arguments = listOf(
                navArgument("scheduleId") { type = NavType.LongType }
            )
        ) { backStackEntry ->

            val ctx = LocalContext.current
            val scheduleId = backStackEntry.arguments?.getLong("scheduleId") ?: 0L

            val vm: ScheduleDetailsViewModel = viewModel(
                factory = ScheduleDetailsViewModel.Factory(
                    context = ctx,
                    scheduleId = scheduleId
                )
            )

            val uiState by vm.uiState.collectAsState()
            val isDeleted by vm.deleted.collectAsState()

            if (isDeleted) {
                LaunchedEffect(isDeleted) {
                    navController.popBackStack()
                }
            }

            ScheduleDetailsScreen(
                state = uiState,
                onBack = { navController.popBackStack() },
                onEdit = { navController.navigate("edit_schedule/$scheduleId") },
                onDelete = { vm.delete() },
                onAddProgress = { minutes, notes, isCompleted ->
                    vm.addProgress(
                        loggedMinutes = minutes,
                        notes = notes,
                        isCompleted = isCompleted
                    )
                }
            )
        }

        // ---------- EDIT SCHEDULE ----------
        composable(
            route = "edit_schedule/{scheduleId}",
            arguments = listOf(
                navArgument("scheduleId") { type = NavType.LongType }
            )
        ) { backStackEntry ->

            val ctx = LocalContext.current
            val scheduleId = backStackEntry.arguments?.getLong("scheduleId") ?: 0L

            EditScheduleRoute(
                scheduleId = scheduleId,
                appContext = ctx,
                onBack = { navController.popBackStack() }
            )
        }

        // ---------- PROFILE ----------
        composable(route = "profile") {

            val ui by profileVm.uiState.collectAsState()
            val loading by profileVm.loading.collectAsState()
            val error by profileVm.error.collectAsState()

            when {
                loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                error != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceAround
                    ) {

                        Text(
                            text = "Failed to load profile",
                            color = MaterialTheme.colorScheme.error
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(onClick = { profileVm.loadProfile() }) {
                            Text("Retry")
                        }
                    }
                }

                else -> {
                    ProfileScreen(
                        ui = ui,
                        onEdit = { navController.navigate("edit_profile") },
                        onLogout = {
                            navController.navigate("login") {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    )
                }
            }
        }

        // ---------- EDIT PROFILE ----------
        composable(route = "edit_profile") {

            val ui by profileVm.uiState.collectAsState()

            EditProfileScreen(
                ui = ui,
                onBack = { navController.popBackStack() },
                onSave = { username, description ->
                    profileVm.updateLocalProfile(username, description)
                    navController.popBackStack()
                }
            )
        }
    }
}
