package com.algorand.android.module.custominfo.data.repository

import com.algorand.android.module.custominfo.data.mapper.entity.CustomInfoEntityMapper
import com.algorand.android.module.custominfo.data.mapper.model.CustomInfoMapper
import com.algorand.android.module.custominfo.domain.model.CustomInfo
import com.algorand.android.module.custominfo.domain.repository.CustomInfoRepository
import com.algorand.android.module.encryption.EncryptionManager
import com.algorand.android.module.shareddb.custominfo.dao.CustomInfoDao
import kotlinx.coroutines.*

internal class CustomInfoRepositoryImpl(
    private val customInfoDao: CustomInfoDao,
    private val customInfoMapper: CustomInfoMapper,
    private val customInfoEntityMapper: CustomInfoEntityMapper,
    private val encryptionManager: EncryptionManager,
    private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO
) : CustomInfoRepository {

    override suspend fun getCustomInfo(address: String): CustomInfo {
        return withContext(coroutineDispatcher) {
            val encryptedAddress = encryptionManager.encrypt(address)
            val customInfoEntity = customInfoDao.getOrNull(encryptedAddress)
            customInfoMapper(address, customInfoEntity)
        }
    }

    override suspend fun getCustomInfoOrNull(address: String): CustomInfo? {
        return withContext(coroutineDispatcher) {
            val encryptedAddress = encryptionManager.encrypt(address)
            val customInfoEntity = customInfoDao.getOrNull(encryptedAddress) ?: return@withContext null
            customInfoMapper(address, customInfoEntity)
        }
    }

    override suspend fun setCustomInfo(customInfo: CustomInfo) {
        withContext(coroutineDispatcher) {
            val entity = customInfoEntityMapper(customInfo)
            customInfoDao.insert(entity)
        }
    }

    override suspend fun setCustomName(address: String, name: String) {
        withContext(coroutineDispatcher) {
            val encryptedAddress = encryptionManager.encrypt(address)
            customInfoDao.updateCustomName(encryptedAddress, name)
        }
    }

    override suspend fun deleteCustomInfo(address: String) {
        withContext(coroutineDispatcher) {
            val encryptedAddress = encryptionManager.encrypt(address)
            customInfoDao.delete(encryptedAddress)
        }
    }
}
