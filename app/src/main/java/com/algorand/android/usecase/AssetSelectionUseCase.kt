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

package com.algorand.android.usecase

import com.algorand.android.module.account.core.ui.asset.select.BaseSelectAssetItem
import com.algorand.android.module.account.core.ui.usecase.GetAssetName
import com.algorand.android.module.account.info.domain.usecase.FetchAccountInformation
import com.algorand.android.assetdetail.component.AssetConstants.ALGO_ASSET_ID
import com.algorand.android.assetdetail.component.asset.domain.usecase.GetAsset
import com.algorand.android.module.account.core.component.assetdata.usecase.GetAccountOwnedAssetsDataFlow
import com.algorand.android.module.account.core.component.domain.model.BaseAccountAssetData
import com.algorand.android.module.account.core.component.domain.model.BaseAccountAssetData.BaseOwnedAssetData.BaseOwnedCollectibleData
import com.algorand.android.module.account.core.component.domain.model.BaseAccountAssetData.BaseOwnedAssetData.BaseOwnedCollectibleData.OwnedCollectibleAudioData
import com.algorand.android.module.account.core.component.domain.model.BaseAccountAssetData.BaseOwnedAssetData.BaseOwnedCollectibleData.OwnedCollectibleImageData
import com.algorand.android.module.account.core.component.domain.model.BaseAccountAssetData.BaseOwnedAssetData.BaseOwnedCollectibleData.OwnedCollectibleMixedData
import com.algorand.android.module.account.core.component.domain.model.BaseAccountAssetData.BaseOwnedAssetData.BaseOwnedCollectibleData.OwnedCollectibleUnsupportedData
import com.algorand.android.module.account.core.component.domain.model.BaseAccountAssetData.BaseOwnedAssetData.BaseOwnedCollectibleData.OwnedCollectibleVideoData
import com.algorand.android.module.account.core.component.domain.usecase.GetAccountCollectibleDataFlow
import com.algorand.android.customviews.accountandassetitem.mapper.AssetItemConfigurationMapper
import com.algorand.android.mapper.AssetSelectionMapper
import com.algorand.android.models.AssetTransaction
import com.algorand.android.modules.sorting.assetsorting.ui.usecase.AssetItemSortUseCase
import com.algorand.android.nft.mapper.AssetSelectionPreviewMapper
import com.algorand.android.nft.ui.model.AssetSelectionOptInPayload
import com.algorand.android.nft.ui.model.AssetSelectionPreview
import com.algorand.android.parity.domain.usecase.GetSelectedCurrencyDetailFlow
import com.algorand.android.utils.Event
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow

@SuppressWarnings("LongParameterList")
class AssetSelectionUseCase @Inject constructor(
    private val transactionTipsUseCase: TransactionTipsUseCase,
    private val assetSelectionMapper: AssetSelectionMapper,
    private val assetSelectionPreviewMapper: AssetSelectionPreviewMapper,
    private val fetchAccountInformation: FetchAccountInformation,
    private val assetItemConfigurationMapper: AssetItemConfigurationMapper,
    private val assetItemSortUseCase: AssetItemSortUseCase,
    private val getAccountOwnedAssetsDataFlow: GetAccountOwnedAssetsDataFlow,
    private val getAccountCollectibleDataFlow: GetAccountCollectibleDataFlow,
    private val getSelectedCurrencyDetailFlow: GetSelectedCurrencyDetailFlow,
    private val getAssetName: GetAssetName,
    private val getAsset: GetAsset
) {
    fun getAssetSelectionListFlow(publicKey: String): Flow<List<BaseSelectAssetItem>> {
        return combine(
            getAccountOwnedAssetsDataFlow(publicKey, includeAlgo = true),
            getAccountCollectibleDataFlow(publicKey),
            getSelectedCurrencyDetailFlow()
        ) { accountAssetData, accountCollectibleData, _ ->
            val assetList = mutableListOf<BaseSelectAssetItem>().apply {
                addAll(createAssetSelectionItems(accountAssetData))
                addAll(createCollectibleSelectionItems(accountCollectibleData))
            }
            assetItemSortUseCase.sortAssets(assetList)
        }.distinctUntilChanged()
    }

    private suspend fun createAssetSelectionItems(
        accountAssetData: List<BaseAccountAssetData.BaseOwnedAssetData.OwnedAssetData>
    ): List<BaseSelectAssetItem> {
        return accountAssetData.map { baseAccountAssetData ->
            val assetItemConfiguration = with(baseAccountAssetData) {
                assetItemConfigurationMapper.mapTo(
                    isAmountInSelectedCurrencyVisible = isAmountInSelectedCurrencyVisible,
                    secondaryValueText =
                    getSelectedCurrencyParityValue().getFormattedValue(isCompact = true),
                    formattedCompactAmount = formattedCompactAmount,
                    assetId = id,
                    name = name,
                    shortName = shortName,
                    verificationTier = verificationTier,
                    primaryValue = parityValueInSelectedCurrency.amountAsCurrency
                )
            }
            assetSelectionMapper.mapToAssetItem(assetItemConfiguration)
        }
    }

    private suspend fun createCollectibleSelectionItems(
        accountCollectibleData: List<BaseOwnedCollectibleData>
    ): List<BaseSelectAssetItem.BaseSelectCollectibleItem> {
        return accountCollectibleData.mapNotNull { ownedCollectibleData ->
            val isOwnedByTheUser = ownedCollectibleData.isOwnedByTheUser
            if (isOwnedByTheUser) {
                when (ownedCollectibleData) {
                    is OwnedCollectibleImageData -> assetSelectionMapper.mapToCollectibleImageItem(
                        ownedCollectibleData
                    )

                    is OwnedCollectibleVideoData -> assetSelectionMapper.mapToCollectibleVideoItem(
                        ownedCollectibleData
                    )

                    is OwnedCollectibleMixedData -> assetSelectionMapper.mapToCollectibleMixedItem(
                        ownedCollectibleData
                    )

                    is OwnedCollectibleUnsupportedData -> {
                        assetSelectionMapper.mapToCollectibleNotSupportedItem(ownedCollectibleData)
                    }

                    is OwnedCollectibleAudioData -> assetSelectionMapper.mapToCollectibleAudioItem(
                        ownedCollectibleData
                    )
                }
            } else {
                null
            }
        }
    }

    fun shouldShowTransactionTips(): Boolean {
        return transactionTipsUseCase.shouldShowTransactionTips()
    }

    fun getInitialStateOfAssetSelectionPreview(assetTransaction: AssetTransaction): AssetSelectionPreview {
        return assetSelectionPreviewMapper.mapToInitialState(assetTransaction)
    }

    fun getUpdatedPreviewFlowWithSelectedAsset(
        assetId: Long,
        previousState: AssetSelectionPreview
    ) = flow<AssetSelectionPreview> {
        emit(previousState.copy(isReceiverAccountOptInCheckLoadingVisible = true))
        val receiverAddress = previousState.assetTransaction.receiverUser?.address
        val loadingFinishedStatePreview =
            previousState.copy(isReceiverAccountOptInCheckLoadingVisible = false)
        receiverAddress?.let {
            fetchAccountInformation(it)
                .onSuccess { accountInformation ->
                    val isReceiverOptedInToAsset =
                        assetId == ALGO_ASSET_ID || accountInformation.hasAsset(assetId)
                    val newState = if (!isReceiverOptedInToAsset) {
                        val payload = getOptInPayload(assetId, previousState) ?: return@let
                        loadingFinishedStatePreview.copy(navigateToOptInEvent = Event(payload))
                    } else {
                        loadingFinishedStatePreview.copy(
                            navigateToAssetTransferAmountFragmentEvent = Event(assetId)
                        )
                    }
                    emit(newState)
                }
                .onFailure {
                    val exceptionMessage = it.message
                    if (exceptionMessage != null) {
                        emit(
                            loadingFinishedStatePreview.copy(
                                globalErrorTextEvent = Event(
                                    exceptionMessage
                                )
                            )
                        )
                    } else {
                        // TODO Show default error message
                        emit(loadingFinishedStatePreview)
                    }
                }
        } ?: emit(
            loadingFinishedStatePreview.copy(
                navigateToAssetTransferAmountFragmentEvent = Event(
                    assetId
                )
            )
        )
    }

    private suspend fun getOptInPayload(
        assetId: Long,
        previousState: AssetSelectionPreview
    ): AssetSelectionOptInPayload? {
        val receiverAddress = previousState.assetTransaction.receiverUser?.address ?: return null
        val assetDetail = getAsset(assetId) ?: return null
        return AssetSelectionOptInPayload(
            assetId = assetId,
            senderAddress = previousState.assetTransaction.senderAddress,
            receiverAddress = receiverAddress,
            assetName = getAssetName(assetDetail.fullName).assetName
        )
    }
}
