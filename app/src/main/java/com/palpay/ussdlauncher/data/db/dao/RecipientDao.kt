package com.palpay.ussdlauncher.data.db.dao

import androidx.room.*
import com.palpay.ussdlauncher.data.db.entity.Recipient
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipientDao {
    @Query("SELECT * FROM recipients ORDER BY name ASC")
    fun getAllRecipients(): Flow<List<Recipient>>

    @Query("SELECT * FROM recipients WHERE id = :id")
    suspend fun getRecipientById(id: Long): Recipient?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipient(recipient: Recipient): Long

    @Update
    suspend fun updateRecipient(recipient: Recipient)

    @Delete
    suspend fun deleteRecipient(recipient: Recipient)

    @Query("DELETE FROM recipients WHERE id = :id")
    suspend fun deleteRecipientById(id: Long)
}
