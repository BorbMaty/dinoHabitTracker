package com.dinoHabitTracker.app.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.dinoHabitTracker.app.viewmodel.LoginViewModel

@Composable
fun LoginScreen(nav: NavController, vm: LoginViewModel = viewModel()) {
    val state by vm.state.collectAsState()

    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            Modifier.padding(24.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Üdv a dínóbirodalomban!", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = state.email,
                onValueChange = vm::onEmailChange,
                label = { Text("Email") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = state.password,
                onValueChange = vm::onPasswordChange,
                label = { Text("Jelszó") },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))
            Button(
                onClick = {
                    vm.signIn {
                        nav.navigate("home") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                },
                enabled = !state.loading,
                modifier = Modifier.fillMaxWidth()
            ) { Text(if (state.loading) "Bejelentkezés..." else "Bejelentkezés") }

            state.error?.let {
                Spacer(Modifier.height(8.dp))
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            TextButton(onClick = { /* TODO: Reset Password */ }) { Text("Elfelejtett jelszó?") }
            TextButton(onClick = { /* TODO: Google Login */ }) { Text("Bejelentkezés Google-lel") }
        }
    }
}
