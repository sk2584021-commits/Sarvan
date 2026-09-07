package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.example.data.UtsavDatabase
import com.example.data.UtsavRepository
import com.example.data.entity.UserEntity
import com.example.data.AuthManager
import com.example.ui.AppNavigation
import com.example.ui.theme.MyApplicationTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    
    lateinit var repository: UtsavRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Initialize DB and Repository (Simple Service Locator pattern for this prototype)
        AuthManager.initialize(this)
        val database = UtsavDatabase.getDatabase(this)
        repository = UtsavRepository(database.utsavDao())
        
        // Optional: Pre-populate some dummy data if needed
        lifecycleScope.launch {
            if (repository.getUser("dummy_admin") == null) {
                repository.insertUser(
                    UserEntity(
                        id = "dummy_admin",
                        username = "admin",
                        displayName = "Utsav Admin",
                        profilePicUrl = null,
                        bio = "Welcome to Utsav!",
                        isAdmin = true
                    )
                )
                repository.insertPost(
                    com.example.data.entity.PostEntity(
                        id = "dummy_post_1",
                        userId = "dummy_admin",
                        content = "Welcome to the new Utsav! 🇮🇳 #launch",
                        type = "IMAGE",
                        mediaUrl = "https://picsum.photos/seed/post1/800/600",
                        createdAt = System.currentTimeMillis()
                    )
                )
            }
        }

        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}
