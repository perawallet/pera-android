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

package com.algorand.android.modules.transaction.refactor.usecase

import com.algorand.android.models.TransactionSignData
import com.algorand.wallet.account.core.domain.usecase.GetTransactionSigner
import com.algorand.wallet.account.info.domain.usecase.GetAccountInformation
import com.algorand.wallet.asset.domain.usecase.GetAsset
import javax.inject.Inject

internal class CreateRemoveAssetTransactionDataUseCase @Inject constructor(
    private val getAccountInformation: GetAccountInformation,
    private val getTransactionSigner: GetTransactionSigner,
    private val getAsset: GetAsset
) : CreateRemoveAssetTransactionData {

    override suspend fun invoke(address: String, assetId: Long): TransactionSignData.RemoveAsset? {
        val senderInfo = getAccountInformation(address) ?: return null
        val creatorAddress = getAsset(assetId)?.creatorAddress ?: return null
        return TransactionSignData.RemoveAsset(
            senderAccountAddress = senderInfo.address,
            senderAuthAddress = senderInfo.rekeyAdminAddress,
            signer = getTransactionSigner(senderInfo.address),
            assetId = assetId,
            creatorAddress = creatorAddress
        )
    }
}
