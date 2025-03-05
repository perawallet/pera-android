package com.algorand.wallet.account.info.domain.repository

import com.algorand.wallet.account.info.domain.model.EntropyInformation
import kotlinx.coroutines.flow.Flow

internal interface EntropyInformationRepository {

    fun getAllAsFlow(): Flow<List<EntropyInformation>>

    fun getEntropyInformationCountAsFlow(): Flow<Int>

    suspend fun getAllEntropyInformation(): List<EntropyInformation>

    suspend fun getEntropyInformation(seedId: Int): EntropyInformation?

    suspend fun updateEntropyCustomName(seedId: Int, entropyCustomName: String)

    suspend fun addEntropyInformation(entropyInformation: EntropyInformation)

    suspend fun deleteEntropyInformation(seedId: Int)

    suspend fun deleteAllEntropyInformation()
}
