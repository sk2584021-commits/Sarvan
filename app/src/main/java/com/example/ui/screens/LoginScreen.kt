package com.example.ui.screens

import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.MainActivity
import com.example.data.AuthManager
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.FirebaseException
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun LoginScreen(
    onNavigateToSignUp: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    var loginMode by remember { mutableStateOf("CHOICE") } // CHOICE, EMAIL, PHONE, OTP
    val context = LocalContext.current
    val repository = (context as MainActivity).repository
    val scope = rememberCoroutineScope()

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    var phoneNumber by remember { mutableStateOf("") }
    var countryCode by remember { mutableStateOf("+91") }
    var otpCode by remember { mutableStateOf("") }
    var verificationId by remember { mutableStateOf<String?>(null) }
    var resendToken by remember { mutableStateOf<PhoneAuthProvider.ForceResendingToken?>(null) }
    var resendCooldown by remember { mutableStateOf(0) }

    // Cooldown timer
    LaunchedEffect(resendCooldown) {
        if (resendCooldown > 0) {
            kotlinx.coroutines.delay(1000L)
            resendCooldown--
        }
    }

    val sendOtp = {
        errorMsg = null
        if (AuthManager.isFirebaseConfigured) {
            val options = PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
                .setPhoneNumber("$countryCode$phoneNumber")
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(context as Activity)
                .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                        scope.launch {
                            try {
                                FirebaseAuth.getInstance().signInWithCredential(credential).await()
                                val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch
                                val user = repository.getUser(userId)
                                if (user != null) {
                                    AuthManager.login(userId)
                                    onLoginSuccess()
                                } else {
                                    onNavigateToSignUp()
                                }
                            } catch (e: Exception) {
                                errorMsg = e.localizedMessage
                            }
                        }
                    }

                    override fun onVerificationFailed(e: FirebaseException) {
                        errorMsg = e.localizedMessage
                    }

                    override fun onCodeSent(
                        verificationIdResult: String,
                        token: PhoneAuthProvider.ForceResendingToken
                    ) {
                        verificationId = verificationIdResult
                        resendToken = token
                        resendCooldown = 60
                        loginMode = "OTP"
                    }
                })
                .build()
            PhoneAuthProvider.verifyPhoneNumber(options)
        } else {
            // Mock flow for local prototype
            verificationId = "mock_verification_id"
            resendCooldown = 60
            loginMode = "OTP"
        }
    }

    val verifyOtp = {
        errorMsg = null
        scope.launch {
            if (AuthManager.isFirebaseConfigured) {
                if (verificationId != null) {
                    try {
                        val credential = PhoneAuthProvider.getCredential(verificationId!!, otpCode)
                        FirebaseAuth.getInstance().signInWithCredential(credential).await()
                        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch
                        val user = repository.getUser(userId)
                        if (user != null) {
                            AuthManager.login(userId)
                            onLoginSuccess()
                        } else {
                            onNavigateToSignUp()
                        }
                    } catch (e: Exception) {
                        errorMsg = "Invalid or expired OTP."
                    }
                }
            } else {
                // Mock local OTP validation
                if (otpCode == "123456") {
                    val user = repository.getUserByUsername(phoneNumber)
                    if (user != null) {
                        AuthManager.login(user.id)
                        onLoginSuccess()
                    } else {
                        onNavigateToSignUp()
                    }
                } else {
                    errorMsg = "Invalid mock OTP. Use 123456."
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Utsav", style = MaterialTheme.typography.displayLarge, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Connect. Celebrate. Share.", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(32.dp))

        if (loginMode == "CHOICE") {
            Button(onClick = { loginMode = "EMAIL" }, modifier = Modifier.fillMaxWidth()) {
                Text("Continue with Email / Username")
            }
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedButton(onClick = { loginMode = "PHONE" }, modifier = Modifier.fillMaxWidth()) {
                Text("Continue with Mobile Number")
            }
            Spacer(modifier = Modifier.height(32.dp))
            TextButton(onClick = onNavigateToSignUp) {
                Text("Don't have an account? Sign up")
            }
        } else if (loginMode == "EMAIL") {
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Email / Username") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth()
            )

            if (errorMsg != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(errorMsg!!, color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    scope.launch {
                        val user = repository.getUserByUsername(username)
                        if (user != null) {
                            AuthManager.login(user.id)
                            onLoginSuccess()
                        } else {
                            errorMsg = "User not found or invalid credentials."
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Login")
            }
            Spacer(modifier = Modifier.height(16.dp))
            TextButton(onClick = { loginMode = "CHOICE" }) {
                Text("Back")
            }
        } else if (loginMode == "PHONE") {
            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = countryCode,
                    onValueChange = { countryCode = it },
                    label = { Text("Code") },
                    modifier = Modifier.weight(0.3f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    label = { Text("Mobile Number") },
                    modifier = Modifier.weight(0.7f)
                )
            }

            if (errorMsg != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(errorMsg!!, color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    if (phoneNumber.length == 10) {
                        sendOtp()
                    } else {
                        errorMsg = "Please enter a valid 10-digit number."
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Send OTP")
            }
            Spacer(modifier = Modifier.height(16.dp))
            TextButton(onClick = { loginMode = "CHOICE" }) {
                Text("Back")
            }
        } else if (loginMode == "OTP") {
            Text("Enter the 6-digit OTP sent to $countryCode $phoneNumber")
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = otpCode,
                onValueChange = { if (it.length <= 6) otpCode = it },
                label = { Text("6-Digit OTP") },
                modifier = Modifier.fillMaxWidth()
            )

            if (errorMsg != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(errorMsg!!, color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = { verifyOtp() },
                modifier = Modifier.fillMaxWidth(),
                enabled = otpCode.length == 6
            ) {
                Text("Verify OTP")
            }
            Spacer(modifier = Modifier.height(16.dp))
            TextButton(
                onClick = { sendOtp() },
                enabled = resendCooldown == 0
            ) {
                Text(if (resendCooldown > 0) "Resend OTP in ${resendCooldown}s" else "Resend OTP")
            }
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(onClick = { loginMode = "PHONE" }) {
                Text("Change Mobile Number")
            }
        }
    }
}
