/*
 * Copyright 2022-2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.wallet.account.local.data.repository

import com.algorand.wallet.account.local.data.database.dao.JointDao
import com.algorand.wallet.account.local.data.database.dao.JointParticipantDao
import com.algorand.wallet.account.local.data.mapper.entity.JointEntityMapper
import com.algorand.wallet.account.local.data.mapper.model.JointMapper
import com.algorand.wallet.account.local.domain.model.LocalAccount
import com.algorand.wallet.account.local.domain.repository.JointAccountPersistence
import com.algorand.wallet.account.local.domain.repository.JointAccountRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class JointAccountRepositoryImpl @Inject constructor(
    private val jointDao: JointDao,
    private val jointParticipantDao: JointParticipantDao,
    private val jointEntityMapper: JointEntityMapper,
    private val jointMapper: JointMapper,
    private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO
) : JointAccountRepository, JointAccountPersistence {

    override fun getAllAsFlow(): Flow<List<LocalAccount.Joint>> {
        return jointDao.getAllWithParticipantsAsFlow().map { entityList ->
            entityList.map { entity -> jointMapper(entity) }
        }
    }

    override fun getAccountCountAsFlow(): Flow<Int> {
        return jointDao.getTableSizeAsFlow()
    }

    override suspend fun getAccountCount(): Int {
        return jointDao.getTableSize()
    }

    override suspend fun getAll(): List<LocalAccount.Joint> {
        return withContext(coroutineDispatcher) {
            val jointEntities = jointDao.getAllWithParticipants()
            jointEntities.map { jointMapper(it) }
        }
    }

    override suspend fun getAllAddresses(): List<String> {
        return withContext(coroutineDispatcher) {
            jointDao.getAllAddresses()
        }
    }

    override suspend fun getAccount(address: String): LocalAccount.Joint? {
        return withContext(coroutineDispatcher) {
            val jointWithParticipants = jointDao.getWithParticipants(address)
            jointWithParticipants?.let { jointMapper(it) }
        }
    }

    override suspend fun addAccount(account: LocalAccount.Joint) {
        withContext(coroutineDispatcher) {
            val mapperResult = jointEntityMapper(account)
            jointDao.insert(mapperResult.jointEntity)
            jointParticipantDao.insertAll(mapperResult.participantEntities)
        }
    }

    override suspend fun deleteAccount(address: String) {
        withContext(coroutineDispatcher) {
            // Participants will be deleted automatically due to CASCADE
            jointDao.delete(address)
        }
    }

    override suspend fun isAddressExists(address: String): Boolean {
        return withContext(coroutineDispatcher) {
            jointDao.isAddressExists(address)
        }
    }

    override suspend fun deleteAllAccounts() {
        withContext(coroutineDispatcher) {
            // Participants will be deleted automatically due to CASCADE
            jointDao.clearAll()
        }
    }

    override suspend fun getParticipantCount(jointAddress: String): Int {
        return withContext(coroutineDispatcher) {
            jointParticipantDao.getParticipantCount(jointAddress)
        }
    }

    override suspend fun getParticipantAddresses(jointAddress: String): List<String> {
        return withContext(coroutineDispatcher) {
            // Returns addresses ordered by participant_index
            jointParticipantDao.getParticipantAddresses(jointAddress)
        }
    }

    override suspend fun getJointAddressesByParticipant(participantAddress: String): List<String> {
        return withContext(coroutineDispatcher) {
            jointParticipantDao.getJointAddressesByParticipant(participantAddress)
        }
    }

    override suspend fun isParticipant(jointAddress: String, participantAddress: String): Boolean {
        return withContext(coroutineDispatcher) {
            jointParticipantDao.isParticipant(jointAddress, participantAddress)
        }
    }
}
