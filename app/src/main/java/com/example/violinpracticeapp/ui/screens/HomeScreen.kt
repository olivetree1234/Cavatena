package com.example.violinpracticeapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.violinpracticeapp.R
import com.example.violinpracticeapp.data.PracticePlanDao
import com.example.violinpracticeapp.data.PracticeSessionDao
import androidx.compose.ui.text.style.TextAlign
import java.time.DayOfWeek
import java.time.LocalDate
import kotlin.math.roundToInt

@Composable
fun HomeScreen(
    practicePlanDao: PracticePlanDao,
    practiceSessionDao: PracticeSessionDao,
    onStartPracticeClick: () -> Unit
) {
    val todayDate = LocalDate.now()
    val today = todayDate.toString()

    val todayPlans by practicePlanDao
        .getPlansWithLibraryForDate(today)
        .collectAsState(initial = emptyList())

    val completedSecondsToday by practiceSessionDao
        .getCompletedSecondsForDate(today)
        .collectAsState(initial = 0)

    val totalPlannedMinutes = todayPlans.sumOf { it.plan.durationMinutes }
    val completedMinutes = (completedSecondsToday ?: 0) / 60
    val remainingMinutes = (totalPlannedMinutes - completedMinutes).coerceAtLeast(0)

    val progress = if (totalPlannedMinutes > 0) {
        completedMinutes.toFloat() / totalPlannedMinutes.toFloat()
    } else {
        0f
    }.coerceIn(0f, 1f)

    val progressPercent = (progress * 100).roundToInt()

    val todayStickerRes = dayStickerRes(todayDate.dayOfWeek)

    val stickerMessage = when {
        totalPlannedMinutes == 0 ->
            "Create a plan to start collecting stickers!"

        progress >= 1f ->
            "🏆 You completed today's practice!"

        progress >= 0.5f ->
            "⭐ Great progress! You're halfway there!"

        progress > 0f ->
            "🎻 Nice start! Keep going!"

        else ->
            "Start practicing to unlock today's sticker!"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "🎻",
                style = MaterialTheme.typography.displaySmall
            )

            Column {
                Text(
                    text = "Welcome Back! 👋",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "Keep practicing, keep improving!",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        OutlinedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.outlinedCardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Today's Progress",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "$progressPercent%",
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Text(
                            text = "Complete",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text = "Completed",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Text(
                            text = "$completedMinutes / $totalPlannedMinutes min",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Remaining",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.error
                        )

                        Text(
                            text = "$remainingMinutes min",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(14.dp),
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }
        }

        Button(
            onClick = onStartPracticeClick,
            enabled = todayPlans.isNotEmpty(),
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            shape = RoundedCornerShape(20.dp)
        ) {
            Text(
                text = "▶ Start Practicing",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        OutlinedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.outlinedCardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Column(

                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Today's Sticker",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                if (progress > 0f) {
                    Image(
                        painter = painterResource(todayStickerRes),
                        contentDescription = "Today's sticker",
                        modifier = Modifier.size(150.dp)
                    )
                } else {
                    Text(
                        text = "🔒",
                        style = MaterialTheme.typography.displayLarge
                    )
                }

                Text(
                    text = stickerMessage,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        OutlinedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Weekly Sticker Collection",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Collect a sticker every day you practice!",
                    style = MaterialTheme.typography.bodyMedium
                )

                WeeklyStickerGrid(
                    todayDate = todayDate,
                    todayProgress = progress
                )
            }
        }

    }
}

@Composable
private fun WeeklyStickerGrid(
    todayDate: LocalDate,
    todayProgress: Float
) {
    val weekStart = todayDate.minusDays(
        todayDate.dayOfWeek.value.toLong() - 1
    )

    val days = (0..6).map {
        weekStart.plusDays(it.toLong())
    }

    // Hide all past days, including their boxes.
    val visibleDays = days.filter { !it.isBefore(todayDate) }

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        visibleDays.chunked(4).forEach { rowDays ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowDays.forEach { date ->
                    WeeklyStickerItem(
                        date = date,
                        todayDate = todayDate,
                        todayProgress = todayProgress,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Keep card widths consistent across rows.
                repeat(4 - rowDays.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}
@Composable
private fun WeeklyStickerItem(
    date: LocalDate,
    todayDate: LocalDate,
    todayProgress: Float,
    modifier: Modifier = Modifier
) {
    val isToday = date == todayDate
    val isPastDay = date.isBefore(todayDate)

    val isUnlocked = isToday && todayProgress > 0f

    val dayLabel = when (date.dayOfWeek) {
        DayOfWeek.MONDAY -> "Mon"
        DayOfWeek.TUESDAY -> "Tue"
        DayOfWeek.WEDNESDAY -> "Wed"
        DayOfWeek.THURSDAY -> "Thu"
        DayOfWeek.FRIDAY -> "Fri"
        DayOfWeek.SATURDAY -> "Sat"
        DayOfWeek.SUNDAY -> "Sun"
    }

    OutlinedCard(
        modifier = modifier.height(135.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.outlinedCardColors(
            containerColor = if (isUnlocked) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = dayLabel,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )

            when {
                isUnlocked -> {
                    Image(
                        painter = painterResource(
                            dayStickerRes(date.dayOfWeek)
                        ),
                        contentDescription = "$dayLabel sticker",
                        modifier = Modifier.size(70.dp)
                    )
                }

                isPastDay -> {
                    // Hide the sticker for past days.
                    Spacer(modifier = Modifier.size(70.dp))
                }

                else -> {
                    Text(
                        text = "🔒",
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
            }

            Text(
                text = when {
                    isUnlocked -> "Unlocked"
                    isPastDay -> ""
                    else -> "Locked"
                },
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

private fun dayStickerRes(
    dayOfWeek: DayOfWeek
): Int {
    return when (dayOfWeek) {
        DayOfWeek.MONDAY -> R.drawable.monday
        DayOfWeek.TUESDAY -> R.drawable.tuesday
        DayOfWeek.WEDNESDAY -> R.drawable.wednesday
        DayOfWeek.THURSDAY -> R.drawable.thursday
        DayOfWeek.FRIDAY -> R.drawable.friday
        DayOfWeek.SATURDAY -> R.drawable.saturday
        DayOfWeek.SUNDAY -> R.drawable.sunday
    }
}