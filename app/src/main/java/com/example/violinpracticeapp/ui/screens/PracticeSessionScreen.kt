package com.example.violinpracticeapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.violinpracticeapp.data.PracticePlanWithLibraryItem
import com.example.violinpracticeapp.data.PracticeSessionDao
import com.example.violinpracticeapp.data.PracticeSessionEntity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate

@Composable
fun PracticeSessionScreen(
    practiceItem: PracticePlanWithLibraryItem,
    practiceSessionDao: PracticeSessionDao,
    onBackClick: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val today = LocalDate.now().toString()

    val practiceTitle = practiceItem.libraryItem?.title
        ?: practiceItem.plan.customNotes

    val plannedSeconds = practiceItem.plan.durationMinutes * 60

    val latestSession by practiceSessionDao
        .getLatestSessionForPlan(
            date = today,
            practicePlanId = practiceItem.plan.id
        )
        .collectAsState(initial = null)

    var remainingSeconds by remember(practiceItem.plan.id) {
        mutableIntStateOf(plannedSeconds)
    }

    var loadedSessionId by remember(practiceItem.plan.id) {
        mutableStateOf<Int?>(null)
    }

    LaunchedEffect(latestSession?.id) {
        if (latestSession != null && latestSession!!.id != loadedSessionId) {
            remainingSeconds = latestSession!!.remainingSeconds
            loadedSessionId = latestSession!!.id
        }
    }

    var isRunning by remember { mutableStateOf(false) }

    LaunchedEffect(isRunning) {
        while (isRunning && remainingSeconds > 0) {
            delay(1000)
            remainingSeconds--
        }

        if (remainingSeconds == 0) {
            isRunning = false
        }
    }

    val completedSeconds = plannedSeconds - remainingSeconds

    val completedMinutesForSave = completedSeconds / 60
    val remainingMinutesForSave = remainingSeconds / 60

    val completedTime = formatPracticeTime(completedSeconds)
    val remainingTime = formatPracticeTime(remainingSeconds)

    val progress = if (plannedSeconds > 0) {
        completedSeconds.toFloat() / plannedSeconds.toFloat()
    } else {
        0f
    }.coerceIn(0f, 1f)

    val formattedTime = formatPracticeTime(remainingSeconds)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextButton(
            onClick = {
                isRunning = false
                onBackClick()
            },
            modifier = Modifier.align(Alignment.Start)
        ) {
            Text("← Back")
        }

        Text(
            text = "🎻 Practice Timer",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        OutlinedCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = practiceItem.plan.category,
                    style = MaterialTheme.typography.titleLarge
                )

                if (practiceTitle.isNotBlank()) {
                    Text(
                        text = practiceTitle,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                Text(
                    text = "Planned time: ${practiceItem.plan.durationMinutes} minutes",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Text(
            text = "Time Remaining",
            style = MaterialTheme.typography.titleLarge
        )

        Text(
            text = formattedTime,
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.primary
        )

        OutlinedCard(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Practice Progress",
                    style = MaterialTheme.typography.titleLarge
                )

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp),
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(
                onClick = {
                    isRunning = true
                },
                enabled = !isRunning && remainingSeconds > 0
            ) {
                Text("Start")
            }

            OutlinedButton(
                onClick = {
                    isRunning = false
                },
                enabled = isRunning
            ) {
                Text("Pause")
            }

            OutlinedButton(
                onClick = {
                    isRunning = false
                    remainingSeconds = plannedSeconds
                }
            ) {
                Text("Reset")
            }
        }

        Button(
            onClick = {
                scope.launch {
                    isRunning = false

                    practiceSessionDao.insertSession(

                        PracticeSessionEntity(
                            date = today,
                            practicePlanId = practiceItem.plan.id,
                            libraryItemId = practiceItem.plan.libraryItemId,
                            category = practiceItem.plan.category,
                            title = practiceTitle,
                            plannedMinutes = practiceItem.plan.durationMinutes,
                            completedSeconds = completedSeconds,
                            remainingSeconds = remainingSeconds,
                            completed = remainingSeconds == 0
                        )

                    )

                    onBackClick()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Complete Practice")
        }
    }
}

private fun formatPracticeTime(
    totalSeconds: Int
): String {
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60

    return "%02d:%02d".format(minutes, seconds)
}