package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.dao.UtsavDao
import com.example.data.entity.*

@Database(
    entities = [
        UserEntity::class,
        PostEntity::class,
        ReelEntity::class,
        StoryEntity::class,
        MessageEntity::class,
        NotificationEntity::class,
        MonetizationAppEntity::class,
        PaymentInfoEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class UtsavDatabase : RoomDatabase() {
    abstract fun utsavDao(): UtsavDao

    companion object {
        @Volatile
        private var INSTANCE: UtsavDatabase? = null

        fun getDatabase(context: Context): UtsavDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    UtsavDatabase::class.java,
                    "utsav_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
