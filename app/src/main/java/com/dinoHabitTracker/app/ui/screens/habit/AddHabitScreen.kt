package com.dinoHabitTracker.app.ui.screens.habit

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.platform.LocalContext
import com.dinoHabitTracker.app.viewmodel.HabitViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddHabitScreen(
    onCreated: (Long) -> Unit = {}
) {
    val ctx = LocalContext.current
    val vm: HabitViewModel = viewModel(factory = HabitViewModel.Factory(ctx))

    // --- VM state ---
    val categories = vm.categories.observeAsState(emptyList()).value
    val loading = vm.loading.observeAsState(false).value
    val error = vm.error.observeAsState(null).value
    val createdId = vm.createSuccessId.observeAsState(null).value

    // --- Local form state ---
    var name by remember { mutableStateOf("") }
    var goal by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var catExpanded by remember { mutableStateOf(false) }
    var selectedCatIndex by remember { mutableStateOf<Int?>(null) }

    // belépéskor kategóriák
    LaunchedEffect(Unit) { vm.loadCategories() }

    // siker callback
    LaunchedEffect(createdId) {
        if (createdId != null) {
            onCreated(createdId)
            vm.clearCreateResult()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Add Habit") })
        }
    ) { padding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            if (loading) {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Kategória dropdown
                ExposedDropdownMenuBox(
                    expanded = catExpanded,
                    onExpandedChange = { catExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedCatIndex?.let { categories.getOrNull(it)?.name } ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Category") },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = catExpanded,
                        onDismissRequest = { catExpanded = false }
                    ) {
                        categories.forEachIndexed { index, c ->
                            DropdownMenuItem(
                                text = { Text(c.name) },
                                onClick = {
                                    selectedCatIndex = index
                                    catExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = goal,
                    onValueChange = { goal = it },
                    label = { Text("Goal (e.g., \"Run 10 times in 2 weeks\")") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (optional)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))

                Button(
                    onClick = {
                        val catId = selectedCatIndex?.let { categories[it].id }
                        if (!name.isBlank() && !goal.isBlank() && catId != null) {
                            vm.createHabit(
                                name = name.trim(),
                                categoryId = catId,
                                goal = goal.trim(),
                                description = description.trim().ifBlank { null }
                            )
                        }
                    },
                    enabled = !loading && !name.isBlank() && !goal.isBlank() && selectedCatIndex != null,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Create Habit")
                }

                if (error != null) {
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}
