/*
 *   ~ Copyright 2022 Pera Wallet, LDA
 *   ~ Licensed under the Apache License, Version 2.0 (the "License");
 *   ~ you may not use this file except in compliance with the License.
 *   ~ You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *   ~ Unless required by applicable law or agreed to in writing, software
 *   ~ distributed under the License is distributed on an "AS IS" BASIS,
 *   ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   ~ See the License for the specific language governing permissions and
 *   ~ limitations under the License
 *   -->
 */

package com.algorand.android.module.swap.ui.accountselection.usecase

import com.algorand.android.accountinfo.component.domain.usecase.GetAccountInformation
import com.algorand.android.assetutils.AssetAdditionPayload
import com.algorand.android.designsystem.AnnotatedString
import com.algorand.android.designsystem.R
import com.algorand.android.foundation.Event
import com.algorand.android.module.swap.ui.accountselection.model.SwapAccountSelectionNavDirection
import com.algorand.android.module.swap.ui.accountselection.model.SwapAccountSelectionPreview
import javax.inject.Inject

internal class GetSwapAccountSelectedUpdatedPreviewUseCase @Inject constructor(
    private val getAccountInformation: GetAccountInformation,
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
            val accountInfo = getAccountInformation(accountAddress) ?: return copy(
                errorEvent = Event(AnnotatedString(R.string.an_error_occured))
            )

            if (fromAssetId != null) {
                val isUserOptedIntoFromAsset = accountInfo.hasAsset(fromAssetId)
                if (!isUserOptedIntoFromAsset) {
                    return copy(errorEvent = Event(AnnotatedString(R.string.you_are_not_opted_in)))
                }

                if (toAssetId != null) {
                    val isUserOptedIntoToAsset = accountInfo.hasAsset(toAssetId)
                    if (!isUserOptedIntoToAsset) {
                        return getAssetAdditionPreview(previousState, toAssetId, accountAddress)
                    }
                }
            }

            if (toAssetId != null) {
                val isUserOptedIntoToAsset = accountInfo.hasAsset(toAssetId)
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
