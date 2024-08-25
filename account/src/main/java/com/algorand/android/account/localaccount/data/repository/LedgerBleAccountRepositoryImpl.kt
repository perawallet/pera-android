package com.algorand.android.account.localaccount.data.repository

import com.algorand.android.account.localaccount.data.database.dao.LedgerBleDao
import com.algorand.android.account.localaccount.data.mapper.entity.LedgerBleEntityMapper
import com.algorand.android.account.localaccount.data.mapper.model.ledgerble.LedgerBleMapper
import com.algorand.android.account.localaccount.domain.model.LocalAccount.LedgerBle
import com.algorand.android.account.localaccount.domain.repository.LedgerBleAccountRepository
import com.algorand.android.encryption.EncryptionManager
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

internal class LedgerBleAccountRepositoryImpl(
    private val ledgerBleDao: LedgerBleDao,
    private val ledgerBleEntityMapper: LedgerBleEntityMapper,
    private val ledgerBleMapper: LedgerBleMapper,
    private val encryptionManager: EncryptionManager,
    private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO
) : LedgerBleAccountRepository {

    override fun getAllAsFlow(): Flow<List<LedgerBle>> {
        return ledgerBleDao.getAllAsFlow().map { entityList ->
            entityList.map { entity -> ledgerBleMapper(entity) }
        }
    }

    override fun getAccountCountAsFlow(): Flow<Int> {
        return ledgerBleDao.getTableSizeAsFlow()
    }

    override suspend fun getAll(): List<LedgerBle> {
        return withContext(coroutineDispatcher) {
            val ledgerBleEntities = ledgerBleDao.getAll()
            ledgerBleEntities.map { ledgerBleMapper(it) }
        }
    }

    override suspend fun getAccount(address: String): LedgerBle? {
        return withContext(coroutineDispatcher) {
            val ledgerBleEntity = ledgerBleDao.get(encryptionManager.encrypt(address))
            ledgerBleEntity?.let { ledgerBleMapper(it) }
        }
    }

    override suspend fun addAccount(account: LedgerBle) {
        withContext(coroutineDispatcher) {
            val ledgerBleEntity = ledgerBleEntityMapper(account)
            ledgerBleDao.insert(ledgerBleEntity)
        }
    }

    override suspend fun deleteAccount(address: String) {
        withContext(coroutineDispatcher) {
            val encryptedAddress = encryptionManager.encrypt(address)
            ledgerBleDao.delete(encryptedAddress)
        }
    }

    override suspend fun deleteAllAccounts() {
        withContext(coroutineDispatcher) {
            ledgerBleDao.clearAll()
        }
    }
}
