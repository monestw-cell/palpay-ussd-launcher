package com.palpay.ussdlauncher.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.palpay.ussdlauncher.data.db.dao.RecipientDao
import com.palpay.ussdlauncher.data.db.dao.TransferDao
import com.palpay.ussdlauncher.data.db.entity.Recipient
import com.palpay.ussdlauncher.data.db.entity.Transfer

@Database(
    entities = [Recipient::class, Transfer::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun recipientDao(): RecipientDao
    abstract fun transferDao(): TransferDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "ussd_launcher_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
