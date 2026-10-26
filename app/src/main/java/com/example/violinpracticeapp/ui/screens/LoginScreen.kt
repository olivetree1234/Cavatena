package com.example.violinpracticeapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.violinpracticeapp.data.UserDao
import com.example.violinpracticeapp.data.UserEntity
import com.example.violinpracticeapp.ui.header.AppHeader
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    userDao: UserDao,
    onLoginSuccess: () -> Unit
) {
    var isSignUpMode by remember { mutableStateOf(false) }

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AppHeader(
                        subtitle = if (isSignUpMode) {
                            "Create your practice account"
                        } else {
                            "Build your daily practice habit"
                        }
                    )

                    if (isSignUpMode) {
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Name") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        keyboardOptions = KeyboardOptions(
                            autoCorrectEnabled = false,
                            keyboardType = KeyboardType.Email
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Button(
                        onClick = {
                            scope.launch {
                                errorMessage = ""

                                if (email.isBlank() || password.isBlank()) {
                                    errorMessage = "Please enter email and password"
                                    return@launch
                                }

                                if (isSignUpMode) {
                                    if (name.isBlank()) {
                                        errorMessage = "Please enter your name"
                                        return@launch
                                    }

                                    val existingUser = userDao.getUserByEmail(email)

                                    if (existingUser != null) {
                                        errorMessage = "Email already exists"
                                        return@launch
                                    }

                                    userDao.insertUser(
                                        UserEntity(
                                            name = name,
                                            email = email,
                                            password = password
                                        )
                                    )

                                    onLoginSuccess()
                                } else {
                                    val user = userDao.loginUser(
                                        email = email,
                                        password = password
                                    )

                                    if (user != null) {
                                        onLoginSuccess()
                                    } else {
                                        errorMessage = "Invalid email or password"
                                    }
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (isSignUpMode) {
                                "Create Account"
                            } else {
                                "Start Practicing"
                            }
                        )
                    }

                    if (errorMessage.isNotEmpty()) {
                        Text(
                            text = errorMessage,
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isSignUpMode) {
                                "Already have an account? "
                            } else {
                                "🎻 New here? "
                            }
                        )

                        TextButton(
                            onClick = {
                                isSignUpMode = !isSignUpMode
                                errorMessage = ""
                            },
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(
                                text = if (isSignUpMode) {
                                    "Log In"
                                } else {
                                    "Create Account"
                                },
                                color = MaterialTheme.colorScheme.primary,
                                textDecoration = TextDecoration.Underline
                            )
                        }
                    }
                }
            }
        }
    }
}