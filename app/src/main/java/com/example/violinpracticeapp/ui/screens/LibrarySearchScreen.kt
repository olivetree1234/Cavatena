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
import com.example.violinpracticeapp.data.LibraryItemEntity
import com.example.violinpracticeapp.data.PracticePlanDao
import com.example.violinpracticeapp.data.PracticePlanEntity
import kotlinx.coroutines.launch
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibrarySearchScreen(
    selectedDate: LocalDate,
    practicePlanDao: PracticePlanDao,
    libraryItemDao: LibraryItemDao,
    onBackClick: () -> Unit
) {

    val categories = listOf(
        "Scales",
        "Technique",
        "Etudes",
        "Pieces",
        "Orchestra"
    )

    val durations = listOf(5, 10, 15, 20, 30, 40, 45, 60, 90)

    val scope = rememberCoroutineScope()

    var selectedCategory by remember { mutableStateOf(categories.first()) }
    var keyword by remember { mutableStateOf("") }
    var selectedDuration by remember { mutableIntStateOf(20) }

    var categoryExpanded by remember { mutableStateOf(false) }
    var durationExpanded by remember { mutableStateOf(false) }

    var searchResults by remember {
        mutableStateOf(emptyList<LibraryItemEntity>())
    }

    var selectedItems by remember {
        mutableStateOf(setOf<LibraryItemEntity>())
    }

    Scaffold(
        bottomBar = {
            if (selectedItems.isNotEmpty()) {
                Surface(
                    tonalElevation = 8.dp,
                    shadowElevation = 8.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Selected: ${selectedItems.size} item(s)",
                            style = MaterialTheme.typography.titleMedium
                        )

                        ExposedDropdownMenuBox(
                            expanded = durationExpanded,
                            onExpandedChange = {
                                durationExpanded = !durationExpanded
                            }
                        ) {
                            OutlinedTextField(
                                value = "$selectedDuration minutes",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Duration for selected items") },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(
                                        durationExpanded
                                    )
                                },
                                modifier = Modifier
                                    .menuAnchor(
                                        type = ExposedDropdownMenuAnchorType.PrimaryNotEditable
                                    )
                                    .fillMaxWidth()
                            )

                            ExposedDropdownMenu(
                                expanded = durationExpanded,
                                onDismissRequest = {
                                    durationExpanded = false
                                }
                            ) {
                                durations.forEach { duration ->
                                    DropdownMenuItem(
                                        text = {
                                            Text("$duration minutes")
                                        },
                                        onClick = {
                                            selectedDuration = duration
                                            durationExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Button(
                            onClick = {
                                scope.launch {
                                    selectedItems.forEach { item ->
                                        practicePlanDao.insertPlan(
                                            PracticePlanEntity(
                                                date = selectedDate.toString(),
                                                category = item.category,
                                                libraryItemId = item.id,
                                                customNotes = "",
                                                durationMinutes = selectedDuration
                                            )
                                        )
                                    }

                                    onBackClick()
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                        ) {
                            Text("Add Selected to Plan")
                        }
                    }
                }
            }
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TextButton(onClick = onBackClick) {
                Text("← Back to Plan")
            }

            Text(
                text = "🔍 Search Library",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )

            ExposedDropdownMenuBox(
                expanded = categoryExpanded,
                onExpandedChange = {
                    categoryExpanded = !categoryExpanded
                }
            ) {
                OutlinedTextField(
                    value = selectedCategory,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Category") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            categoryExpanded
                        )
                    },
                    modifier = Modifier
                        .menuAnchor(
                            type = ExposedDropdownMenuAnchorType.PrimaryNotEditable
                        )
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = categoryExpanded,
                    onDismissRequest = {
                        categoryExpanded = false
                    }
                ) {
                    categories.forEach { category ->
                        DropdownMenuItem(
                            text = {
                                Text(category)
                            },
                            onClick = {
                                selectedCategory = category
                                searchResults = emptyList()
                                selectedItems = emptySet()
                                categoryExpanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = keyword,
                onValueChange = {
                    keyword = it
                },
                label = {
                    Text("Keywords")
                },
                placeholder = {
                    Text("Example: G Major, Kreutzer, Detaché")
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Button(
                onClick = {
                    scope.launch {
                        searchResults = libraryItemDao.searchLibraryItems(
                            category = selectedCategory,
                            keyword = keyword
                        )

                        selectedItems = emptySet()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Search")
            }

            if (searchResults.isEmpty()) {
                Text(
                    text = "Search for library items to add to your plan.",
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                Text(
                    text = "Search Results",
                    style = MaterialTheme.typography.titleLarge
                )

                searchResults.forEach { item ->
                    val isSelected = selectedItems.contains(item)

                    OutlinedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedItems = if (isSelected) {
                                    selectedItems - item
                                } else {
                                    selectedItems + item
                                }
                            },
                        colors = CardDefaults.outlinedCardColors(
                            containerColor = if (isSelected) {
                                MaterialTheme.colorScheme.primaryContainer
                            } else {
                                MaterialTheme.colorScheme.surface
                            }
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = isSelected,
                                onCheckedChange = { checked ->
                                    selectedItems = if (checked) {
                                        selectedItems + item
                                    } else {
                                        selectedItems - item
                                    }
                                }
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Column {
                                Text(
                                    text = item.title,
                                    style = MaterialTheme.typography.titleMedium
                                )

                                Text(
                                    text = item.composer,
                                    style = MaterialTheme.typography.bodySmall
                                )

                                if (item.notes.isNotBlank()) {
                                    Text(
                                        text = item.notes,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(140.dp))
            }
        }
    }
}