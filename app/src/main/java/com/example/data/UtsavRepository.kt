package com.example.data

import com.example.data.dao.UtsavDao
import com.example.data.entity.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class UtsavRepository(private val dao: UtsavDao) {
    private val isFirebase get() = AuthManager.isFirebaseConfigured
    
    private val firestore by lazy { com.google.firebase.firestore.FirebaseFirestore.getInstance() }
    
    // Users
    fun getUserFlow(userId: String): Flow<UserEntity?> = if (isFirebase) {
        callbackFlow {
            val listener = firestore.collection("users").document(userId)
                .addSnapshotListener { snapshot, _ ->
                    val user = snapshot?.toObject(UserEntity::class.java)
                    trySend(user)
                }
            awaitClose { listener.remove() }
        }
    } else dao.getUserFlow(userId)

    fun getAllUsersFlow(): Flow<List<UserEntity>> = if (isFirebase) {
        callbackFlow {
            val listener = firestore.collection("users")
                .addSnapshotListener { snapshot, _ ->
                    val list = snapshot?.toObjects(UserEntity::class.java) ?: emptyList()
                    trySend(list)
                }
            awaitClose { listener.remove() }
        }
    } else dao.getAllUsersFlow()

    suspend fun getUser(userId: String): UserEntity? = if (isFirebase) {
        firestore.collection("users").document(userId).get().await().toObject(UserEntity::class.java)
    } else dao.getUser(userId)

    suspend fun getUserByUsername(username: String): UserEntity? = if (isFirebase) {
        firestore.collection("users").whereEqualTo("username", username).limit(1).get().await()
            .documents.firstOrNull()?.toObject(UserEntity::class.java)
    } else dao.getUserByUsername(username)

    suspend fun insertUser(user: UserEntity) = if (isFirebase) {
        firestore.collection("users").document(user.id).set(user).await()
        Unit
    } else dao.insertUser(user)

    suspend fun updateUser(user: UserEntity) = if (isFirebase) {
        firestore.collection("users").document(user.id).set(user).await()
        Unit
    } else dao.updateUser(user)

    // Posts
    fun getAllPostsFlow(): Flow<List<PostEntity>> = if (isFirebase) {
        callbackFlow {
            val listener = firestore.collection("posts")
                .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, _ ->
                    val list = snapshot?.toObjects(PostEntity::class.java) ?: emptyList()
                    trySend(list)
                }
            awaitClose { listener.remove() }
        }
    } else dao.getAllPostsFlow()

    fun getPostsByUserFlow(userId: String): Flow<List<PostEntity>> = if (isFirebase) {
        callbackFlow {
            val listener = firestore.collection("posts")
                .whereEqualTo("userId", userId)
                .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, _ ->
                    val list = snapshot?.toObjects(PostEntity::class.java) ?: emptyList()
                    trySend(list)
                }
            awaitClose { listener.remove() }
        }
    } else dao.getPostsByUserFlow(userId)

    suspend fun insertPost(post: PostEntity) = if (isFirebase) {
        firestore.collection("posts").document(post.id).set(post).await()
        Unit
    } else dao.insertPost(post)

    suspend fun deletePost(postId: String) = if (isFirebase) {
        firestore.collection("posts").document(postId).delete().await()
        Unit
    } else dao.deletePost(postId)

    // Reels
    fun getAllReelsFlow(): Flow<List<ReelEntity>> = if (isFirebase) {
        callbackFlow {
            val listener = firestore.collection("reels")
                .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, _ ->
                    val list = snapshot?.toObjects(ReelEntity::class.java) ?: emptyList()
                    trySend(list)
                }
            awaitClose { listener.remove() }
        }
    } else dao.getAllReelsFlow()

    fun getReelsByUserFlow(userId: String): Flow<List<ReelEntity>> = if (isFirebase) {
        callbackFlow {
            val listener = firestore.collection("reels")
                .whereEqualTo("userId", userId)
                .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, _ ->
                    val list = snapshot?.toObjects(ReelEntity::class.java) ?: emptyList()
                    trySend(list)
                }
            awaitClose { listener.remove() }
        }
    } else dao.getReelsByUserFlow(userId)

    suspend fun insertReel(reel: ReelEntity) = if (isFirebase) {
        firestore.collection("reels").document(reel.id).set(reel).await()
        Unit
    } else dao.insertReel(reel)

    // Stories
    fun getActiveStoriesFlow(): Flow<List<StoryEntity>> = if (isFirebase) {
        callbackFlow {
            val listener = firestore.collection("stories")
                .whereGreaterThan("expiresAt", System.currentTimeMillis())
                .orderBy("expiresAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, _ ->
                    val list = snapshot?.toObjects(StoryEntity::class.java) ?: emptyList()
                    trySend(list)
                }
            awaitClose { listener.remove() }
        }
    } else dao.getActiveStoriesFlow(System.currentTimeMillis())

    suspend fun insertStory(story: StoryEntity) = if (isFirebase) {
        firestore.collection("stories").document(story.id).set(story).await()
        Unit
    } else dao.insertStory(story)

    // Messages
    fun getChatMessagesFlow(user1: String, user2: String): Flow<List<MessageEntity>> = if (isFirebase) {
        callbackFlow {
            // Firestore doesn't easily support OR queries like Room natively for real-time without composite index.
            // For simplicity in UI, we fetch where sender is user1 or user2, but usually we use a conversationId.
            // As a quick fallback, we fetch all where sender is in [user1, user2] and filter client-side.
            val listener = firestore.collection("messages")
                .whereIn("senderId", listOf(user1, user2))
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.ASCENDING)
                .addSnapshotListener { snapshot, _ ->
                    val list = snapshot?.toObjects(MessageEntity::class.java)?.filter {
                        it.receiverId == user1 || it.receiverId == user2
                    } ?: emptyList()
                    trySend(list)
                }
            awaitClose { listener.remove() }
        }
    } else dao.getChatMessagesFlow(user1, user2)

    suspend fun insertMessage(message: MessageEntity) = if (isFirebase) {
        firestore.collection("messages").document(message.id).set(message).await()
        Unit
    } else dao.insertMessage(message)

    // Notifications
    fun getNotificationsFlow(userId: String): Flow<List<NotificationEntity>> = if (isFirebase) {
        callbackFlow {
            val listener = firestore.collection("notifications")
                .whereEqualTo("userId", userId)
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, _ ->
                    val list = snapshot?.toObjects(NotificationEntity::class.java) ?: emptyList()
                    trySend(list)
                }
            awaitClose { listener.remove() }
        }
    } else dao.getNotificationsFlow(userId)

    suspend fun insertNotification(notification: NotificationEntity) = if (isFirebase) {
        firestore.collection("notifications").document(notification.id).set(notification).await()
        Unit
    } else dao.insertNotification(notification)

    suspend fun markAllNotificationsAsRead(userId: String) = if (isFirebase) {
        val batch = firestore.batch()
        val notifs = firestore.collection("notifications").whereEqualTo("userId", userId).get().await()
        notifs.documents.forEach { doc ->
            batch.update(doc.reference, "isRead", true)
        }
        batch.commit().await()
        Unit
    } else dao.markAllNotificationsAsRead(userId)

    // Monetization
    fun getMonetizationAppFlow(userId: String): Flow<MonetizationAppEntity?> = if (isFirebase) {
        callbackFlow {
            val listener = firestore.collection("monetization_applications")
                .whereEqualTo("userId", userId)
                .orderBy("appliedAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .limit(1)
                .addSnapshotListener { snapshot, _ ->
                    val app = snapshot?.documents?.firstOrNull()?.toObject(MonetizationAppEntity::class.java)
                    trySend(app)
                }
            awaitClose { listener.remove() }
        }
    } else dao.getMonetizationAppFlow(userId)

    suspend fun insertMonetizationApp(app: MonetizationAppEntity) = if (isFirebase) {
        firestore.collection("monetization_applications").document(app.id).set(app).await()
        Unit
    } else dao.insertMonetizationApp(app)

    // Payment Info
    fun getPaymentInfoFlow(userId: String): Flow<PaymentInfoEntity?> = if (isFirebase) {
        callbackFlow {
            val listener = firestore.collection("payment_info")
                .whereEqualTo("userId", userId)
                .limit(1)
                .addSnapshotListener { snapshot, _ ->
                    val app = snapshot?.documents?.firstOrNull()?.toObject(PaymentInfoEntity::class.java)
                    trySend(app)
                }
            awaitClose { listener.remove() }
        }
    } else dao.getPaymentInfoFlow(userId)

    suspend fun insertPaymentInfo(paymentInfo: PaymentInfoEntity) = if (isFirebase) {
        firestore.collection("payment_info").document(paymentInfo.id).set(paymentInfo).await()
        Unit
    } else dao.insertPaymentInfo(paymentInfo)
}
