package com.dinoHabitTracker.app.ui.screens.home

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dinoHabitTracker.app.data.remote.ScheduleResponse
import com.dinoHabitTracker.app.viewmodel.HomeViewModel
import java.time.Duration
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.rememberDatePickerState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    appContext: Context,
    onAdd: () -> Unit = {},
    onOpenDetails: (Long) -> Unit
) {
    val vm: HomeViewModel = viewModel(factory = HomeViewModel.Factory(appContext))

    val items = vm.items.observeAsState().value ?: emptyList()
    val loading = vm.loading.observeAsState().value ?: false
    val error = vm.error.observeAsState().value

    // aktuális nap (UTC)
    var selectedDate by rememberSaveable { mutableStateOf(LocalDate.now(ZoneOffset.UTC)) }
    val today = remember { LocalDate.now(ZoneOffset.UTC) }

    // DatePicker dialog láthatósága
    var showDatePicker by remember { mutableStateOf(false) }

    // ha változik a kiválasztott nap, automatikusan töltjük
    LaunchedEffect(selectedDate) {
        vm.loadByDate(selectedDate.toString())
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Bal oldal: nap váltás + "Today"
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = { selectedDate = selectedDate.minusDays(1) }) {
                                Icon(
                                    imageVector = Icons.Filled.ChevronLeft,
                                    contentDescription = "Previous day"
                                )
                            }

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clickable { showDatePicker = true }
                            ) {
                                Text(
                                    text = if (selectedDate == today) "Today" else selectedDate.dayOfWeek.name.lowercase()
                                        .replaceFirstChar { it.uppercase() },
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                                Text(
                                    text = selectedDate.toString(),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            IconButton(onClick = { selectedDate = selectedDate.plusDays(1) }) {
                                Icon(
                                    imageVector = Icons.Filled.ChevronRight,
                                    contentDescription = "Next day"
                                )
                            }
                        }

                        // Jobb oldal: Add Schedule – mint a mockupban
                        TextButton(onClick = onAdd) {
                            Text(
                                text = "Add Schedule",
                                style = MaterialTheme.typography.labelLarge,
                                color = Color(0xFF3DDC84)
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            // visszaugrás ma-ra
            FloatingActionButton(
                onClick = { selectedDate = today },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            ) {
                Text("Today")
            }
        }
    ) { innerPadding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))

                error != null -> Text(
                    text = error ?: "Error",
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error
                )

                items.isEmpty() -> Text(
                    text = "No schedules for this day 🦕",
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )

                else -> ScheduleList(
                    items = items,
                    onOpenDetails = onOpenDetails
                )
            }
        }
    }

    // DatePickerDialog – teljes hónap nézettel
    if (showDatePicker) {
        val initialMillis = remember(selectedDate) {
            selectedDate
                .atStartOfDay(ZoneOffset.UTC)
                .toInstant()
                .toEpochMilli()
        }

        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = initialMillis
        )

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val millis = datePickerState.selectedDateMillis
                        if (millis != null) {
                            val pickedDate = java.time.Instant.ofEpochMilli(millis)
                                .atZone(ZoneOffset.UTC)
                                .toLocalDate()
                            selectedDate = pickedDate
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
private fun ScheduleList(
    items: List<ScheduleResponse>,
    onOpenDetails: (Long) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            items = items,
            key = { it.id }
        ) { schedule ->
            ScheduleCard(
                schedule = schedule,
                onClick = { onOpenDetails(schedule.id.toLong()) }
            )
        }
    }
}

@Composable
private fun ScheduleCard(
    schedule: ScheduleResponse,
    onClick: () -> Unit
) {
    val fmt = remember { DateTimeFormatter.ofPattern("HH:mm") }

    val start = schedule.startTime
    val end = schedule.endTime

    val timeText = when {
        start != null && end != null ->
            "${start.format(fmt)} – ${end.format(fmt)}"
        start != null -> start.format(fmt)
        else -> "Time not set"
    }

    val durationText = if (start != null && end != null) {
        val minutes = Duration.between(start, end).toMinutes()
        if (minutes > 0) "${minutes} min" else null
    } else null

    val status = schedule.status ?: "Planned"
    val statusColor = when (status) {
        "Completed" -> Color(0xFF4CAF50)
        "Skipped" -> Color(0xFFF44336)
        else -> Color(0xFFFFC107)
    }

    val typeLabel = when (schedule.isCustom) {
        true -> "Custom"
        false -> "Recurring"
        else -> null
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.95f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            // felső sor: idő, duration, status chip
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Schedule,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = Color(0xFF3DDC84)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = timeText,
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    if (durationText != null) {
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = durationText,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (typeLabel != null) {
                        Text(
                            text = typeLabel,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = statusColor.copy(alpha = 0.18f),
                        border = BorderStroke(1.dp, statusColor)
                    ) {
                        Text(
                            text = status,
                            style = MaterialTheme.typography.labelSmall,
                            color = statusColor,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(6.dp))

            // habit név – mockup szerint erős cím
            Text(
                text = schedule.habit?.name ?: "Unknown habit",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )

            if (!schedule.notes.isNullOrBlank()) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = schedule.notes!!,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
        }
    }
}
