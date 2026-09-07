package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.VideoCameraBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateScreen(onCreationDone: () -> Unit) {
    var text by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf("Post") } // Post, Reel, Story

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create $selectedType") },
                navigationIcon = {
                    IconButton(onClick = onCreationDone) {
                        Icon(Icons.Filled.Close, contentDescription = "Close")
                    }
                },
                actions = {
                    TextButton(onClick = onCreationDone) {
                        Text("Share", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding).padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                FilterChip(selected = selectedType == "Post", onClick = { selectedType = "Post" }, label = { Text("Post") })
                FilterChip(selected = selectedType == "Reel", onClick = { selectedType = "Reel" }, label = { Text("Reel") })
                FilterChip(selected = selectedType == "Story", onClick = { selectedType = "Story" }, label = { Text("Story") })
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                placeholder = { Text("What's on your mind?") },
                modifier = Modifier.fillMaxWidth().weight(1f),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Mock Media Picker
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(if (selectedType == "Reel") Icons.Filled.VideoCameraBack else Icons.Filled.Image, contentDescription = "Add Media")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Tap to select media")
                }
            }
        }
    }
}
