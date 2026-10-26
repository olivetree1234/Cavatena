package com.example.violinpracticeapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.violinpracticeapp.data.LibraryItemDao
import com.example.violinpracticeapp.data.PracticePlanDao
import com.example.violinpracticeapp.data.PracticePlanWithLibraryItem
import com.example.violinpracticeapp.ui.theme.categoryColor
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun PlanScreen(
    practicePlanDao: PracticePlanDao,
    libraryItemDao: LibraryItemDao
) {
    var showLibrarySearch by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }

    if (showLibrarySearch) {
        LibrarySearchScreen(
            selectedDate = selectedDate,
            practicePlanDao = practicePlanDao,
            libraryItemDao = libraryItemDao,
            onBackClick = { showLibrarySearch = false }
        )
    } else {
        PlanMainScreen(
            practicePlanDao = practicePlanDao,
            selectedDate = selectedDate,
            onSelectedDateChange = { selectedDate = it },
            onOpenLibrarySearch = { showLibrarySearch = true }
        )
    }
}

@Composable
private fun PlanMainScreen(
    practicePlanDao: PracticePlanDao,
    selectedDate: LocalDate,
    onSelectedDateChange: (LocalDate) -> Unit,
    onOpenLibrarySearch: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val today = LocalDate.now()

    var weekStart by remember {
        mutableStateOf(today.minusDays(today.dayOfWeek.value.toLong() - 1))
    }

    val weekDates = remember(weekStart) {
        (0..6).map { weekStart.plusDays(it.toLong()) }
    }

    val selectedPlans by practicePlanDao
        .getPlansWithLibraryForDate(selectedDate.toString())
        .collectAsState(initial = emptyList())

    var planToDelete by remember {
        mutableStateOf<PracticePlanWithLibraryItem?>(null)
    }

    var planToEditTime by remember {
        mutableStateOf<PracticePlanWithLibraryItem?>(null)
    }

    var editedDuration by remember {
        mutableIntStateOf(20)
    }

    val dayFormatter = DateTimeFormatter.ofPattern("EEE")
    val dateFormatter = DateTimeFormatter.ofPattern("MMM d")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "📝 Weekly Practice Plan",
            style = MaterialTheme.typography.headlineMedium
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(
                onClick = {
                    weekStart = weekStart.minusWeeks(1)
                    onSelectedDateChange(weekStart)
                }
            ) {
                Text("← Previous")
            }

            Text(
                text = "${weekStart.format(dateFormatter)} - ${weekStart.plusDays(6).format(dateFormatter)}",
                style = MaterialTheme.typography.titleMedium
            )

            OutlinedButton(
                onClick = {
                    weekStart = weekStart.plusWeeks(1)
                    onSelectedDateChange(weekStart)
                }
            ) {
                Text("Next →")
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            weekDates.forEach { date ->
                val isSelected = date == selectedDate

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            MaterialTheme.colorScheme.surface
                        }
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            onSelectedDateChange(date)
                        }
                ) {
                    Column(
                        modifier = Modifier.padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = date.format(dayFormatter),
                            style = MaterialTheme.typography.labelMedium
                        )

                        Text(
                            text = date.dayOfMonth.toString(),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }

        Button(
            onClick = onOpenLibrarySearch,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text("🔍 Search & Add Library Items")
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "Plan for ${selectedDate.format(dateFormatter)}",
                    style = MaterialTheme.typography.titleLarge
                )

                if (selectedPlans.isEmpty()) {
                    Text("No practice planned for this day.")
                } else {
                    selectedPlans.forEachIndexed { index, item ->
                        val title = item.libraryItem?.title ?: item.plan.customNotes

                        OutlinedCard(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.outlinedCardColors(
                                containerColor = categoryColor(item.plan.category)
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(
                                    horizontal = 12.dp,
                                    vertical = 8.dp
                                ),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "${item.plan.category} - ${item.plan.durationMinutes} min",
                                    style = MaterialTheme.typography.titleMedium
                                )

                                if (title.isNotBlank()) {
                                    Text(
                                        text = title,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    TextButton(
                                        onClick = {
                                            planToEditTime = item
                                            editedDuration = item.plan.durationMinutes
                                        }
                                    ) {
                                        Text("Edit Time")
                                    }

                                    TextButton(
                                        onClick = {
                                            planToDelete = item
                                        }
                                    ) {
                                        Text("Delete")
                                    }
                                }
                            }
                        }

                        if (index < selectedPlans.lastIndex) {
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 12.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Total: ${selectedPlans.sumOf { it.plan.durationMinutes }} minutes",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }

    if (planToEditTime != null) {
        AlertDialog(
            onDismissRequest = {
                planToEditTime = null
            },
            title = {
                Text("Change Practice Time")
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = planToEditTime!!.libraryItem?.title
                            ?: planToEditTime!!.plan.customNotes
                            ?: planToEditTime!!.plan.category
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = {
                                editedDuration = (editedDuration - 5).coerceAtLeast(5)
                            }
                        ) {
                            Text("-5")
                        }

                        Text(
                            text = "$editedDuration min",
                            style = MaterialTheme.typography.headlineSmall
                        )

                        OutlinedButton(
                            onClick = {
                                editedDuration += 5
                            }
                        ) {
                            Text("+5")
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            practicePlanDao.updatePlan(
                                planToEditTime!!.plan.copy(
                                    durationMinutes = editedDuration
                                )
                            )

                            planToEditTime = null
                        }
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        planToEditTime = null
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    if (planToDelete != null) {
        AlertDialog(
            onDismissRequest = {
                planToDelete = null
            },
            title = {
                Text("Delete Plan?")
            },
            text = {
                Text("Are you sure you want to delete this practice item?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            practicePlanDao.deletePlan(planToDelete!!.plan)
                            planToDelete = null
                        }
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        planToDelete = null
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}