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
import com.algorand.wallet.account.info.data.database.model.AccountInformationEntity
import com.algorand.wallet.account.info.data.mapper.entity.AccountInformationEntityMapper
import com.algorand.wallet.account.info.data.mapper.model.AccountInformationMapper
import com.algorand.wallet.account.info.data.model.AccountInformationResponse
import com.algorand.wallet.account.info.data.model.AssetHoldingResponse
import com.algorand.wallet.account.info.domain.model.AccountInformation
import com.algorand.wallet.account.info.domain.model.AssetHolding
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AccountInformationCacheHelperImplTest {

    private val mockAccountInformationEntityMapper = mockk<AccountInformationEntityMapper>()
    private val mockAccountInformationMapper = mockk<AccountInformationMapper>()
    private val mockAccountInformationDao = mockk<AccountInformationDao>(relaxed = true)
    private val mockAssetHoldingCacheHelper = mockk<AssetHoldingCacheHelper>()
    private val mockAccountInformationErrorCache = mockk<AccountInformationErrorCache>(relaxed = true)
    private var sut: AccountInformationCacheHelperImpl = AccountInformationCacheHelperImpl(
        mockAccountInformationEntityMapper,
        mockAccountInformationMapper,
        mockAccountInformationDao,
        mockAssetHoldingCacheHelper,
        mockAccountInformationErrorCache
    )

    @Test
    fun `EXPECT account information WHEN entity mapping succeeds`() = runTest {
        val address = "TEST_ADDRESS"
        val mockResponse = mockk<AccountInformationResponse>()
        val mockAssetHoldingList = listOf(mockk<AssetHoldingResponse>())
        val mockEntity = mockk<AccountInformationEntity>()
        val mockAssetHoldings = listOf(mockk<AssetHolding>())
        val expectedAccountInformation = mockk<AccountInformation>()

        coEvery { mockResponse.accountInformation?.allAssetHoldingList } returns mockAssetHoldingList
        coEvery { mockAccountInformationEntityMapper(mockResponse) } returns mockEntity
        coEvery { mockAssetHoldingCacheHelper.cacheAssetHolding(address, mockAssetHoldingList) } returns mockAssetHoldings
        coEvery { mockAccountInformationMapper(mockEntity, mockAssetHoldings) } returns expectedAccountInformation

        val result = sut.cacheAccountInformation(address, mockResponse)

        assertEquals(expectedAccountInformation, result)
        coVerify(exactly = 1) { mockAccountInformationDao.insert(mockEntity) }
        coVerify(exactly = 1) { mockAccountInformationErrorCache.remove(address) }
    }

    @Test
    fun `EXPECT null WHEN entity mapping fails and address does not exist`() = runTest {
        val address = "TEST_ADDRESS"
        val mockResponse = mockk<AccountInformationResponse>()

        coEvery { mockAccountInformationEntityMapper(mockResponse) } returns null
        coEvery { mockAccountInformationDao.isAddressExists(address) } returns false

        val result = sut.cacheAccountInformation(address, mockResponse)

        assertNull(result)
        coVerify(exactly = 0) { mockAccountInformationDao.insert(any()) }
        coVerify(exactly = 0) { mockAccountInformationErrorCache.remove(any()) }
        coVerify(exactly = 1) { mockAccountInformationErrorCache.put(address) }
    }

    @Test
    fun `EXPECT null WHEN entity mapping fails and address exists`() = runTest {
        val address = "TEST_ADDRESS"
        val mockResponse = mockk<AccountInformationResponse>()

        coEvery { mockAccountInformationEntityMapper(mockResponse) } returns null
        coEvery { mockAccountInformationDao.isAddressExists(address) } returns true

        val result = sut.cacheAccountInformation(address, mockResponse)

        assertNull(result)
        coVerify(exactly = 0) { mockAccountInformationDao.insert(any()) }
        coVerify(exactly = 0) { mockAccountInformationErrorCache.remove(any()) }
        coVerify(exactly = 0) { mockAccountInformationErrorCache.put(any()) }
    }

    @Test
    fun `EXPECT account information WHEN response has empty asset holdings`() = runTest {
        val address = "TEST_ADDRESS"
        val mockResponse = mockk<AccountInformationResponse>()
        val mockEntity = mockk<AccountInformationEntity>()
        val emptyAssetHoldings = emptyList<AssetHolding>()
        val expectedAccountInformation = mockk<AccountInformation>()

        coEvery { mockResponse.accountInformation?.allAssetHoldingList } returns null
        coEvery { mockAccountInformationEntityMapper(mockResponse) } returns mockEntity
        coEvery { mockAssetHoldingCacheHelper.cacheAssetHolding(address, emptyList()) } returns emptyAssetHoldings
        coEvery { mockAccountInformationMapper(mockEntity, emptyAssetHoldings) } returns expectedAccountInformation

        val result = sut.cacheAccountInformation(address, mockResponse)

        assertEquals(expectedAccountInformation, result)
        coVerify(exactly = 1) { mockAccountInformationDao.insert(mockEntity) }
        coVerify(exactly = 1) { mockAccountInformationErrorCache.remove(address) }
    }
}
