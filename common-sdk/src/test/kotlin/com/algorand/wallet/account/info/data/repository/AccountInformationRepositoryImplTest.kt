/*
 * Copyright 2025 Pera Wallet, LDA
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

import com.algorand.test.peraFixture
import com.algorand.wallet.account.info.data.cache.AccountInformationErrorCache
import com.algorand.wallet.account.info.data.database.dao.AccountInformationDao
import com.algorand.wallet.account.info.data.database.dao.AssetHoldingDao
import com.algorand.wallet.account.info.data.mapper.entity.AssetHoldingEntityMapper
import com.algorand.wallet.account.info.data.mapper.entity.AssetStatusEntityMapper
import com.algorand.wallet.account.info.data.mapper.model.AccountAssetAndAppsCountMapper
import com.algorand.wallet.account.info.data.mapper.model.AccountInformationMapper
import com.algorand.wallet.account.info.data.mapper.model.AssetHoldingMapper
import com.algorand.wallet.account.info.data.model.AccountAssetAndAppsCountDto
import com.algorand.wallet.account.info.data.service.AccountInformationApiService
import com.algorand.wallet.account.info.domain.model.AccountAssetAndAppsCount
import com.algorand.wallet.account.local.domain.usecase.GetLocalAccountsAddresses
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import java.math.BigInteger
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class AccountInformationRepositoryImplTest {

    private val indexerApi: AccountInformationApiService = mockk()
    private val accountInformationMapper: AccountInformationMapper = mockk()
    private val accountInformationDao: AccountInformationDao = mockk()
    private val assetHoldingDao: AssetHoldingDao = mockk()
    private val assetHoldingMapper: AssetHoldingMapper = mockk()
    private val assetStatusEntityMapper: AssetStatusEntityMapper = mockk()
    private val assetHoldingEntityMapper: AssetHoldingEntityMapper = mockk()
    private val accountInformationErrorCache: AccountInformationErrorCache = mockk()
    private val getLocalAccountsAddresses: GetLocalAccountsAddresses = mockk()
    private val accountAssetAndAppsCountMapper: AccountAssetAndAppsCountMapper = mockk()
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
        accountInformationErrorCache,
        getLocalAccountsAddresses,
        accountAssetAndAppsCountMapper
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

    @Test
    fun `EXPECT asset opted-in status WHEN isAssetOptedInByAnyLocalAccount is invoked`() = runTest {
        val assetId = 1234L
        val localAccountAddresses = listOf("address1", "address2")

        coEvery { getLocalAccountsAddresses() } returns localAccountAddresses
        coEvery { assetHoldingDao.isAssetOptedInByAnyLocalAccount(localAccountAddresses, assetId) } returns true

        val result = sut.isAssetOptedInByAnyLocalAccount(assetId)

        assertEquals(true, result)
    }

    @Test
    fun `EXPECT dao result WHEN get account algo balance is invoked`() = runTest {
        coEvery { accountInformationDao.getAccountAlgoBalance("address") } returns BigInteger.TWO

        val result = sut.getAccountAlgoBalance("address")

        assertEquals(BigInteger.TWO, result)
    }

    @Test
    fun `EXPECT account asset and app count WHEN exists in cache`() = runTest {
        val dto = peraFixture<AccountAssetAndAppsCountDto>()
        val assetAndAppsCount = peraFixture<AccountAssetAndAppsCount>()
        coEvery { accountInformationDao.getAssetsAndAppsCount(ADDRESS) } returns dto
        coEvery { accountAssetAndAppsCountMapper.map(dto) } returns assetAndAppsCount

        val result = sut.getAccountAssetsAndAppsCount(ADDRESS)

        assertEquals(assetAndAppsCount, result)
    }

    @Test
    fun `EXPECT null WHEN asset and app count is not in cache`() = runTest {
        coEvery { accountInformationDao.getAssetsAndAppsCount(ADDRESS) } returns null

        val result = sut.getAccountAssetsAndAppsCount(ADDRESS)

        assertEquals(null, result)
    }

    private companion object {
        const val ADDRESS = "address"
    }
}
