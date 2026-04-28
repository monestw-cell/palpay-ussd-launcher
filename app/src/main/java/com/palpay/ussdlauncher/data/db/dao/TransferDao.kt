package com.palpay.ussdlauncher.data.db.dao

import androidx.room.*
import com.palpay.ussdlauncher.data.db.entity.Transfer
import kotlinx.coroutines.flow.Flow

@Dao
interface TransferDao {
    @Query("SELECT * FROM transfers ORDER BY timestamp DESC")
    fun getAllTransfers(): Flow<List<Transfer>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransfer(transfer: Transfer): Long

    @Update
    suspend fun updateTransfer(transfer: Transfer)

    @Query("UPDATE transfers SET status = :status WHERE id = :id")
    suspend fun updateTransferStatus(id: Long, status: String)

    @Delete
    suspend fun deleteTransfer(transfer: Transfer)

    @Query("DELETE FROM transfers")
    suspend fun clearAllTransfers()
}
