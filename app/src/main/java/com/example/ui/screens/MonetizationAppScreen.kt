package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonetizationAppScreen(
    onBack: () -> Unit
) {
    var applicationStep by remember { mutableStateOf(0) } // 0: Eligibility, 1: Form, 2: Success
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Monetization") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding).padding(16.dp).verticalScroll(rememberScrollState())) {
            
            if (applicationStep == 0) {
                Text("Eligibility Requirements", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(16.dp))
                
                val followers = 5200 // Mock user data
                val watchTime = 1250 // Mock user data
                
                Text("Followers: $followers / 5,000")
                LinearProgressIndicator(progress = { (followers / 5000f).coerceIn(0f, 1f) }, modifier = Modifier.fillMaxWidth())
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text("Watch Time: $watchTime / 1,000 hours")
                LinearProgressIndicator(progress = { (watchTime / 1000f).coerceIn(0f, 1f) }, modifier = Modifier.fillMaxWidth())
                
                Spacer(modifier = Modifier.height(32.dp))
                
                if (followers >= 5000 && watchTime >= 1000) {
                    Text("Congratulations! You are eligible to apply for monetization.", color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { applicationStep = 1 }, modifier = Modifier.fillMaxWidth()) {
                        Text("Apply for Monetization")
                    }
                } else {
                    Text("Keep growing — you are not eligible yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else if (applicationStep == 1) {
                Text("Payment & Tax Information", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Tax and payment requirements may vary. Please provide accurate information.", style = MaterialTheme.typography.bodySmall)
                Spacer(modifier = Modifier.height(16.dp))
                
                var legalName by remember { mutableStateOf("") }
                var panNumber by remember { mutableStateOf("") }
                var bankName by remember { mutableStateOf("") }
                var accountNumber by remember { mutableStateOf("") }
                var ifscCode by remember { mutableStateOf("") }
                
                OutlinedTextField(value = legalName, onValueChange = { legalName = it }, label = { Text("Legal Name") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = panNumber, onValueChange = { panNumber = it }, label = { Text("PAN Number") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = bankName, onValueChange = { bankName = it }, label = { Text("Bank Name") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = accountNumber, onValueChange = { accountNumber = it }, label = { Text("Account Number") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = ifscCode, onValueChange = { ifscCode = it }, label = { Text("IFSC Code") }, modifier = Modifier.fillMaxWidth())
                
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = { applicationStep = 2 },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = legalName.isNotBlank() && panNumber.isNotBlank() && accountNumber.isNotBlank()
                ) {
                    Text("Submit Application")
                }
            } else if (applicationStep == 2) {
                Text("Application Submitted", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(16.dp))
                Text("Your application is currently Under Review. We will notify you once approved.")
                Spacer(modifier = Modifier.height(24.dp))
                Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
                    Text("Back to Dashboard")
                }
            }
        }
    }
}
