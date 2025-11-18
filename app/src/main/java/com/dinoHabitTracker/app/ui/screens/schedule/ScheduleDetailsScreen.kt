package com.dinoHabitTracker.app.ui.screens.schedule

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.dinoHabitTracker.app.data.dto.ProgressResponseDto
import com.dinoHabitTracker.app.data.dto.ScheduleResponseDto
import java.time.Instant
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleDetailsScreen(
    state: ScheduleDetailsUiState,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onAddProgress: (loggedMinutes: Int?, notes: String?, isCompleted: Boolean) -> Unit
) {
    val schedule = state.schedule
    val loading = state.loading
    val error = state.error

    var showDeleteDialog by remember { mutableStateOf(false) }
    var isDone by remember { mutableStateOf(false) }
    var progressNotes by remember { mutableStateOf("") }
    var minutesText by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🦕 Schedule details")
                    }
                },
                actions = {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit schedule")
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete schedule",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )
        }
    ) { padding ->
        if (loading) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        if (schedule == null) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Failed to load schedule.", color = MaterialTheme.colorScheme.error)
            }
            return@Scaffold
        }

        val progressList = schedule.progress ?: emptyList()
        val completedCount = progressList.count { it.isCompleted == true }
        val totalCount = progressList.size
        val ratio = if (totalCount > 0) completedCount.toFloat() / totalCount.toFloat() else 0f

        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // HEADER: habit + status
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = schedule.habit.name,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.ExtraBold
                    )
                )

                Spacer(Modifier.width(12.dp))

                statusChip(schedule.status)
            }

            if (!schedule.notes.isNullOrBlank()) {
                Text(
                    text = schedule.notes!!,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.9f)
                )
            }

            // INFO CARD
            ScheduleInfoCard(schedule)

            // PROGRESS OVERVIEW – vidámabb, dínós
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    "Progress overview 🌈",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.primary
                )

                LinearProgressIndicator(
                    progress = ratio,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp),
                    color = Color(0xFF3DDC84),
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
                Text(
                    "$completedCount / $totalCount completed entries",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                )
            }

            // PROGRESS HISTORY LIST
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    "Progress history 📜",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.primary
                )

                if (progressList.isEmpty()) {
                    Text(
                        "No progress entries yet. Add one below 🦕",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF3DDC84)
                    )
                } else {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        progressList.forEach { p ->
                            ProgressHistoryItem(p)
                        }
                    }
                }
            }

            // TODAY / NEW PROGRESS CARD – barátságosabb színek
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                ),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    Text(
                        "Add progress for your dino 🐾",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                    )

                    OutlinedTextField(
                        value = minutesText,
                        onValueChange = { minutesText = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Minutes (optional)") },
                        singleLine = true
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Switch(checked = isDone, onCheckedChange = { isDone = it })
                        Text(
                            if (isDone) "Mark as completed ✅" else "Not completed yet",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    OutlinedTextField(
                        value = progressNotes,
                        onValueChange = { progressNotes = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Notes for future you (optional)") }
                    )

                    Button(
                        onClick = {
                            val minutes = minutesText.toIntOrNull()
                            val notes = progressNotes.ifBlank { null }
                            onAddProgress(minutes, notes, isDone)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Save progress 🦕")
                    }
                }
            }

            if (error != null) {
                Text(
                    error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }

    // Delete dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            },
            title = { Text("Delete schedule?") },
            text = {
                Text("This action cannot be undone. Are you sure you want to delete this schedule?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        onDelete()
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun ScheduleInfoCard(schedule: ScheduleResponseDto) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f)
        ),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val dateText = schedule.date?.take(10) ?: "N/A"

            val startIso = schedule.start_time
            val endIso = schedule.end_time

            fun isoToHHmm(iso: String?): String {
                if (iso == null || iso.length < 16) return "--:--"
                return iso.substring(11, 16)
            }

            val startText = isoToHHmm(startIso)
            val endText = isoToHHmm(endIso)

            val durationMinutes: Int? =
                if (startIso != null && endIso != null
                    && startIso.length >= 16 && endIso.length >= 16
                ) {
                    runCatching {
                        val s = java.time.LocalTime.parse(startIso.substring(11, 16))
                        val e = java.time.LocalTime.parse(endIso.substring(11, 16))
                        java.time.Duration.between(s, e).toMinutes().toInt()
                    }.getOrNull()
                } else {
                    schedule.duration_minutes
                }

            val durationText = durationMinutes?.let { "$it min" } ?: "N/A"

            InfoRow(Icons.Default.CalendarToday, "Date", dateText)
            InfoRow(Icons.Default.Schedule, "Time", "$startText – $endText")
            InfoRow(Icons.Default.Timer, "Duration", durationText)
        }
    }
}

@Composable
private fun ProgressHistoryItem(progress: ProgressResponseDto) {
    val color = if (progress.isCompleted == true) {
        Color(0xFF4CAF50)
    } else {
        Color(0xFFFFC107)
    }

    val displayDate = remember(progress.date) { formatProgressDate(progress.date) }

    Card(
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, color.copy(alpha = 0.5f)),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = displayDate,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = if (progress.isCompleted == true) "Completed ✅" else "Not completed",
                    style = MaterialTheme.typography.labelSmall,
                    color = color
                )
            }

            if (progress.loggedTime != null) {
                Text(
                    text = "Time: ${progress.loggedTime} min",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            if (!progress.notes.isNullOrBlank()) {
                Text(
                    text = progress.notes,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun InfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            icon,
            contentDescription = null,
            tint = Color(0xFF3DDC84)
        )
        Spacer(Modifier.width(8.dp))
        Column {
            Text(
                label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            Text(
                value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun statusChip(status: String) {
    val (color, label) = when (status) {
        "Completed" -> Color(0xFF4CAF50) to "Completed"
        "Skipped" -> Color(0xFFF44336) to "Skipped"
        else -> Color(0xFFFFC107) to "Planned"
    }

    Surface(
        shape = RoundedCornerShape(50),
        color = color.copy(alpha = 0.16f),
        border = BorderStroke(1.dp, color)
    ) {
        Text(
            text = label,
            color = color,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

// -------- Helper: backend date -> szép dátum --------

private fun formatProgressDate(raw: String): String {
    return runCatching {
        if (raw.contains("T")) {
            val instant = Instant.parse(raw)
            val localDate = instant.atZone(ZoneId.systemDefault()).toLocalDate()
            localDate.toString()
        } else {
            raw
        }
    }.getOrElse {
        if (raw.length >= 10) raw.substring(0, 10) else raw
    }
}
