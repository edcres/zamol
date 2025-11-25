package com.example.zamol.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.zamol.viewmodel.AuthViewModel
import com.example.zamol.viewmodel.AuthState
import com.google.firebase.BuildConfig

//Uses AuthViewModel to call login or signup
//Displays a loading spinner while authenticating
//Shows error messages if login/signup fails
//Switches between login and signup modes
//Calls onAuthSuccess() when login/signup is successful (so you can navigate to chat screen)

private const val TEST_USER_1_EMAIL = "test1@example.com"
private const val TEST_USER_1_PASSWORD = "password123"

private const val TEST_USER_2_EMAIL = "test2@example.com"
private const val TEST_USER_2_PASSWORD = "password123"

@Composable
fun AuthScreen(
    onAuthSuccess: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val authState by viewModel.authState.collectAsStateWithLifecycle()

    var isLogin by remember { mutableStateOf(true) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var displayName by remember { mutableStateOf("") }

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            onAuthSuccess()
            viewModel.resetState()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (isLogin) "Login" else "Sign Up",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (!isLogin) {
            OutlinedTextField(
                value = displayName,
                onValueChange = { displayName = it },
                label = { Text("Display Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Normal login / signup button
        Button(
            onClick = {
                if (isLogin) {
                    viewModel.login(email, password)
                } else {
                    viewModel.signup(email, password, displayName)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = authState !is AuthState.Loading
        ) {
            Text(if (isLogin) "Login" else "Sign Up")
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = { isLogin = !isLogin }) {
            Text(
                text = if (isLogin)
                    "Don't have an account? Sign up"
                else
                    "Already have an account? Log in"
            )
        }

        // 🔥 Dev test user buttons – always visible for now
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Dev test users",
            style = MaterialTheme.typography.labelMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = {
                    viewModel.login(TEST_USER_1_EMAIL, TEST_USER_1_PASSWORD)
                },
                modifier = Modifier.weight(1f),
                enabled = authState !is AuthState.Loading
            ) {
                Text("Test User 1")
            }

            OutlinedButton(
                onClick = {
                    viewModel.login(TEST_USER_2_EMAIL, TEST_USER_2_PASSWORD)
                },
                modifier = Modifier.weight(1f),
                enabled = authState !is AuthState.Loading
            ) {
                Text("Test User 2")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        when (authState) {
            is AuthState.Loading -> {
                Spacer(modifier = Modifier.height(12.dp))
                CircularProgressIndicator()
            }
            is AuthState.Error -> {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = (authState as AuthState.Error).message,
                    color = MaterialTheme.colorScheme.error
                )
            }
            else -> {}
        }
    }
}

