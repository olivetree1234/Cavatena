package com.example.violinpracticeapp.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.violinpracticeapp.data.LibraryItemDao
import com.example.violinpracticeapp.data.PracticePlanDao
import com.example.violinpracticeapp.data.PracticeSessionDao
import com.example.violinpracticeapp.ui.screens.AiCoachScreen
import com.example.violinpracticeapp.ui.screens.HomeScreen
import com.example.violinpracticeapp.ui.screens.LibraryScreen
import com.example.violinpracticeapp.ui.screens.PlanScreen
import com.example.violinpracticeapp.ui.screens.PracticeScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    practicePlanDao: PracticePlanDao,
    libraryItemDao: LibraryItemDao,
    practiceSessionDao : PracticeSessionDao
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    val tabs = listOf(
        BottomTab("Home", "Practice Dashboard", "🏠"),
        BottomTab("Plan", "Practice Plan", "📝"),
        BottomTab("Practice", "Practice Timer", "🎻"),
        BottomTab("Library", "Music Library", "📚"),
        BottomTab("AI", "AI Music Coach", "🤖"),
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(tabs[selectedTab].title)
                }
            )
        },
        bottomBar = {
            NavigationBar(modifier = Modifier.height(100.dp))
            {
                tabs.forEachIndexed { index, tab ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = {
                            selectedTab = index
                        },
                        icon = {
                            Text(
                                text = tab.icon,
                                fontSize = if (selectedTab == index) 40.sp else 35.sp
                            )
                        },
                        label = {
                            Text(tab.label)
                        },
                        alwaysShowLabel = true
                    )
                }
            }
        }
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            when (selectedTab) {
                0 -> HomeScreen(practicePlanDao = practicePlanDao,
                                practiceSessionDao = practiceSessionDao,
                                onStartPracticeClick = {
                                    selectedTab = 2
                                })
                1 -> PlanScreen(practicePlanDao = practicePlanDao,libraryItemDao = libraryItemDao)
                2 -> PracticeScreen(practicePlanDao = practicePlanDao, practiceSessionDao = practiceSessionDao)
                3 -> LibraryScreen(libraryItemDao = libraryItemDao )
                4 -> AiCoachScreen(libraryItemDao = libraryItemDao)
            }
        }
    }
}

private data class BottomTab(
    val label: String,
    val title: String,
    val icon: String
)