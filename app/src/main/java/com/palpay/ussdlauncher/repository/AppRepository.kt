package com.palpay.ussdlauncher.repository

import com.palpay.ussdlauncher.data.db.AppDatabase
import com.palpay.ussdlauncher.data.db.entity.Recipient
import com.palpay.ussdlauncher.data.db.entity.Transfer
import kotlinx.coroutines.flow.Flow

class AppRepository(db: AppDatabase) {

    private val recipientDao = db.recipientDao()
    private val transferDao = db.transferDao()

    val allRecipients: Flow<List<Recipient>> = recipientDao.getAllRecipients()
    val allTransfers: Flow<List<Transfer>> = transferDao.getAllTransfers()

    suspend fun addRecipient(name: String, phone: String): Long {
        return recipientDao.insertRecipient(Recipient(name = name, phone = phone))
    }

    suspend fun updateRecipient(recipient: Recipient) {
        recipientDao.updateRecipient(recipient)
    }

    suspend fun deleteRecipient(recipient: Recipient) {
        recipientDao.deleteRecipient(recipient)
    }

    suspend fun deleteRecipientById(id: Long) {
        recipientDao.deleteRecipientById(id)
    }

    suspend fun addTransfer(
        recipientName: String,
        phone: String,
        amount: String,
        serviceKey: String
    ): Long {
        return transferDao.insertTransfer(
            Transfer(
                recipientName = recipientName,
                phone = phone,
                amount = amount,
                serviceKey = serviceKey
            )
        )
    }

    suspend fun updateTransferStatus(id: Long, status: String) {
        transferDao.updateTransferStatus(id, status)
    }

    suspend fun deleteTransfer(transfer: Transfer) {
        transferDao.deleteTransfer(transfer)
    }

    suspend fun clearAllTransfers() {
        transferDao.clearAllTransfers()
    }
}
