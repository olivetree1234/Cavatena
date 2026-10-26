package com.example.violinpracticeapp.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.violinpracticeapp.data.LibraryItemDao
import com.example.violinpracticeapp.data.LibraryItemEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun LibraryScreen(
    libraryItemDao: LibraryItemDao
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }
    var selectedFileName by remember { mutableStateOf("No file selected") }
    var statusMessage by remember { mutableStateOf("") }
    var isImporting by remember { mutableStateOf(false) }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            selectedFileUri = uri
            selectedFileName = uri.lastPathSegment ?: "Selected CSV file"
            statusMessage = "File selected. Ready to import."
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "📚 Music Library",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = "Import violin pieces from a CSV file.",
            style = MaterialTheme.typography.bodyLarge
        )

        OutlinedCard(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "CSV Format",
                    style = MaterialTheme.typography.titleLarge
                )

                Text("Required columns:")

                Text(
                    text = "title, category, composer, notes",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        OutlinedCard(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Import CSV",
                    style = MaterialTheme.typography.titleLarge
                )

                Text("Selected file: $selectedFileName")

                OutlinedButton(
                    onClick = {
                        filePickerLauncher.launch(
                            arrayOf(
                                "text/csv",
                                "text/comma-separated-values",
                                "text/plain",
                                "application/vnd.ms-excel"
                            )
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Browse CSV File")
                }

                Button(
                    onClick = {
                        val uri = selectedFileUri ?: return@Button

                        scope.launch {
                            isImporting = true
                            statusMessage = "Importing..."

                            val result = importLibraryCsv(
                                context = context,
                                uri = uri,
                                libraryItemDao = libraryItemDao
                            )

                            statusMessage = result
                            isImporting = false
                        }
                    },
                    enabled = selectedFileUri != null && !isImporting,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        if (isImporting) {
                            "Importing..."
                        } else {
                            "Upload / Import Library"
                        }
                    )
                }

                if (statusMessage.isNotBlank()) {
                    Text(
                        text = statusMessage,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

private suspend fun importLibraryCsv(
    context: android.content.Context,
    uri: Uri,
    libraryItemDao: LibraryItemDao
): String {
    return withContext(Dispatchers.IO) {
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
                ?: return@withContext "Could not open selected file."

            val lines = inputStream.bufferedReader().use { it.readLines() }

            if (lines.isEmpty()) {
                return@withContext "CSV file is empty."
            }

            val dataLines = lines.drop(1)

            val items = dataLines.mapNotNull { line ->
                val columns = parseCsvLine(line)

                if (columns.size < 4) {
                    null
                } else {
                    LibraryItemEntity(
                        title = columns[0].trim(),
                        category = columns[1].trim(),
                        composer = columns[2].trim(),
                        notes = columns[3].trim()
                    )
                }
            }.filter {
                it.title.isNotBlank() &&
                        it.category.isNotBlank()
            }

            if (items.isEmpty()) {
                return@withContext "No valid library items found."
            }

            val results = libraryItemDao.insertItems(items)

            val insertedCount = results.count { it != -1L }
            val skippedCount = results.count { it == -1L }

            "Successfully imported $insertedCount new items. " +
                    "Skipped $skippedCount duplicate items."

        } catch (e: Exception) {
            "Import failed: ${e.message}"
        }
    }
}

private fun parseCsvLine(
    line: String
): List<String> {
    val result = mutableListOf<String>()
    val current = StringBuilder()
    var insideQuotes = false

    for (char in line) {
        when {
            char == '"' -> {
                insideQuotes = !insideQuotes
            }

            char == ',' && !insideQuotes -> {
                result.add(current.toString())
                current.clear()
            }

            else -> {
                current.append(char)
            }
        }
    }

    result.add(current.toString())

    return result
}