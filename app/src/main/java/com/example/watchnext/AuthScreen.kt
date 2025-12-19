package com.example.watchnext

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.PaddingValues

// Which tab is selected: Login or Sign Up
enum class AuthMode { LOGIN, SIGN_UP }

/**
 * Simple Login / Signup screen with local "auth".
 * - Sign Up saves email + password in SharedPreferences
 * - Login checks those values before allowing entry
 */
@Composable
fun AuthScreen(
    modifier: Modifier = Modifier,
    onLoginSuccess: () -> Unit      // called only when login is correct
) {
    var authMode by rememberSaveable { mutableStateOf(AuthMode.LOGIN) }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }

    val context = LocalContext.current

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Fake avatar block at the top (like your wireframe)
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )

            Spacer(Modifier.height(24.dp))

            Text(
                text = if (authMode == AuthMode.LOGIN) "WatchNext" else "Create an account",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(Modifier.height(24.dp))

            // Tabs: Login / Sign Up
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AuthTabButton(
                    text = "Login",
                    selected = authMode == AuthMode.LOGIN,
                    modifier = Modifier.weight(1f)
                ) {
                    authMode = AuthMode.LOGIN
                    errorMessage = null
                }
                AuthTabButton(
                    text = "Sign Up",
                    selected = authMode == AuthMode.SIGN_UP,
                    modifier = Modifier.weight(1f)
                ) {
                    authMode = AuthMode.SIGN_UP
                    errorMessage = null
                }
            }

            Spacer(Modifier.height(24.dp))

            // Email
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Username") },
                singleLine = true
            )

            Spacer(Modifier.height(12.dp))

            // Password
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Password") },
                singleLine = true
            )

            Spacer(Modifier.height(24.dp))

            // Error / info message
            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )
            }

            // Big bottom button
            Button(
                onClick = {
                    val prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
                    val savedEmail = prefs.getString("email", null)
                    val savedPassword = prefs.getString("password", null)

                    if (authMode == AuthMode.SIGN_UP) {
                        // very basic validation
                        if (email.isBlank() || password.isBlank()) {
                            errorMessage = "Email and password can’t be empty."
                        } else {
                            // save credentials locally
                            prefs.edit()
                                .putString("email", email)
                                .putString("password", password)
                                .apply()

                            errorMessage = "Account created! You can now log in."
                            authMode = AuthMode.LOGIN
                            password = ""
                        }
                    } else { // LOGIN
                        if (savedEmail == null || savedPassword == null) {
                            errorMessage = "No account found. Please sign up first."
                        } else if (email == savedEmail && password == savedPassword) {
                            errorMessage = null
                            onLoginSuccess()
                        } else {
                            errorMessage = "Invalid email or password."
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = if (authMode == AuthMode.LOGIN) "Sign In" else "Create Account"
                )
            }
        }
    }
}

@Composable
private fun AuthTabButton(
    text: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val containerColor =
        if (selected) MaterialTheme.colorScheme.onBackground
        else MaterialTheme.colorScheme.surfaceVariant

    val contentColor =
        if (selected) MaterialTheme.colorScheme.background
        else MaterialTheme.colorScheme.onSurfaceVariant

    Button(
        onClick = onClick,
        modifier = modifier.height(36.dp),
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        Text(text)
    }
}