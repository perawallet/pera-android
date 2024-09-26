package com.algorand.android.module.asb.data.repository

import com.algorand.android.encryption.EncryptionManager
import com.algorand.android.module.asb.data.mapper.AlgorandSecureBackUpEntityMapper
import com.algorand.android.module.asb.domain.repository.AsbRepository
import com.algorand.android.shared_db.asb.dao.AlgorandSecureBackUpDao
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class AsbRepositoryImpl @Inject constructor(
    private val encryptionManager: EncryptionManager,
    private val algorandSecureBackUpDao: AlgorandSecureBackUpDao,
    private val algorandSecureBackUpEntityMapper: AlgorandSecureBackUpEntityMapper
) : AsbRepository {

    override fun getBackedUpAccountAddressesFlow(): Flow<Set<String>> {
        return algorandSecureBackUpDao.getAllAsFlow().map { entityList ->
            entityList.mapNotNull { entity ->
                if (!entity.isBackedUp) return@mapNotNull null
                encryptionManager.decrypt(entity.encryptedAddress)
            }.toSet()
        }
    }

    override suspend fun setBackedUpBulk(backedUpAccounts: Set<String>) {
        val entities = backedUpAccounts.map { address ->
            val encryptedAddress = encryptionManager.encrypt(address)
            algorandSecureBackUpEntityMapper(encryptedAddress, true)
        }
        algorandSecureBackUpDao.insertAll(entities)
    }

    override suspend fun getBackedUpAccounts(): Set<String> {
        return algorandSecureBackUpDao.getAll().mapNotNull { entity ->
            if (!entity.isBackedUp) return@mapNotNull null
            encryptionManager.decrypt(entity.encryptedAddress)
        }.toSet()
    }

    override suspend fun removeAccount(accountAddress: String) {
        val encryptedAddress = encryptionManager.encrypt(accountAddress)
        algorandSecureBackUpDao.delete(encryptedAddress)
    }

    override suspend fun setBackedUp(accountAddress: String, isBackedUp: Boolean) {
        val encryptedAddress = encryptionManager.encrypt(accountAddress)
        val entity = algorandSecureBackUpEntityMapper(encryptedAddress, isBackedUp)
        algorandSecureBackUpDao.insert(entity)
    }

    override suspend fun getBackedUpStatus(accountAddress: String): Boolean {
        val encryptedAddress = encryptionManager.encrypt(accountAddress)
        return algorandSecureBackUpDao.get(encryptedAddress)?.isBackedUp ?: false
    }
}