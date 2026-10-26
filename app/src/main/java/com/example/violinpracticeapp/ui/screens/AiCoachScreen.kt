package com.example.violinpracticeapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.first
import com.example.violinpracticeapp.data.LibraryItemDao
import kotlinx.coroutines.launch

@Composable
fun AiCoachScreen(
    libraryItemDao: LibraryItemDao
) {
    val scope = rememberCoroutineScope()
    var question by remember { mutableStateOf("") }
    var answer by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "🤖 AI Music Coach",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = "Ask questions about your music library.",
            style = MaterialTheme.typography.bodyLarge
        )

        OutlinedTextField(
            value = question,
            onValueChange = { question = it },
            label = { Text("Ask a question") },
            placeholder = { Text("Example: How many etudes are there?") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )

        Button(
            onClick = {
                scope.launch {
                    isLoading = true
                    try {
                        answer = answerLibraryQuestion(question, libraryItemDao)
                    } catch (e: Exception) {
                        answer = "Unable to search the library: ${e.message}"
                    } finally {
                        isLoading = false
                    }
                }
            },
            enabled = question.isNotBlank() && !isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isLoading) "Thinking..." else "Ask")
        }

        if (answer.isNotBlank()) {
            OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Answer", style = MaterialTheme.typography.titleLarge)
                    Text(answer)
                }
            }
        }
    }
}

private suspend fun answerLibraryQuestion(
    question: String,
    libraryItemDao: LibraryItemDao
): String {
    val lower = question.lowercase()

    val category = when {
        lower.contains("piece") -> "Pieces"
        lower.contains("etude") -> "Etudes"
        lower.contains("orchestra") -> "Orchestra"
        lower.contains("scale") || lower.contains("arpeggio") -> "Scales"
        lower.contains("bowing") ||
                lower.contains("double stop") ||
                lower.contains("double-stop") ||
                lower.contains("technique") -> "Technique"
        else -> null
    }

    return when {
        lower.contains("how many") && category != null -> {
            val count = libraryItemDao.countItemsByCategory(category = category)
            "There are $count item(s) in the $category category."
        }

        lower.contains("list") && category != null -> {
            val items = libraryItemDao
                .getItemsByCategory(category)
                .first()

            if (items.isEmpty()) {
                "I could not find any items in $category."
            } else {
                items.take(20).joinToString("\n") {
                    "• ${it.title} — ${it.composer}"
                }
            }
        }

        lower.contains("show") ||
                lower.contains("find") ||
                lower.contains("search") -> {
            val keyword = question
                .replace("show", "", ignoreCase = true)
                .replace("find", "", ignoreCase = true)
                .replace("search", "", ignoreCase = true)
                .trim()

            if (keyword.isBlank()) {
                "Please enter a keyword to search."
            } else {
                val items = libraryItemDao.searchLibraryByKeyword(keyword)
                if (items.isEmpty()) {
                    "I could not find anything for \"$keyword\"."
                } else {
                    items.take(20).joinToString("\n") {
                        "• ${it.title} — ${it.composer}"
                    }
                }
            }
        }

        else -> {
            "I can answer library questions like:\n\n" +
                    "• How many scales?\n" +
                    "• How many etudes?\n" +
                    "• List pieces\n" +
                    "• List technique\n" +
                    "• List orchestra\n" +
                    "• Show Kreutzer"
        }
    }
}
