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

package com.algorand.wallet.account.info.data.mapper

import com.algorand.wallet.account.info.data.database.model.AccountInformationEntity
import java.math.BigInteger
import javax.inject.Inject

internal class AccountInformationErrorEntityMapperImpl @Inject constructor() : AccountInformationErrorEntityMapper {
    override fun invoke(address: String): AccountInformationEntity {
        return AccountInformationEntity(
            algoAddress = address,
            algoAmount = BigInteger.ZERO,
            optedInAppsCount = 0,
            appsTotalExtraPages = 0,
            authAlgoAddress = null,
            createdAtRound = null,
            lastFetchedRound = 0,
            totalCreatedAppsCount = 0,
            totalCreatedAssetsCount = 0,
            appStateNumByteSlice = null,
            appStateSchemaUint = null
        )
    }
}
