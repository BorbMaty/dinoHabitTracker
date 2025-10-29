package com.dinoHabitTracker.app.ui.screens.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.dinoHabitTracker.app.data.auth.TokenStore
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull

@Composable
fun SplashScreen(nav: NavController) {
    val scale = remember { Animatable(0.85f) }
    val alpha = remember { Animatable(0f) }
    val context = androidx.compose.ui.platform.LocalContext.current
    val tokenStore = remember { TokenStore(context) }

    LaunchedEffect(Unit) {
        // kis belépő anim
        scale.animateTo(1f, tween(700, easing = overshoot))
        alpha.animateTo(1f, tween(500))

        // token kiolvasás
        val token = tokenStore.accessToken.firstOrNull() ?: ""

        // ha üres / "null" → login, különben home
        val goTo = if (token.isBlank() || token == "null") "login" else "home"

        delay(400)
        nav.navigate(goTo) {
            popUpTo("splash") { inclusive = true }
        }
    }

    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "Progr3SS",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.ExtraBold, letterSpacing = 0.5.sp
                ),
                modifier = Modifier
                    .graphicsLayer(scaleX = scale.value, scaleY = scale.value, alpha = alpha.value)
                    .padding(bottom = 8.dp)
            )
            Text("Habit Planner & Tracker 🦕", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

private val overshoot = Easing { t ->
    val tension = 2f
    val f = t - 1f
    f * f * ((tension + 1) * f + tension) + 1f
}
