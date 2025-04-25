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

package com.algorand.wallet.account.info.domain.usecase

import com.algorand.wallet.account.info.domain.model.AccountInformation
import com.algorand.wallet.asset.domain.util.AssetConstants.ALGO_ID
import java.math.BigInteger
import javax.inject.Inject

internal class IsAssetOwnedByAccountUseCase @Inject constructor(
    private val getAccountAlgoBalance: GetAccountAlgoBalance,
    private val getAccountAssetHoldingAmount: GetAccountAssetHoldingAmount
) : IsAssetOwnedByAccount {

    override suspend operator fun invoke(address: String, assetId: Long): Boolean {
        val assetBalance = getAssetBalance(address, assetId) ?: return false
        return assetBalance > BigInteger.ZERO
    }

    private suspend fun getAssetBalance(address: String, assetId: Long): BigInteger? {
        return if (assetId == ALGO_ID) {
            getAccountAlgoBalance(address)
        } else {
            getAccountAssetHoldingAmount(address, assetId)
        }
    }

    override suspend fun invoke(accountInfo: AccountInformation, assetId: Long): Boolean {
        return if (assetId == ALGO_ID) {
            accountInfo.amount > BigInteger.ZERO
        } else {
            accountInfo.assetHoldings.any { it.assetId == assetId && it.amount > BigInteger.ZERO }
        }
    }
}
