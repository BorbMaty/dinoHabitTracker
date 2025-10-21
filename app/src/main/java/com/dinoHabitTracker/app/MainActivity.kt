package com.dinoHabitTracker.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.navigation.compose.rememberNavController
import com.dinoHabitTracker.app.ui.navigation.AppNavGraph
import com.dinoHabitTracker.app.ui.theme.DinoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DinoTheme {                       // ← a saját dínó témád
                Surface {                      // Material3 Surface (a téma színeivel)
                    val navController = rememberNavController()
                    AppNavGraph(navController) // Login ↔ Home
                }
            }
        }
    }
}
