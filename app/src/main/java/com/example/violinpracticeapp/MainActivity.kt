package com.example.violinpracticeapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.violinpracticeapp.data.AppDatabase
import com.example.violinpracticeapp.data.LibrarySeedData
import com.example.violinpracticeapp.ui.navigation.MainScreen
import com.example.violinpracticeapp.ui.screens.LoginScreen
import com.example.violinpracticeapp.ui.theme.ViolinPracticeAppTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = AppDatabase.getDatabase(this)
        val userDao = database.userDao()
        val practicePlanDao = database.practicePlanDao()
        val libraryItemDao = database.libraryItemDao()
        val practiceSessionDao = database.practiceSessionDao()
        setContent {

            LaunchedEffect(Unit) {
                if (libraryItemDao.getLibraryCount() == 0) {
                    libraryItemDao.insertAll(
                        LibrarySeedData.getStarterLibrary()
                    )
                }
            }
            ViolinPracticeAppTheme {
                var isLoggedIn by remember { mutableStateOf(false) }

                if (isLoggedIn) {
                    MainScreen(
                        practicePlanDao = practicePlanDao,
                        libraryItemDao = libraryItemDao,
                        practiceSessionDao = practiceSessionDao
                    )
                } else {
                    LoginScreen(
                        userDao = userDao,
                        onLoginSuccess = {
                            isLoggedIn = true
                        }
                    )
                }
            }
        }
    }
}

