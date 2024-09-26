package com.algorand.android.module.account.local.data.repository

import com.algorand.android.module.account.local.data.database.dao.Algo25Dao
import com.algorand.android.module.account.local.data.mapper.entity.Algo25EntityMapper
import com.algorand.android.module.account.local.data.mapper.model.algo25.Algo25Mapper
import com.algorand.android.module.account.local.domain.model.LocalAccount.Algo25
import com.algorand.android.module.account.local.domain.repository.Algo25AccountRepository
import com.algorand.android.encryption.EncryptionManager
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

internal class Algo25AccountRepositoryImpl(
    private val algo25Dao: Algo25Dao,
    private val algo25EntityMapper: Algo25EntityMapper,
    private val algo25Mapper: Algo25Mapper,
    private val encryptionManager: EncryptionManager,
    private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO
) : Algo25AccountRepository {

    override fun getAllAsFlow(): Flow<List<Algo25>> {
        return algo25Dao.getAllAsFlow().map { entityList ->
            entityList.map { entity -> algo25Mapper(entity) }
        }
    }

    override fun getAccountCountAsFlow(): Flow<Int> {
        return algo25Dao.getTableSizeAsFlow()
    }

    override suspend fun getAll(): List<Algo25> {
        return withContext(coroutineDispatcher) {
            val algo25Entities = algo25Dao.getAll()
            algo25Entities.map { algo25Mapper(it) }
        }
    }

    override suspend fun getAccount(address: String): Algo25? {
        return withContext(coroutineDispatcher) {
            algo25Dao.get(encryptionManager.encrypt(address))?.let { algo25Mapper(it) }
        }
    }

    override suspend fun addAccount(account: Algo25) {
        withContext(coroutineDispatcher) {
            val algo25Entity = algo25EntityMapper(account)
            algo25Dao.insert(algo25Entity)
        }
    }

    override suspend fun deleteAccount(address: String) {
        withContext(coroutineDispatcher) {
            val encryptedAddress = encryptionManager.encrypt(address)
            algo25Dao.delete(encryptedAddress)
        }
    }

    override suspend fun deleteAllAccounts() {
        withContext(coroutineDispatcher) {
            algo25Dao.clearAll()
        }
    }
}
