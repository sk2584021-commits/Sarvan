package com.example.data

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object AuthManager {
    private val _currentUserId = MutableStateFlow<String?>(null)
    val currentUserId: StateFlow<String?> = _currentUserId.asStateFlow()

    var isFirebaseConfigured: Boolean = false
        private set

    fun initialize(context: Context) {
        isFirebaseConfigured = com.google.firebase.FirebaseApp.getApps(context).isNotEmpty()
        if (isFirebaseConfigured) {
            val auth = com.google.firebase.auth.FirebaseAuth.getInstance()
            _currentUserId.value = auth.currentUser?.uid
            auth.addAuthStateListener { firebaseAuth ->
                _currentUserId.value = firebaseAuth.currentUser?.uid
            }
        }
    }

    fun login(userId: String) {
        if (!isFirebaseConfigured) {
            _currentUserId.value = userId
        }
        // If Firebase is configured, the auth state listener will update the flow
    }

    fun logout() {
        if (isFirebaseConfigured) {
            com.google.firebase.auth.FirebaseAuth.getInstance().signOut()
        } else {
            _currentUserId.value = null
        }
    }
}
