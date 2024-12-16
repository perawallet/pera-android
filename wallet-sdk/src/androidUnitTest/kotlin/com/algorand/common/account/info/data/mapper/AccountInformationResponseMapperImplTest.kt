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
import kotlin.test.Test
import kotlin.test.assertEquals

class AccountInformationResponseMapperImplTest {

    private val sut = AccountInformationResponseMapperImpl()

    @Test
    fun `EXPECT account information with default values`() {
        val result = sut.createEmptyAccount(ADDRESS)

        assertEquals(EXPECTED_ACCOUNT_INFORMATION_RESPONSE, result)
    }

    private companion object {
        const val ADDRESS = "address"
        val EXPECTED_ACCOUNT_INFORMATION_RESPONSE = AccountInformationResponse(
            accountInformation = AccountInformationResponsePayloadResponse(
                address = ADDRESS,
                amount = "0",
                participation = null,
                rekeyAdminAddress = null,
                allAssetHoldingList = emptyList(),
                createdAtRound = null,
                appStateSchemaResponse = null,
                appsTotalExtraPages = 0,
                totalAppsOptedIn = 0,
                totalAssetsOptedIn = 0,
                totalCreatedApps = 0,
                totalCreatedAssets = 0
            ),
            currentRound = 0
        )
    }
}
