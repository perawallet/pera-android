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

import com.algorand.common.account.info.data.database.model.AccountInformationEntity
import com.algorand.common.account.info.data.model.AccountInformationResponse
import com.algorand.common.account.info.data.model.AccountInformationResponsePayloadResponse
import com.algorand.common.account.info.data.model.AppStateSchemaResponse
import com.algorand.common.testing.peraFixture
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class AccountInformationEntityMapperImplTest {

    private val sut = AccountInformationEntityMapperImpl()

    @Test
    fun `EXPECT account information to be mapped successfully`() {
        val result = sut(ACCOUNT_INFORMATION_RESPONSE)

        assertEquals(ACCOUNT_INFORMATION_ENTITY, result)
    }

    @Test
    fun `EXPECT null WHEN address is missing`() {
        val accountInformationResponse = AccountInformationResponse(
            accountInformation = ACCOUNT_INFORMATION_PAYLOAD.copy(
                address = null
            ),
            currentRound = 9
        )

        val result = sut(accountInformationResponse)

        assertNull(result)
    }

    @Test
    fun `EXPECT null WHEN current round is missing`() {
        val accountInformationResponse = AccountInformationResponse(
            accountInformation = ACCOUNT_INFORMATION_PAYLOAD.copy(
                address = null
            ),
            currentRound = 9
        )

        val result = sut(accountInformationResponse)

        assertNull(result)
    }

    @Test
    fun `EXPECT null WHEN algo amount is missing`() {
        val accountInformationResponse = AccountInformationResponse(
            accountInformation = ACCOUNT_INFORMATION_PAYLOAD.copy(
                address = null
            ),
            currentRound = 9
        )

        val result = sut(accountInformationResponse)

        assertNull(result)
    }

    @Test
    fun `EXPECT default values WHEN optional fields are missing`() {
        val accountInformationResponse = AccountInformationResponse(
            accountInformation = ACCOUNT_INFORMATION_PAYLOAD.copy(
                totalAppsOptedIn = null,
                totalCreatedApps = null,
                totalCreatedAssets = null,
                appsTotalExtraPages = null
            ),
            currentRound = 9
        )

        val result = sut(accountInformationResponse)

        val expected = ACCOUNT_INFORMATION_ENTITY.copy(
            optedInAppsCount = 0,
            totalCreatedAppsCount = 0,
            totalCreatedAssetsCount = 0,
            appsTotalExtraPages = 0,
        )
        assertEquals(expected, result)
    }

    companion object {
        private const val ADDRESS = "address"

        private val ACCOUNT_INFORMATION_PAYLOAD = peraFixture<AccountInformationResponsePayloadResponse>().copy(
            address = ADDRESS,
            amount = "10",
            participation = null,
            rekeyAdminAddress = "rekeyAddress",
            allAssetHoldingList = emptyList(),
            createdAtRound = 9,
            appStateSchemaResponse = AppStateSchemaResponse(
                numByteSlice = 21,
                numUint = 12
            ),
            appsTotalExtraPages = 2,
            totalAppsOptedIn = 9,
            totalAssetsOptedIn = 4,
            totalCreatedAssets = 0,
            totalCreatedApps = 0
        )
        private val ACCOUNT_INFORMATION_RESPONSE = AccountInformationResponse(
            accountInformation = ACCOUNT_INFORMATION_PAYLOAD,
            currentRound = 9
        )

        private val ACCOUNT_INFORMATION_ENTITY = AccountInformationEntity(
            algoAddress = ADDRESS,
            algoAmount = "10",
            lastFetchedRound = 9,
            authAlgoAddress = "rekeyAddress",
            optedInAppsCount = 9,
            appsTotalExtraPages = 2,
            createdAtRound = 9,
            totalCreatedAssetsCount = 0,
            totalCreatedAppsCount = 0,
            appStateNumByteSlice = 21,
            appStateSchemaUint = 12
        )
    }
}