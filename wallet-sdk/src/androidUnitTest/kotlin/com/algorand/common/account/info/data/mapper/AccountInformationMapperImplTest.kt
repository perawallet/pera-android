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

package com.algorand.common.account.info.data.mapper

import com.algorand.common.account.info.data.model.AccountInformationResponse
import com.algorand.common.account.info.data.model.AccountInformationResponsePayloadResponse
import com.algorand.common.account.info.data.model.AppStateSchemaResponse
import com.algorand.common.account.info.data.model.AssetHoldingResponse
import com.algorand.common.account.info.domain.model.AccountInformation
import com.algorand.common.account.info.domain.model.AppStateScheme
import com.algorand.common.account.info.domain.model.AssetHolding
import com.algorand.common.account.info.domain.model.AssetStatus
import com.algorand.common.encryption.AddressEncryptionManager
import io.mockk.every
import io.mockk.mockk
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class AccountInformationMapperImplTest {

    private val appStateSchemeMapper: AppStateSchemeMapper = mockk {
        every { invoke(response = APP_STATE_SCHEMA_RESPONSE) } returns APP_STATE_SCHEMA
    }
    private val assetHoldingMapper: AssetHoldingMapper = mockk {
        every { invoke(response = ASSET_HOLDING_RESPONSE) } returns ASSET_HOLDING
    }
    private val addressEncryptionManager: AddressEncryptionManager = mockk()

    private val sut = AccountInformationMapperImpl(
        appStateSchemeMapper = appStateSchemeMapper,
        assetHoldingMapper = assetHoldingMapper,
        addressEncryptionManager = addressEncryptionManager
    )

    @Test
    fun `EXPECT null WHEN response account information is null`() {
        val response = ACCOUNT_INFORMATION_RESPONSE.copy(accountInformation = null)

        val result = sut(response)

        assertNull(result)
    }

    @Test
    fun `EXPECT null WHEN asset holding list has null item`() {
        every { assetHoldingMapper(ASSET_HOLDING_RESPONSE) } returns null

        val result = sut(ACCOUNT_INFORMATION_RESPONSE)

        assertNull(result)
    }

    @Test
    fun `EXPECT null WHEN response address is null`() {
        val response = ACCOUNT_INFORMATION_RESPONSE.copy(
            accountInformation = ACCOUNT_INFORMATION_RESPONSE.accountInformation?.copy(
                address = null
            )
        )

        val result = sut(response)

        assertNull(result)
    }

    @Test
    fun `EXPECT null WHEN response amount is null`() {
        val response = ACCOUNT_INFORMATION_RESPONSE.copy(
            accountInformation = ACCOUNT_INFORMATION_RESPONSE.accountInformation?.copy(
                amount = null
            )
        )

        val result = sut(response)

        assertNull(result)
    }

    @Test
    fun `EXPECT mapped object WHEN response is valid`() {
        val result = sut(ACCOUNT_INFORMATION_RESPONSE)

        val expected = ACCOUNT_INFORMATION
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT mapped object with default values WHEN optional values are missing in response`() {
        val response = ACCOUNT_INFORMATION_RESPONSE.copy(
            accountInformation = ACCOUNT_INFORMATION_RESPONSE.accountInformation?.copy(
                totalAppsOptedIn = null,
                totalAssetsOptedIn = null,
                totalCreatedApps = null,
                totalCreatedAssets = null,
                appsTotalExtraPages = null
            )
        )

        val result = sut(response)

        val expected = ACCOUNT_INFORMATION.copy(
            totalAppsOptedIn = 0,
            totalAssetsOptedIn = 0,
            totalCreatedApps = 0,
            totalCreatedAssets = 0,
            appsTotalExtraPages = 0
        )
        assertEquals(expected, result)
    }

    private companion object {
        val ASSET_HOLDING_RESPONSE = AssetHoldingResponse(
            assetId = 1,
            amount = "10",
            isDeleted = false,
            isFrozen = false,
            optedInAtRound = 1,
            optedOutAtRound = 2
        )

        val ASSET_HOLDING = AssetHolding(
            assetId = 1,
            amount = "10",
            isDeleted = false,
            isFrozen = false,
            optedInAtRound = 1,
            optedOutAtRound = 2,
            status = AssetStatus.OWNED_BY_ACCOUNT
        )

        val APP_STATE_SCHEMA_RESPONSE: AppStateSchemaResponse = AppStateSchemaResponse(
            numByteSlice = 1,
            numUint = 2
        )

        val APP_STATE_SCHEMA = AppStateScheme(
            numByteSlice = 1,
            numUint = 2
        )

        val ACCOUNT_INFORMATION_RESPONSE = AccountInformationResponse(
            accountInformation = AccountInformationResponsePayloadResponse(
                address = "address",
                amount = "10",
                participation = null,
                rekeyAdminAddress = null,
                allAssetHoldingList = listOf(ASSET_HOLDING_RESPONSE),
                createdAtRound = 1234,
                appStateSchemaResponse = APP_STATE_SCHEMA_RESPONSE,
                appsTotalExtraPages = 10,
                totalAppsOptedIn = 20,
                totalAssetsOptedIn = 30,
                totalCreatedApps = 40,
                totalCreatedAssets = 50
            ),
            currentRound = 0
        )

        val ACCOUNT_INFORMATION = AccountInformation(
            address = "address",
            amount = "10",
            lastFetchedRound = 0,
            rekeyAdminAddress = null,
            totalAppsOptedIn = 20,
            totalAssetsOptedIn = 30,
            totalCreatedApps = 40,
            totalCreatedAssets = 50,
            appsTotalExtraPages = 10,
            assetHoldings = listOf(ASSET_HOLDING),
            createdAtRound = 1234,
            appsTotalSchema = APP_STATE_SCHEMA
        )
    }
}
