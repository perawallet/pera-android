/*
 * Copyright 2022 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.wallet.account.info.data.repository

import com.algorand.wallet.account.info.data.cache.AccountInformationErrorCache
import com.algorand.wallet.account.info.data.database.dao.AccountInformationDao
import com.algorand.wallet.account.info.data.database.dao.AssetHoldingDao
import com.algorand.wallet.account.info.data.mapper.model.AccountInformationMapper
import com.algorand.wallet.account.info.data.mapper.entity.AssetHoldingEntityMapper
import com.algorand.wallet.account.info.data.mapper.model.AssetHoldingMapper
import com.algorand.wallet.account.info.data.mapper.entity.AssetStatusEntityMapper
import com.algorand.wallet.account.info.data.service.AccountInformationApiService
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlinx.coroutines.test.runTest

class AccountInformationRepositoryImplTest {

    private val indexerApi: AccountInformationApiService = mockk()
    private val accountInformationMapper: AccountInformationMapper = mockk()
    private val accountInformationDao: AccountInformationDao = mockk()
    private val assetHoldingDao: AssetHoldingDao = mockk()
    private val assetHoldingMapper: AssetHoldingMapper = mockk()
    private val assetStatusEntityMapper: AssetStatusEntityMapper = mockk()
    private val assetHoldingEntityMapper: AssetHoldingEntityMapper = mockk()
    private val accountInformationErrorCache: AccountInformationErrorCache = mockk()
    private val sut = AccountInformationRepositoryImpl(
        indexerApi,
        accountInformationMapper,
        accountInformationDao,
        assetHoldingDao,
        assetHoldingMapper,
        mockk(),
        mockk(),
        assetStatusEntityMapper,
        assetHoldingEntityMapper,
        accountInformationErrorCache
    )

    @Test
    fun `EXPECT rekeyed account count WHEN getFilteredRekeyedAccountCount is invoked`() = runTest {
        val authAddress = "authAddress"
        val algoAddresses = listOf("address1", "address2")
        val expectedCount = 2

        coEvery {
            accountInformationDao.getAuthAccountCountFilteredByAddress(authAddress, algoAddresses)
        } returns expectedCount

        val result = sut.getFilteredRekeyedAccountCount(authAddress, algoAddresses)

        coVerify { accountInformationDao.getAuthAccountCountFilteredByAddress(authAddress, algoAddresses) }
        assertEquals(expectedCount, result)
    }

}
