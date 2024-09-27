package com.algorand.android.module.account.local.data.repository

import com.algorand.android.module.account.local.data.database.dao.LedgerBleDao
import com.algorand.android.module.account.local.data.mapper.entity.LedgerBleEntityMapper
import com.algorand.android.module.account.local.data.mapper.model.ledgerble.LedgerBleMapper
import com.algorand.android.module.account.local.domain.model.LocalAccount.LedgerBle
import com.algorand.android.module.account.local.domain.repository.LedgerBleAccountRepository
import com.algorand.android.module.encryption.EncryptionManager
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

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
