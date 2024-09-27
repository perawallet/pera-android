package com.algorand.android.module.account.local.data.repository

import com.algorand.android.module.account.local.data.database.dao.LedgerUsbDao
import com.algorand.android.module.account.local.data.mapper.entity.LedgerUsbEntityMapper
import com.algorand.android.module.account.local.data.mapper.model.ledgerusb.LedgerUsbMapper
import com.algorand.android.module.account.local.domain.model.LocalAccount.LedgerUsb
import com.algorand.android.module.account.local.domain.repository.LedgerUsbAccountRepository
import com.algorand.android.module.encryption.EncryptionManager
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

internal class LedgerUsbAccountRepositoryImpl(
    private val ledgerUsbDao: LedgerUsbDao,
    private val ledgerUsbEntityMapper: LedgerUsbEntityMapper,
    private val ledgerUsbMapper: LedgerUsbMapper,
    private val encryptionManager: EncryptionManager,
    private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO
) : LedgerUsbAccountRepository {

    override fun getAllAsFlow(): Flow<List<LedgerUsb>> {
        return ledgerUsbDao.getAllAsFlow().map { entityList ->
            entityList.map { entity -> ledgerUsbMapper(entity) }
        }
    }

    override fun getAccountCountAsFlow(): Flow<Int> {
        return ledgerUsbDao.getTableSizeAsFlow()
    }

    override suspend fun getAll(): List<LedgerUsb> {
        return withContext(coroutineDispatcher) {
            val ledgerUsbEntities = ledgerUsbDao.getAll()
            ledgerUsbEntities.map { ledgerUsbMapper(it) }
        }
    }

    override suspend fun getAccount(address: String): LedgerUsb? {
        return withContext(coroutineDispatcher) {
            val ledgerUsbEntity = ledgerUsbDao.get(encryptionManager.encrypt(address))
            ledgerUsbEntity?.let { ledgerUsbMapper(it) }
        }
    }

    override suspend fun addAccount(account: LedgerUsb) {
        withContext(coroutineDispatcher) {
            val ledgerUsbEntity = ledgerUsbEntityMapper(account)
            ledgerUsbDao.insert(ledgerUsbEntity)
        }
    }

    override suspend fun deleteAccount(address: String) {
        withContext(coroutineDispatcher) {
            val encryptedAddress = encryptionManager.encrypt(address)
            ledgerUsbDao.delete(encryptedAddress)
        }
    }

    override suspend fun deleteAllAccounts() {
        withContext(coroutineDispatcher) {
            ledgerUsbDao.clearAll()
        }
    }
}
