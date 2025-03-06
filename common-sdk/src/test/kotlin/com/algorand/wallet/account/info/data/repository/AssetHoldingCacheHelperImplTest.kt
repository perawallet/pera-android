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

import com.algorand.test.peraFixture
import com.algorand.wallet.account.info.data.database.dao.AssetHoldingDao
import com.algorand.wallet.account.info.data.database.model.AssetHoldingEntity
import com.algorand.wallet.account.info.data.database.model.AssetStatusEntity.OWNED_BY_ACCOUNT
import com.algorand.wallet.account.info.data.database.model.AssetStatusEntity.PENDING_FOR_ADDITION
import com.algorand.wallet.account.info.data.database.model.AssetStatusEntity.PENDING_FOR_REMOVAL
import com.algorand.wallet.account.info.data.mapper.entity.AssetHoldingEntityMapper
import com.algorand.wallet.account.info.data.mapper.model.AssetHoldingMapper
import com.algorand.wallet.account.info.data.model.AssetHoldingResponse
import com.algorand.wallet.account.info.domain.model.AssetHolding
import com.algorand.wallet.account.info.domain.model.AssetStatus
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import java.math.BigInteger
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AssetHoldingCacheHelperImplTest {

    private val assetHoldingDao: AssetHoldingDao = mockk(relaxed = true)
    private val assetHoldingEntityMapper: AssetHoldingEntityMapper = mockk()
    private val assetHoldingMapper: AssetHoldingMapper = mockk()

    private val sut = AssetHoldingCacheHelperImpl(assetHoldingDao, assetHoldingEntityMapper, assetHoldingMapper)

    @Test
    fun `EXPECT empty list and pending for removals to be removed WHEN response is empty`() = runTest {
        coEvery { assetHoldingDao.getAssetsByAddress(ADDRESS) } returns listOf(PENDING_FOR_REMOVAL_ENTITY)
        every { assetHoldingMapper(emptyList()) } returns emptyList()

        val result = sut.cacheAssetHolding(ADDRESS, emptyList())

        coVerify { assetHoldingDao.updateAssetHoldings(ADDRESS, emptyList()) }
        assertTrue(result.isEmpty())
    }

    @Test
    fun `EXPECT new asset to be cached WHEN response contains new asset`() = runTest {
        coEvery { assetHoldingDao.getAssetsByAddress(ADDRESS) } returns emptyList()
        every { assetHoldingEntityMapper(ADDRESS, OWNED_RESPONSE, AssetStatus.OWNED_BY_ACCOUNT) } returns OWNED_ENTITY
        every { assetHoldingMapper(listOf(OWNED_ENTITY)) } returns listOf(OWNED_ASSET_HOLDING)

        val result = sut.cacheAssetHolding(ADDRESS, listOf(OWNED_RESPONSE))

        coVerify { assetHoldingDao.updateAssetHoldings(ADDRESS, listOf(OWNED_ENTITY)) }
        val expected = listOf(OWNED_ASSET_HOLDING)
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT pending for removal to be removed WHEN response does not contain them`() = runTest {
        val response = listOf(OWNED_RESPONSE)
        val assetHoldings = listOf(OWNED_ENTITY, PENDING_FOR_REMOVAL_ENTITY)
        coEvery { assetHoldingDao.getAssetsByAddress(ADDRESS) } returns assetHoldings
        every { assetHoldingMapper.invoke(listOf(OWNED_ENTITY)) } returns listOf(OWNED_ASSET_HOLDING)
        every { assetHoldingEntityMapper(ADDRESS, OWNED_RESPONSE, AssetStatus.OWNED_BY_ACCOUNT) } returns OWNED_ENTITY

        val result = sut.cacheAssetHolding(ADDRESS, response)

        coVerify { assetHoldingDao.updateAssetHoldings(ADDRESS, listOf(OWNED_ENTITY)) }
        val expected = listOf(OWNED_ASSET_HOLDING)
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT pending for removal to be kept WHEN response still contains it`() = runTest {
        val response = listOf(OWNED_RESPONSE, PENDING_FOR_REMOVAL_RESPONSE)
        val assetHoldingEntities = listOf(OWNED_ENTITY, PENDING_FOR_REMOVAL_ENTITY)
        val assetHoldings = listOf(OWNED_ASSET_HOLDING, PENDING_FOR_REMOVAL_ASSET_HOLDING)
        coEvery { assetHoldingDao.getAssetsByAddress(ADDRESS) } returns assetHoldingEntities
        every { assetHoldingMapper(assetHoldingEntities) } returns assetHoldings
        every { assetHoldingEntityMapper(ADDRESS, OWNED_RESPONSE, AssetStatus.OWNED_BY_ACCOUNT) } returns OWNED_ENTITY
        every {
            assetHoldingEntityMapper(ADDRESS, PENDING_FOR_REMOVAL_RESPONSE, AssetStatus.PENDING_FOR_REMOVAL)
        } returns PENDING_FOR_REMOVAL_ENTITY

        val result = sut.cacheAssetHolding(ADDRESS, response)

        coVerify { assetHoldingDao.updateAssetHoldings(ADDRESS, listOf(OWNED_ENTITY, PENDING_FOR_REMOVAL_ENTITY)) }
        assertEquals(assetHoldings, result)
    }

    @Test
    fun `EXPECT pending for addition to be cached as owned WHEN asset holding contains it`() = runTest {
        val ownedResponse = peraFixture<AssetHoldingResponse>().copy(
            assetId = PENDING_FOR_ADDITION_ENTITY.assetId,
            amount = "0"
        )
        val ownedEntity = peraFixture<AssetHoldingEntity>().copy(
            id = PENDING_FOR_ADDITION_ENTITY.id,
            assetStatusEntity = OWNED_BY_ACCOUNT,
            amount = BigInteger.ZERO,
            algoAddress = ADDRESS
        )
        val assetHolding = peraFixture<AssetHolding>().copy(
            assetId = PENDING_FOR_ADDITION_ENTITY.assetId,
            status = AssetStatus.OWNED_BY_ACCOUNT,
            amount = BigInteger.ZERO
        )
        coEvery { assetHoldingDao.getAssetsByAddress(ADDRESS) } returns listOf(PENDING_FOR_ADDITION_ENTITY)
        every { assetHoldingMapper.invoke(listOf(ownedEntity)) } returns listOf(assetHolding)
        every { assetHoldingEntityMapper(ADDRESS, ownedResponse, AssetStatus.OWNED_BY_ACCOUNT) } returns ownedEntity

        val result = sut.cacheAssetHolding(ADDRESS, listOf(ownedResponse))

        coVerify { assetHoldingDao.updateAssetHoldings(ADDRESS, listOf(ownedEntity)) }
        val expected = listOf(assetHolding)
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT pending for addition to be cached as is WHEN asset holdings does not contain it`() = runTest {
        val response = listOf(OWNED_RESPONSE)
        val assetHoldings = listOf(OWNED_ENTITY, PENDING_FOR_ADDITION_ENTITY)
        coEvery { assetHoldingDao.getAssetsByAddress(ADDRESS) } returns assetHoldings
        every { assetHoldingMapper.invoke(listOf(OWNED_ENTITY, PENDING_FOR_ADDITION_ENTITY)) } returns listOf(
            OWNED_ASSET_HOLDING,
            PENDING_FOR_ADDITION_ASSET_HOLDING
        )
        every { assetHoldingEntityMapper(ADDRESS, OWNED_RESPONSE, AssetStatus.OWNED_BY_ACCOUNT) } returns OWNED_ENTITY

        val result = sut.cacheAssetHolding(ADDRESS, response)

        coVerify { assetHoldingDao.updateAssetHoldings(ADDRESS, listOf(OWNED_ENTITY, PENDING_FOR_ADDITION_ENTITY)) }
        val expected = listOf(OWNED_ASSET_HOLDING, PENDING_FOR_ADDITION_ASSET_HOLDING)
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT new asset not to be cached WHEN mapping fails`() = runTest {
        coEvery { assetHoldingDao.getAssetsByAddress(ADDRESS) } returns emptyList()
        every { assetHoldingEntityMapper(ADDRESS, OWNED_RESPONSE, AssetStatus.OWNED_BY_ACCOUNT) } returns null
        every { assetHoldingMapper(emptyList()) } returns emptyList()

        val result = sut.cacheAssetHolding(ADDRESS, listOf(OWNED_RESPONSE))

        coVerify { assetHoldingDao.updateAssetHoldings(ADDRESS, emptyList()) }
        assertTrue(result.isEmpty())
    }

    @Test
    fun `EXPECT cached asset not to be updated WHEN mapping fails`() = runTest {
        coEvery { assetHoldingDao.getAssetsByAddress(ADDRESS) } returns listOf(OWNED_ENTITY)
        every { assetHoldingEntityMapper(ADDRESS, OWNED_RESPONSE, AssetStatus.OWNED_BY_ACCOUNT) } returns null
        every { assetHoldingMapper(emptyList()) } returns emptyList()

        val result = sut.cacheAssetHolding(ADDRESS, listOf(OWNED_RESPONSE))

        coVerify { assetHoldingDao.updateAssetHoldings(ADDRESS, emptyList()) }
        assertTrue(result.isEmpty())
    }

    private companion object {
        private const val ADDRESS = "ADDRESS"
        private val OWNED_ENTITY = peraFixture<AssetHoldingEntity>().copy(
            id = 1,
            assetStatusEntity = OWNED_BY_ACCOUNT,
            amount = BigInteger.ONE
        )
        private val PENDING_FOR_REMOVAL_ENTITY = peraFixture<AssetHoldingEntity>().copy(
            id = 2,
            assetStatusEntity = PENDING_FOR_REMOVAL,
            amount = BigInteger.ONE
        )
        private val PENDING_FOR_ADDITION_ENTITY = peraFixture<AssetHoldingEntity>().copy(
            id = 3,
            assetStatusEntity = PENDING_FOR_ADDITION,
            amount = BigInteger.ONE
        )

        private val OWNED_RESPONSE = peraFixture<AssetHoldingResponse>().copy(
            assetId = OWNED_ENTITY.assetId,
            amount = "10"
        )
        private val PENDING_FOR_REMOVAL_RESPONSE = peraFixture<AssetHoldingResponse>().copy(
            assetId = PENDING_FOR_REMOVAL_ENTITY.assetId,
            amount = "10"
        )

        private val OWNED_ASSET_HOLDING = peraFixture<AssetHolding>().copy(
            assetId = OWNED_ENTITY.assetId,
            status = AssetStatus.OWNED_BY_ACCOUNT,
            amount = BigInteger.TEN
        )
        private val PENDING_FOR_REMOVAL_ASSET_HOLDING = peraFixture<AssetHolding>().copy(
            assetId = PENDING_FOR_REMOVAL_ENTITY.assetId,
            status = AssetStatus.PENDING_FOR_REMOVAL,
            amount = BigInteger.TEN
        )
        private val PENDING_FOR_ADDITION_ASSET_HOLDING = peraFixture<AssetHolding>().copy(
            assetId = PENDING_FOR_ADDITION_ENTITY.assetId,
            status = AssetStatus.PENDING_FOR_ADDITION,
            amount = BigInteger.TEN
        )
    }
}
