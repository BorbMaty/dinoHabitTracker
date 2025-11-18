package com.dinoHabitTracker.app.ui.screens.schedule

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.unit.width
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dinoHabitTracker.app.viewmodel.AddScheduleViewModel
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@Composable
fun AddScheduleScreen(
    onCreated: () -> Unit = {},
    onAddHabit: () -> Unit = {}
) {
    val ctx = LocalContext.current
    val vm: AddScheduleViewModel = viewModel(factory = AddScheduleViewModel.Factory(ctx))

    // VM state
    val loading = vm.loading.observeAsState(false).value
    val error = vm.error.observeAsState(null).value
    val created = vm.created.observeAsState(null).value
    val habits = vm.habits.observeAsState(emptyList()).value

    // Form state
    var selectedHabitIndex by remember { mutableStateOf<Int?>(null) }
    var dateUtc by remember { mutableStateOf(LocalDate.now(ZoneOffset.UTC).toString()) } // YYYY-MM-DD
    var startHHmm by remember { mutableStateOf("18:00") }   // HH:mm
    var durationText by remember { mutableStateOf("30") }
    var notes by remember { mutableStateOf("") }

    // ÚJ: ismétlés állapotok
    var repeatPattern by remember { mutableStateOf("none") }           // "none", "daily", "weekdays", "weekends"
    var selectedWeekDays by remember { mutableStateOf(setOf<Int>()) }  // 1 = hétfő ... 7 = vasárnap

    // Habits betöltése
    LaunchedEffect(Unit) { vm.loadHabits(ctx) }

    // Siker -> vissza
    LaunchedEffect(created) {
        if (created != null) {
            onCreated()
            vm.clearResult()
        }
    }

    Scaffold { padding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                // 🔹 FELÜL: görgethető tartalom
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    // ---- Fejléc ----
                    Text(
                        text = "Add Schedule",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Text(
                        text = "Plan a new habit session quickly.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Divider(Modifier.padding(top = 8.dp, bottom = 4.dp))

                    // ---- Habit választó ----
                    Text(
                        text = "Habit",
                        style = MaterialTheme.typography.labelLarge
                    )

                    var expanded by remember { mutableStateOf(false) }
                    var tfSize by remember { mutableStateOf(Size.Zero) }
                    val density = LocalDensity.current

                    Box {
                        OutlinedTextField(
                            value = selectedHabitIndex?.let { habits.getOrNull(it)?.name } ?: "",
                            onValueChange = { /* read-only */ },
                            readOnly = true,
                            label = { Text("Select habit") },
                            trailingIcon = { Text("▼") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .onGloballyPositioned { c -> tfSize = c.size.toSize() }
                        )
                        Box(
                            Modifier
                                .matchParentSize()
                                .clickable { expanded = true }
                        )
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.width(with(density) { tfSize.width.toDp() })
                        ) {
                            habits.forEachIndexed { idx, h ->
                                DropdownMenuItem(
                                    text = { Text(h.name) },
                                    onClick = {
                                        selectedHabitIndex = idx
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                    TextButton(onClick = onAddHabit) {
                        Text("＋ Add new habit")
                    }

                    // ---- Dátum / idő / duration ----
                    Text(
                        text = "When",
                        style = MaterialTheme.typography.labelLarge
                    )

                    OutlinedTextField(
                        value = dateUtc,
                        onValueChange = { dateUtc = it.trim() },
                        label = { Text("Date (UTC, YYYY-MM-DD)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = startHHmm,
                        onValueChange = {
                            startHHmm = it.filter { ch -> ch.isDigit() || ch == ':' }.take(5)
                        },
                        label = { Text("Start (HH:mm)") },
                        supportingText = { Text("Will send: ${dateUtc}T${startHHmm}:00Z (UTC)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = durationText,
                        onValueChange = { durationText = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Duration (minutes)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // ---- Notes ----
                    Text(
                        text = "Notes",
                        style = MaterialTheme.typography.labelLarge
                    )

                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Notes (optional)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // ---- Repeat pattern ----
                    Text(
                        text = "Repeat",
                        style = MaterialTheme.typography.labelLarge
                    )

                    var repeatExpanded by remember { mutableStateOf(false) }
                    val patterns = listOf("none", "daily", "weekdays", "weekends")

                    OutlinedButton(onClick = { repeatExpanded = true }) {
                        val label = when (repeatPattern) {
                            "none" -> "No repeat"
                            "daily" -> "Daily"
                            "weekdays" -> "Weekdays"
                            "weekends" -> "Weekends"
                            else -> repeatPattern
                        }
                        Text(label)
                    }

                    DropdownMenu(
                        expanded = repeatExpanded,
                        onDismissRequest = { repeatExpanded = false }
                    ) {
                        patterns.forEach { pattern ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        when (pattern) {
                                            "none" -> "No repeat"
                                            "daily" -> "Daily"
                                            "weekdays" -> "Weekdays"
                                            "weekends" -> "Weekends"
                                            else -> pattern
                                        }
                                    )
                                },
                                onClick = {
                                    repeatPattern = pattern
                                    repeatExpanded = false
                                }
                            )
                        }
                    }

                    // ---- Weekday chips ----
                    Text(
                        text = "Specific weekdays (optional)",
                        style = MaterialTheme.typography.labelLarge
                    )

                    val dayLabels = listOf("M", "T", "W", "T", "F", "S", "S")

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        (1..7).forEach { day ->
                            val selected = selectedWeekDays.contains(day)
                            FilterChip(
                                selected = selected,
                                onClick = {
                                    selectedWeekDays =
                                        if (selected) selectedWeekDays - day else selectedWeekDays + day
                                },
                                label = { Text(dayLabels[day - 1]) }
                            )
                        }
                    }

                    // Hibaüzenet a tartalom végén
                    if (error != null) {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                // 🔹 ALUL: fix Create gomb (mindig látszik)
                val buttonEnabled =
                    selectedHabitIndex != null &&
                            dateUtc.isNotBlank() &&
                            startHHmm.length == 5 &&
                            durationText.isNotBlank() &&
                            !loading

                Button(
                    onClick = {
                        val idx = selectedHabitIndex
                        val habitId = idx?.let { habits[it].id }
                        val duration = durationText.toIntOrNull()

                        // HH:mm -> ISO-8601 Z (UTC)
                        val startIso = runCatching {
                            val t = LocalTime.parse(
                                startHHmm,
                                DateTimeFormatter.ofPattern("HH:mm")
                            )
                            "${dateUtc}T${t.format(DateTimeFormatter.ofPattern("HH:mm"))}:00Z"
                        }.getOrNull()

                        if (habitId != null && startIso != null && duration != null) {
                            vm.createSchedule(
                                habitId = habitId,
                                dateUtc = dateUtc,
                                startIso = startIso,
                                endIso = null,
                                durationMinutes = duration,
                                notes = notes.ifBlank { null },
                                repeatPattern = repeatPattern,
                                daysOfWeek = selectedWeekDays.toList(),
                                repeatDays = 30,
                                numberOfWeeks = 4
                            )
                        }
                    },
                    enabled = buttonEnabled,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Text("Create schedule")
                }
            }

            // Loading overlay
            if (loading) {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            }
        }
    }
}
