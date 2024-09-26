package com.algorand.android.module.account.local.data.repository

import com.algorand.android.module.account.local.data.database.dao.NoAuthDao
import com.algorand.android.module.account.local.data.mapper.entity.NoAuthEntityMapper
import com.algorand.android.module.account.local.data.mapper.model.noauth.NoAuthMapper
import com.algorand.android.module.account.local.domain.model.LocalAccount.NoAuth
import com.algorand.android.module.account.local.domain.repository.NoAuthAccountRepository
import com.algorand.android.module.encryption.EncryptionManager
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

internal class NoAuthAccountRepositoryImpl(
    private val noAuthDao: NoAuthDao,
    private val noAuthEntityMapper: NoAuthEntityMapper,
    private val noAuthMapper: NoAuthMapper,
    private val encryptionManager: EncryptionManager,
    private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO
) : NoAuthAccountRepository {

    override fun getAllAsFlow(): Flow<List<NoAuth>> {
        return noAuthDao.getAllAsFlow().map { entityList ->
            entityList.map { entity -> noAuthMapper(entity) }
        }
    }

    override fun getAccountCountAsFlow(): Flow<Int> {
        return noAuthDao.getTableSizeAsFlow()
    }

    override suspend fun getAll(): List<NoAuth> {
        return withContext(coroutineDispatcher) {
            val noAuthEntities = noAuthDao.getAll()
            noAuthEntities.map { noAuthMapper(it) }
        }
    }

    override suspend fun getAccount(address: String): NoAuth? {
        return withContext(coroutineDispatcher) {
            val noAuthEntity = noAuthDao.get(encryptionManager.encrypt(address))
            noAuthEntity?.let { noAuthMapper(it) }
        }
    }

    override suspend fun addAccount(account: NoAuth) {
        withContext(coroutineDispatcher) {
            val noAuthEntity = noAuthEntityMapper(account)
            noAuthDao.insert(noAuthEntity)
        }
    }

    override suspend fun deleteAccount(address: String) {
        withContext(coroutineDispatcher) {
            val encryptedAddress = encryptionManager.encrypt(address)
            noAuthDao.delete(encryptedAddress)
        }
    }

    override suspend fun deleteAllAccounts() {
        withContext(coroutineDispatcher) {
            noAuthDao.clearAll()
        }
    }
}
