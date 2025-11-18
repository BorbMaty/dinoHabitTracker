package com.dinoHabitTracker.app.ui.screens.schedule

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dinoHabitTracker.app.viewmodel.EditScheduleViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditScheduleRoute(
    scheduleId: Long,
    appContext: android.content.Context,
    onBack: () -> Unit
) {
    val vm: EditScheduleViewModel =
        viewModel(factory = EditScheduleViewModel.Factory(appContext, scheduleId))

    val schedule = vm.schedule.observeAsState().value
    val loading = vm.loading.observeAsState(false).value
    val saving = vm.saving.observeAsState(false).value
    val error = vm.error.observeAsState().value
    val saved = vm.saved.observeAsState(false).value

    // mentés után vissza
    LaunchedEffect(saved) {
        if (saved) {
            onBack()
        }
    }

    EditScheduleScreen(
        loading = loading,
        saving = saving,
        error = error,
        schedule = schedule,
        onBack = onBack,
        onSave = { startHHmm, endHHmm, duration, status, isCustom, notes ->
            vm.updateSchedule(
                startHHmm = startHHmm,
                endHHmm = endHHmm,
                durationMinutes = duration,
                status = status,
                isCustom = isCustom,
                notes = notes
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditScheduleScreen(
    loading: Boolean,
    saving: Boolean,
    error: String?,
    schedule: com.dinoHabitTracker.app.data.dto.ScheduleResponseDto?,
    onBack: () -> Unit,
    onSave: (
        startHHmm: String?,
        endHHmm: String?,
        durationMinutes: Int?,
        status: String,
        isCustom: Boolean,
        notes: String?
    ) -> Unit
) {
    if (loading && schedule == null) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Edit schedule") },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            }
        ) { padding ->
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        return
    }

    if (schedule == null) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Edit schedule") },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            }
        ) { padding ->
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Failed to load schedule.", color = MaterialTheme.colorScheme.error)
            }
        }
        return
    }

    // ---- Inicializált lokális state a schedule alapján ----
    var startHHmm by rememberSaveable(schedule.id) {
        mutableStateOf(isoToHHmm(schedule.start_time))
    }
    var endHHmm by rememberSaveable(schedule.id) {
        mutableStateOf(isoToHHmm(schedule.end_time))
    }
    var durationText by rememberSaveable(schedule.id) {
        mutableStateOf(schedule.duration_minutes?.toString() ?: "")
    }
    var status by rememberSaveable(schedule.id) {
        mutableStateOf(schedule.status ?: "Planned")
    }
    var isCustom by rememberSaveable(schedule.id) {
        mutableStateOf(schedule.isCustom ?: false)
    }
    var notes by rememberSaveable(schedule.id) {
        mutableStateOf(schedule.notes ?: "")
    }

    val statusOptions = listOf("Planned", "Completed", "Skipped")
    var statusMenuExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit schedule") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            val duration = durationText.toIntOrNull()
                            onSave(
                                startHHmm.ifBlank { null },
                                endHHmm.ifBlank { null },
                                duration,
                                status,
                                isCustom,
                                notes.ifBlank { null }
                            )
                        },
                        enabled = !saving
                    ) {
                        Text("Save")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Text(
                text = schedule.habit.name,
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                text = "Edit time, status and notes for this schedule.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Date (read-only kijelzés)
            val dateText = schedule.date?.let { iso ->
                try {
                    Instant.parse(iso)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                } catch (_: Exception) {
                    iso
                }
            } ?: "N/A"

            OutlinedTextField(
                value = dateText,
                onValueChange = { },
                label = { Text("Date") },
                readOnly = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Start time
            OutlinedTextField(
                value = startHHmm,
                onValueChange = {
                    startHHmm = it.filter { ch -> ch.isDigit() || ch == ':' }.take(5)
                },
                label = { Text("Start (HH:mm)") },
                leadingIcon = {
                    Icon(Icons.Default.Schedule, contentDescription = null)
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // End time
            OutlinedTextField(
                value = endHHmm,
                onValueChange = {
                    endHHmm = it.filter { ch -> ch.isDigit() || ch == ':' }.take(5)
                },
                label = { Text("End (HH:mm)") },
                leadingIcon = {
                    Icon(Icons.Default.Schedule, contentDescription = null)
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Duration
            OutlinedTextField(
                value = durationText,
                onValueChange = {
                    durationText = it.filter { ch -> ch.isDigit() }
                },
                label = { Text("Duration (minutes)") },
                leadingIcon = {
                    Icon(Icons.Default.Timer, contentDescription = null)
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Status dropdown
            Text("Status", style = MaterialTheme.typography.labelLarge)

            Box {
                OutlinedButton(
                    onClick = { statusMenuExpanded = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(status)
                }
                DropdownMenu(
                    expanded = statusMenuExpanded,
                    onDismissRequest = { statusMenuExpanded = false }
                ) {
                    statusOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                status = option
                                statusMenuExpanded = false
                            }
                        )
                    }
                }
            }

            // Custom flag
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Switch(
                    checked = isCustom,
                    onCheckedChange = { isCustom = it }
                )
                Text("Custom schedule")
            }

            // Notes
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes") },
                modifier = Modifier.fillMaxWidth()
            )

            if (saving) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                )
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

private fun isoToHHmm(iso: String?): String {
    if (iso == null) return ""
    return try {
        Instant.parse(iso)
            .atZone(ZoneId.systemDefault())
            .toLocalTime()
            .format(DateTimeFormatter.ofPattern("HH:mm"))
    } catch (_: Exception) {
        ""
    }
}
