package com.algorand.wallet.account.info.data.repository

import com.algorand.wallet.account.info.data.database.dao.EntropyInformationDao
import com.algorand.wallet.account.info.data.mapper.entity.EntropyInformationEntityMapper
import com.algorand.wallet.account.info.data.mapper.model.EntropyInformationMapper
import com.algorand.wallet.account.info.domain.model.EntropyInformation
import com.algorand.wallet.account.info.domain.repository.EntropyInformationRepository
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

internal class EntropyInformationRepositoryImpl @Inject constructor(
    private val entropyInformationDao: EntropyInformationDao,
    private val entropyInformationMapper: EntropyInformationMapper,
    private val entropyInformationEntityMapper: EntropyInformationEntityMapper,
    private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO
): EntropyInformationRepository {
    override fun getAllAsFlow(): Flow<List<EntropyInformation>> {
        return entropyInformationDao.getAllAsFlow().map { entityList ->
            entityList.map { entity -> entropyInformationMapper(entity) }
        }
    }

    override fun getEntropyInformationCountAsFlow(): Flow<Int> {
        return entropyInformationDao.getTableSizeAsFlow()
    }

    override suspend fun getAllEntropyInformation(): List<EntropyInformation> {
        return withContext(coroutineDispatcher) {
            val entities = entropyInformationDao.getAll()
            entities.map { entropyInformationMapper(it) }
        }
    }

    override suspend fun getEntropyInformation(seedId: Int): EntropyInformation? {
        return withContext(coroutineDispatcher) {
            entropyInformationDao.get(seedId)?.let { entropyInformationMapper(it) }
        }
    }

    override suspend fun updateEntropyCustomName(seedId: Int, entropyCustomName: String) {
        entropyInformationDao.update(seedId, entropyCustomName)
    }

    override suspend fun addEntropyInformation(entropyInformation: EntropyInformation) {
        withContext(coroutineDispatcher) {
            val entity = entropyInformationEntityMapper(entropyInformation)
            entropyInformationDao.insert(entity)
        }
    }

    override suspend fun deleteEntropyInformation(seedId: Int) {
        withContext(coroutineDispatcher) {
            entropyInformationDao.delete(seedId)
        }
    }

    override suspend fun deleteAllEntropyInformation() {
        withContext(coroutineDispatcher) {
            entropyInformationDao.clearAll()
        }
    }

}
