package com.dinoHabitTracker.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.navigation.compose.rememberNavController
import com.dinoHabitTracker.app.ui.navigation.AppNavGraph
import com.dinoHabitTracker.app.ui.theme.DinoTheme
import com.dinoHabitTracker.app.ui.RootScaffold

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DinoTheme { // ← a te Theme composable-od neve
                val navController = rememberNavController()

                RootScaffold(
                    navController = navController,
                    NavGraph = { innerModifier ->
                        AppNavGraph(
                            navController = navController,
                            modifier = innerModifier // FONTOS: a Scaffold paddingje
                        )
                    }
                )
            }
        }
    }
}