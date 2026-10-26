package com.example.violinpracticeapp.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.violinpracticeapp.data.PracticePlanDao
import com.example.violinpracticeapp.data.PracticePlanWithLibraryItem
import com.example.violinpracticeapp.data.PracticeSessionDao
import java.time.LocalDate

@Composable
fun PracticeScreen(
    practicePlanDao: PracticePlanDao,
    practiceSessionDao: PracticeSessionDao
) {
    var activePracticeItem by remember {
        mutableStateOf<PracticePlanWithLibraryItem?>(null)
    }

    if (activePracticeItem != null) {
        PracticeSessionScreen(
            practiceItem = activePracticeItem!!,
            practiceSessionDao = practiceSessionDao,
            onBackClick = {
                activePracticeItem = null
            }
        )
    } else {
        PracticeSelectionScreen(
            practicePlanDao = practicePlanDao,
            onStartPractice = { selectedItem ->
                activePracticeItem = selectedItem
            }
        )
    }
}

@Composable
private fun PracticeSelectionScreen(
    practicePlanDao: PracticePlanDao,
    onStartPractice: (PracticePlanWithLibraryItem) -> Unit
) {
    val today = LocalDate.now().toString()

    val todayPlans by practicePlanDao
        .getPlansWithLibraryForDate(today)
        .collectAsState(initial = emptyList())

    var selectedPlan by remember {
        mutableStateOf<PracticePlanWithLibraryItem?>(null)
    }

    LaunchedEffect(todayPlans) {
        if (selectedPlan == null && todayPlans.isNotEmpty()) {
            selectedPlan = todayPlans.first()
        }
    }

    val selectedMinutes = selectedPlan?.plan?.durationMinutes ?: 0
    val selectedTitle = selectedPlan?.libraryItem?.title
        ?: selectedPlan?.plan?.customNotes
        ?: ""

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "🎻 Practice Session",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = "Choose a practice item from today's plan.",
            style = MaterialTheme.typography.bodyLarge
        )

        OutlinedCard(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Today's Plan",
                    style = MaterialTheme.typography.titleLarge
                )

                if (todayPlans.isEmpty()) {
                    Text("No practice plan for today. Add one in the Plan tab.")
                } else {
                    todayPlans.forEach { item ->
                        val isSelected = selectedPlan?.plan?.id == item.plan.id
                        val itemTitle = item.libraryItem?.title
                            ?: item.plan.customNotes

                        OutlinedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedPlan = item
                                },
                            border = BorderStroke(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.outline
                                }
                            ),
                            colors = CardDefaults.outlinedCardColors(
                                containerColor = if (isSelected) {
                                    MaterialTheme.colorScheme.primaryContainer
                                } else {
                                    MaterialTheme.colorScheme.surface
                                }
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = if (isSelected) {
                                        "✓ ${item.plan.category} - ${item.plan.durationMinutes} min"
                                    } else {
                                        "${item.plan.category} - ${item.plan.durationMinutes} min"
                                    },
                                    style = MaterialTheme.typography.titleMedium
                                )

                                if (itemTitle.isNotBlank()) {
                                    Text(
                                        text = itemTitle,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        OutlinedCard(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Selected Practice",
                    style = MaterialTheme.typography.titleLarge
                )

                if (selectedPlan == null) {
                    Text("No practice item selected.")
                } else {
                    Text("${selectedPlan!!.plan.category} - $selectedMinutes min")

                    if (selectedTitle.isNotBlank()) {
                        Text(selectedTitle)
                    }
                }
            }
        }

        Button(
            onClick = {
                selectedPlan?.let {
                    onStartPractice(it)
                }
            },
            enabled = selectedPlan != null,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Start ${selectedMinutes}-Minute Practice")
        }
    }
}