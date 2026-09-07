package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.MainActivity
import com.example.data.AuthManager
import com.example.data.entity.UserEntity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
fun SignUpScreen(
    onNavigateBack: () -> Unit,
    onSignUpSuccess: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var displayName by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val repository = (context as MainActivity).repository

    val isPhoneAuth = AuthManager.isFirebaseConfigured && FirebaseAuth.getInstance().currentUser != null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(if (isPhoneAuth) "Complete Profile" else "Create Account", style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = displayName,
            onValueChange = { displayName = it },
            label = { Text("Display Name") },
            modifier = Modifier.fillMaxWidth()
        )
        
        if (!isPhoneAuth) {
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (errorMsg != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(errorMsg!!, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = {
                if (username.isNotBlank() && displayName.isNotBlank() && (isPhoneAuth || password.isNotBlank())) {
                    scope.launch {
                        val existing = repository.getUserByUsername(username)
                        if (existing != null) {
                            errorMsg = "Username already taken."
                        } else {
                            val newId = if (isPhoneAuth) FirebaseAuth.getInstance().currentUser!!.uid else UUID.randomUUID().toString()
                            val newUser = UserEntity(
                                id = newId,
                                username = username,
                                displayName = displayName,
                                profilePicUrl = null,
                                bio = "New to Utsav!"
                            )
                            repository.insertUser(newUser)
                            AuthManager.login(newId)
                            onSignUpSuccess()
                        }
                    }
                } else {
                    errorMsg = "Please fill all fields."
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isPhoneAuth) "Finish Setup" else "Sign Up")
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = {
            if (isPhoneAuth) {
                FirebaseAuth.getInstance().signOut()
            }
            onNavigateBack()
        }) {
            Text("Back to Login")
        }
    }
}
