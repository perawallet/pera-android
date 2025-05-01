/*
 * Copyright 2022-2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.android.modules.swap.accountselection.ui.usecase

import com.algorand.android.R
import com.algorand.android.models.AnnotatedString
import com.algorand.android.modules.assets.addition.ui.model.AssetAdditionPayload
import com.algorand.android.modules.swap.accountselection.ui.model.SwapAccountSelectionNavDirection
import com.algorand.android.modules.swap.accountselection.ui.model.SwapAccountSelectionPreview
import com.algorand.android.utils.Event
import com.algorand.wallet.account.info.domain.usecase.GetAccountAssetHoldings
import javax.inject.Inject

internal class GetSwapAccountSelectedUpdatedPreviewUseCase @Inject constructor(
    private val getAccountAssetHoldings: GetAccountAssetHoldings
) : GetSwapAccountSelectedUpdatedPreview {

    override suspend fun invoke(
        accountAddress: String,
        fromAssetId: Long?,
        toAssetId: Long?,
        defaultFromAssetIdArg: Long,
        defaultToAssetIdArg: Long,
        previousState: SwapAccountSelectionPreview
    ): SwapAccountSelectionPreview {
        with(previousState) {
            val assetHoldings = getAccountAssetHoldings(accountAddress)
            if (fromAssetId != null) {
                val isUserOptedIntoFromAsset = assetHoldings.any { it.assetId == fromAssetId }
                if (!isUserOptedIntoFromAsset) {
                    return copy(errorEvent = Event(AnnotatedString(R.string.you_are_not_opted_in)))
                }

                if (toAssetId != null) {
                    val isUserOptedIntoToAsset = assetHoldings.any { it.assetId == toAssetId }
                    if (!isUserOptedIntoToAsset) {
                        return getAssetAdditionPreview(previousState, toAssetId, accountAddress)
                    }
                }
            }

            if (toAssetId != null) {
                val isUserOptedIntoToAsset = assetHoldings.any { it.assetId == toAssetId }
                if (!isUserOptedIntoToAsset) {
                    return getAssetAdditionPreview(previousState, toAssetId, accountAddress)
                }
            }

            return copy(
                navToSwapNavigationEvent = getSwapNavigationDestinationEvent(
                    accountAddress = accountAddress,
                    fromAssetId = fromAssetId ?: defaultFromAssetIdArg,
                    toAssetId = toAssetId ?: defaultToAssetIdArg
                )
            )
        }
    }

    private fun getSwapNavigationDestinationEvent(
        accountAddress: String,
        fromAssetId: Long,
        toAssetId: Long
    ): Event<SwapAccountSelectionNavDirection> {
        return Event(
            SwapAccountSelectionNavDirection.SwapNavigation(
                accountAddress = accountAddress,
                fromAssetId = fromAssetId,
                toAssetId = toAssetId
            )
        )
    }

    private fun getAssetAdditionPreview(
        previousState: SwapAccountSelectionPreview,
        assetId: Long,
        accountAddress: String
    ): SwapAccountSelectionPreview {
        val assetAdditionAction = AssetAdditionPayload(assetId, accountAddress)
        return previousState.copy(
            isLoading = true,
            optInToAssetEvent = Event(assetAdditionAction)
        )
    }
}
