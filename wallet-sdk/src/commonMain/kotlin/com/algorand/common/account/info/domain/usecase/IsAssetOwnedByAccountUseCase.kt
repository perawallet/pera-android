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

package com.algorand.common.account.info.domain.usecase

internal class IsAssetOwnedByAccountUseCase(
    private val getAccountInformation: GetAccountInformation
) : IsAssetOwnedByAccount {

    override suspend operator fun invoke(address: String, assetId: Long): Boolean {
        val assetHolding = getAccountInformation(address)
            ?.assetHoldings
            ?.firstOrNull { it.assetId == assetId }
            ?: return false
        return assetHolding.amount != "0" // TODO Fix here after fixing BigInteger issue on wallet-sdk
    }
}
