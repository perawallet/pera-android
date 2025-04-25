/*
 * Copyright 2022-2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 *  limitations under the License
 */

package com.algorand.android.usecase

import com.algorand.android.mapper.SenderAccountSelectionPreviewMapper
import com.algorand.android.models.AssetTransaction
import com.algorand.android.models.BaseAccountSelectionListItem
import com.algorand.android.models.Result
import com.algorand.android.models.SenderAccountSelectionPreview
import com.algorand.android.models.TargetUser
import com.algorand.android.models.TransactionSignData
import com.algorand.android.modules.accountcore.ui.accountselection.usecase.GetAccountSelectionAccountsWhichCanSignTransaction
import com.algorand.android.modules.accountcore.ui.usecase.GetAccountIconDrawablePreview
import com.algorand.android.modules.accounts.lite.domain.usecase.GetAccountLite
import com.algorand.android.utils.Event
import com.algorand.wallet.account.core.domain.usecase.FetchAccountInformationAndCacheAssets
import com.algorand.wallet.account.core.domain.usecase.GetTransactionSigner
import com.algorand.wallet.account.info.domain.usecase.GetAccountInformation
import java.math.BigInteger
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SenderAccountSelectionPreviewUseCase @Inject constructor(
    private val senderAccountSelectionPreviewMapper: SenderAccountSelectionPreviewMapper,
    private val getAccountIconDrawablePreview: GetAccountIconDrawablePreview,
    private val getAccountSelectionAccountsWhichCanSignTransaction: GetAccountSelectionAccountsWhichCanSignTransaction,
    private val fetchAccountInformationAndCacheAssets: FetchAccountInformationAndCacheAssets,
    private val getAccountInformation: GetAccountInformation,
    private val getTransactionSigner: GetTransactionSigner,
    private val getAccountLite: GetAccountLite
) {

    suspend fun createSendTransactionData(
        accountAddress: String,
        note: String?,
        assetId: Long,
        amount: BigInteger,
        assetTransaction: AssetTransaction
    ): TransactionSignData.Send? {
        val senderAccountLite = getAccountLite(accountAddress)
        val senderAccountLiteCachedData = senderAccountLite?.cachedInfo ?: return null
        val receiverAccountInfo = getAccountInformation(accountAddress)
        return TransactionSignData.Send(
            senderAccountAddress = accountAddress,
            senderAuthAddress = senderAccountLiteCachedData.rekeyAuthAddress,
            senderAlgoAmount = senderAccountLiteCachedData.algoAmountValue.amount,
            senderAccountName = senderAccountLite.customName,
            minimumBalance = senderAccountLiteCachedData.minRequiredBalance.toLong(),
            amount = amount,
            assetId = assetId,
            note = note,
            targetUser = TargetUser(
                contact = assetTransaction.receiverUser,
                publicKey = assetTransaction.receiverUser?.publicKey.orEmpty(),
                accountIconDrawablePreview = getAccountIconDrawablePreview(accountAddress)
            ),
            isArc59Transaction = receiverAccountInfo?.hasAsset(assetId)?.not() ?: false,
            signer = getTransactionSigner(accountAddress)
        )
    }

    fun getInitialPreview(): SenderAccountSelectionPreview {
        return senderAccountSelectionPreviewMapper.mapToInitialPreview()
    }

    suspend fun getUpdatedPreviewWithAccountList(
        preview: SenderAccountSelectionPreview
    ): SenderAccountSelectionPreview {
        val accountList = getBaseNormalAccountListItems()
        return preview.copy(
            accountList = accountList,
            isEmptyStateVisible = accountList.isEmpty(),
            isLoading = false
        )
    }

    suspend fun getUpdatedPreviewWithAccountListAndSpecificAsset(
        assetId: Long,
        preview: SenderAccountSelectionPreview
    ): SenderAccountSelectionPreview {
        val accountList = getBaseNormalAccountListItemsFilteredByAssetId(assetId)
        return preview.copy(
            accountList = accountList,
            isEmptyStateVisible = accountList.isEmpty(),
            isLoading = false
        )
    }

    fun getUpdatedPreviewFlowWithAccountInformation(
        senderAccountAddress: String,
        preview: SenderAccountSelectionPreview
    ): Flow<SenderAccountSelectionPreview> = flow {
        emit(preview.copy(isLoading = true))
        val loadingFinishedPreview = preview.copy(isLoading = false)
        fetchAccountInformationAndCacheAssets(senderAccountAddress, false).use(
            onSuccess = {
                emit(loadingFinishedPreview.copy(senderAccountInformationSuccessEvent = Event(it)))
            },
            onFailed = { exception, code ->
                val errorEvent = Event(Result.Error(exception, code))
                emit(loadingFinishedPreview.copy(senderAccountInformationErrorEvent = errorEvent))
            }
        )
    }

    private suspend fun getBaseNormalAccountListItems(): List<BaseAccountSelectionListItem> {
        return getAccountSelectionAccountsWhichCanSignTransaction(
            showHoldings = true,
            showFailedAccounts = true
        )
    }

    private suspend fun getBaseNormalAccountListItemsFilteredByAssetId(
        assetId: Long
    ): List<BaseAccountSelectionListItem> {
        return getAccountSelectionAccountsWhichCanSignTransaction(
            assetId = assetId,
            showHoldings = true,
            showFailedAccounts = true
        )
    }
}
