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
 *
 */

package com.algorand.android.usecase

import com.algorand.android.R
import com.algorand.android.SendAlgoNavigationDirections
import com.algorand.android.models.AnnotatedString
import com.algorand.android.models.BaseAccountSelectionListItem
import com.algorand.android.models.Result
import com.algorand.android.models.TargetUser
import com.algorand.android.models.User
import com.algorand.android.modules.accountasset.GetAccountAssetUseCase
import com.algorand.android.modules.accountasset.domain.model.AccountAssetDetail
import com.algorand.android.modules.accountcore.domain.usecase.GetAccountBaseOwnedAssetData
import com.algorand.android.modules.accountcore.ui.accountselection.usecase.GetAccountSelectionAccountItems
import com.algorand.android.modules.accountcore.ui.accountselection.usecase.GetAccountSelectionContactItems
import com.algorand.android.modules.accountcore.ui.accountselection.usecase.GetAccountSelectionItemsFromAccountAddress
import com.algorand.android.modules.accountcore.ui.accountselection.usecase.GetAccountSelectionNameServiceItems
import com.algorand.android.modules.accountcore.ui.usecase.GetAccountIconDrawablePreview
import com.algorand.android.modules.assetinbox.send.summary.ui.model.Arc59SendSummaryNavArgs
import com.algorand.android.ui.send.receiveraccount.ReceiverAccountSelectionFragmentDirections
import com.algorand.android.utils.exceptions.GlobalException
import com.algorand.android.utils.exceptions.NavigationException
import com.algorand.android.utils.exceptions.WarningException
import com.algorand.android.utils.formatAsAlgoString
import com.algorand.android.utils.isValidAddress
import com.algorand.android.utils.validator.AccountTransactionValidator
import com.algorand.wallet.account.detail.domain.model.AccountType.Companion.canSignTransaction
import com.algorand.wallet.account.detail.domain.usecase.GetAccountState
import com.algorand.wallet.account.info.domain.usecase.GetAccountInformation
import com.algorand.wallet.asset.domain.util.AssetConstants.ALGO_ID
import java.math.BigInteger
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow

@Suppress("LongParameterList")
class ReceiverAccountSelectionUseCase @Inject constructor(
    private val contactUseCase: ContactUseCase,
    private val accountTransactionValidator: AccountTransactionValidator,
    private val getAccountBaseOwnedAssetData: GetAccountBaseOwnedAssetData,
    private val getAccountIconDrawablePreview: GetAccountIconDrawablePreview,
    private val getAccountSelectionContactItems: GetAccountSelectionContactItems,
    private val getAccountSelectionNameServiceItems: GetAccountSelectionNameServiceItems,
    private val getAccountSelectionItemsFromAccountAddress: GetAccountSelectionItemsFromAccountAddress,
    private val getAccountSelectionAccountItems: GetAccountSelectionAccountItems,
    private val getAccountInformation: GetAccountInformation,
    private val getAccountState: GetAccountState,
    getAccountAssetUseCase: GetAccountAssetUseCase
) : BaseSendAccountSelectionUseCase(getAccountAssetUseCase) {

    fun getToAccountList(
        query: String,
        latestCopiedMessage: String?
    ): Flow<List<BaseAccountSelectionListItem>> {
        val contactList = fetchContactList(query)
        val accountList = fetchAccountList(query)
        val nftDomainAccountList = fetchNftDomainAccountList(query)
        val queriedAddress = query.takeIf { it.isValidAddress() }
        return combine(
            accountList,
            contactList,
            nftDomainAccountList
        ) { accounts, contacts, nftDomainAccounts ->
            mutableListOf<BaseAccountSelectionListItem>().apply {
                createPasteItem(latestCopiedMessage)?.run { add(this) }
                createQueriedAccountItem(
                    accountAddresses = accounts.map { it.address },
                    contactAddresses = contacts.map { it.address },
                    queriedAddress = queriedAddress
                )?.run {
                    add(BaseAccountSelectionListItem.HeaderItem(R.string.account))
                    add(this)
                }
                if (nftDomainAccounts.isNotEmpty()) {
                    add(BaseAccountSelectionListItem.HeaderItem(R.string.matched_accounts))
                    addAll(nftDomainAccounts)
                }
                if (accounts.isNotEmpty()) {
                    add(BaseAccountSelectionListItem.HeaderItem(R.string.my_accounts))
                    addAll(accounts)
                }
                if (contacts.isNotEmpty()) {
                    add(BaseAccountSelectionListItem.HeaderItem(R.string.contacts))
                    addAll(contacts)
                }
            }
        }
    }

    private fun createPasteItem(latestCopiedMessage: String?): BaseAccountSelectionListItem.PasteItem? {
        return latestCopiedMessage.takeIf { it.isValidAddress() }?.let { copiedAccount ->
            BaseAccountSelectionListItem.PasteItem(copiedAccount)
        }
    }

    private suspend fun createQueriedAccountItem(
        queriedAddress: String?,
        accountAddresses: List<String>,
        contactAddresses: List<String>
    ): BaseAccountSelectionListItem.BaseAccountItem.AccountItem? {
        if (queriedAddress.isNullOrBlank()) return null
        val shouldInsertQueriedAccount = shouldInsertQueriedAccount(
            accountAddresses = accountAddresses,
            contactAddresses = contactAddresses,
            queriedAccount = queriedAddress
        )
        if (!shouldInsertQueriedAccount) return null
        return getAccountSelectionItemsFromAccountAddress(accountAddress = queriedAddress)
    }

    private fun shouldInsertQueriedAccount(
        accountAddresses: List<String>,
        contactAddresses: List<String>,
        queriedAccount: String?
    ): Boolean {
        return !accountAddresses.contains(queriedAccount) && !contactAddresses.contains(queriedAccount)
    }

    private fun fetchContactList(query: String) = flow {
        val contacts = getAccountSelectionContactItems().filter {
            it.displayName.contains(query, true) || it.address.contains(query, true)
        }
        emit(contacts)
    }

    private fun fetchNftDomainAccountList(query: String) = flow {
        val nftDomainAccounts = getAccountSelectionNameServiceItems(query)
        emit(nftDomainAccounts)
    }

    private fun fetchAccountList(query: String) = flow {
        val localAccounts = getAccountSelectionAccountItems(
            showHoldings = false,
            showFailedAccounts = false
        ).filter { it.displayName.contains(query, true) || it.address.contains(query, true) }
        emit(localAccounts)
    }

    fun isAccountAddressValid(toAccountPublicKey: String): Result<String> {
        return accountTransactionValidator.isAccountAddressValid(toAccountPublicKey)
    }

    @SuppressWarnings("ReturnCount", "LongMethod")
    suspend fun checkToAccountTransactionRequirements(
        accountAssetDetail: AccountAssetDetail,
        assetId: Long,
        fromAccountAddress: String,
        amount: BigInteger,
        nftDomainAddress: String?,
        nftDomainServiceLogoUrl: String?
    ): Result<TargetUser> {
        val isSelectedAssetValid = accountTransactionValidator.isSelectedAssetValid(fromAccountAddress, assetId)
        val receiverAccountType = getAccountState(accountAssetDetail.address).accountType
        val isReceiverAccountInMyWallet = receiverAccountType?.canSignTransaction() == true
        if (!isSelectedAssetValid) {
            // TODO: 18.03.2022 Find better exception message
            return Result.Error(Exception())
        }

        if (accountAssetDetail.assetDetail == null && assetId != ALGO_ID && !isReceiverAccountInMyWallet) {
            val nextDirection = ReceiverAccountSelectionFragmentDirections
                .actionReceiverAccountSelectionFragmentToArc59RequestOptInNavigation(
                    Arc59SendSummaryNavArgs(
                        fromAccountAddress,
                        accountAssetDetail.address,
                        assetId,
                        amount
                    )
                )
            return Result.Error(NavigationException(nextDirection))
        }

        if (assetId == ALGO_ID) {
            val minBalance = accountAssetDetail.minBalanceRequired
            val toAccountAlgoBalance = accountAssetDetail.algoAmount
            val isSendingAmountValid = accountTransactionValidator.isSendingAmountLesserThanMinimumBalance(
                toAccountAlgoBalance,
                amount,
                minBalance
            )
            if (isSendingAmountValid) {
                val warningBodyMessage = AnnotatedString(
                    R.string.you_re_trying_to_send,
                    listOf("amount" to (minBalance - toAccountAlgoBalance).formatAsAlgoString())
                )
                return Result.Error(WarningException(R.string.warning, warningBodyMessage))
            }
        }

        val ownedAssetData = getAccountBaseOwnedAssetData(fromAccountAddress, assetId)
        val isSendingMaxAmountToSameAccount = accountTransactionValidator.isSendingMaxAmountToTheSameAccount(
            fromAccount = fromAccountAddress,
            toAccount = accountAssetDetail.address,
            maxAmount = ownedAssetData?.amount ?: BigInteger.ZERO,
            amount = amount,
            isAlgo = ownedAssetData?.isAlgo ?: false
        )

        if (isSendingMaxAmountToSameAccount) {
            return Result.Error(GlobalException(descriptionRes = R.string.you_can_not_send_your))
        }

        val isCloseTransactionToSameAccount = accountTransactionValidator.isCloseTransactionToSameAccount(
            getAccountInformation(fromAccountAddress),
            accountAssetDetail.address,
            ownedAssetData,
            amount
        )

        if (isCloseTransactionToSameAccount) {
            return Result.Error(GlobalException(descriptionRes = R.string.you_can_not_send_your))
        }

        val isAccountNewlyOpenedAndBalanceInvalid = accountTransactionValidator.isAccountNewlyOpenedAndBalanceInvalid(
            accountAssetDetail,
            amount,
            assetId
        )
        if (isAccountNewlyOpenedAndBalanceInvalid) {
            // TODO: 18.02.2022 Move all navigation logic into the presentation layer
            return Result.Error(
                NavigationException(
                    SendAlgoNavigationDirections.actionGlobalSingleButtonBottomSheet(
                        titleAnnotatedString = AnnotatedString(R.string.minimum_amount_required),
                        descriptionAnnotatedString = AnnotatedString(R.string.this_is_the_first_transaction),
                        buttonStringResId = R.string.i_understand,
                        drawableResId = R.drawable.ic_info,
                        drawableTintResId = R.color.error_tint_color
                    )
                )
            )
        }

        val toAccountPublicKey = accountAssetDetail.address
        val contact = getContactByAddressIfExists(toAccountPublicKey)
        // TODO Will be implemented after transaction migration
        val targetUser = TargetUser(
            contact = contact,
            publicKey = toAccountPublicKey,
            algoBalance = accountAssetDetail.algoAmount,
            minBalance = accountAssetDetail.minBalanceRequired,
            nftDomainAddress = nftDomainAddress,
            nftDomainServiceLogoUrl = nftDomainServiceLogoUrl,
            accountIconDrawablePreview = getAccountIconDrawablePreview(toAccountPublicKey)
        )
        return Result.Success(targetUser)
    }

    private suspend fun getContactByAddressIfExists(accountAddress: String): User? {
        return contactUseCase.getAllContacts().firstOrNull { it.publicKey == accountAddress }
    }
}
