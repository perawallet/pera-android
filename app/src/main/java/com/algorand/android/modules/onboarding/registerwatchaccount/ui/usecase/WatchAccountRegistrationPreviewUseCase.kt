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

package com.algorand.android.modules.onboarding.registerwatchaccount.ui.usecase

import com.algorand.android.R
import com.algorand.android.models.CreateAccount
import com.algorand.android.module.account.local.domain.usecase.IsThereAnyAccountWithAddress
import com.algorand.android.modules.nftdomain.domain.usecase.GetNftDomainSearchResultUseCase
import com.algorand.android.modules.onboarding.registerwatchaccount.ui.mapper.BasePasteableWatchAccountItemMapper
import com.algorand.android.modules.onboarding.registerwatchaccount.ui.mapper.WatchAccountRegistrationPreviewMapper
import com.algorand.android.modules.onboarding.registerwatchaccount.ui.model.BasePasteableWatchAccountItem
import com.algorand.android.modules.onboarding.registerwatchaccount.ui.model.WatchAccountRegistrationPreview
import com.algorand.android.utils.Event
import com.algorand.android.utils.analytics.CreationType
import com.algorand.android.utils.isValidAddress
import com.algorand.android.utils.isValidNFTDomain
import com.algorand.android.utils.toShortenedAddress
import javax.inject.Inject
import kotlinx.coroutines.flow.flow

class WatchAccountRegistrationPreviewUseCase @Inject constructor(
    private val getNftDomainSearchResultUseCase: GetNftDomainSearchResultUseCase,
    private val basePasteableWatchAccountItemMapper: BasePasteableWatchAccountItemMapper,
    private val isThereAnyAccountWithAddress: IsThereAnyAccountWithAddress,
    private val watchAccountRegistrationPreviewMapper: WatchAccountRegistrationPreviewMapper
) {

    suspend fun updatePreviewAccordingAccountAddress(
        currentPreview: WatchAccountRegistrationPreview,
        accountAddress: String,
        nfDomainName: String?
    ): WatchAccountRegistrationPreview {
        return when {
            !accountAddress.isValidAddress() -> {
                currentPreview.copy(showAccountIsNotValidErrorEvent = Event(Unit))
            }
            isThereAnyAccountWithAddress(accountAddress) -> {
                currentPreview.copy(showAccountAlreadyExistErrorEvent = Event(Unit))
            }
            else -> {
                val noAuthAccount = CreateAccount.NoAuth(
                    address = accountAddress,
                    customName = nfDomainName,
                    isBackedUp = true,
                    creationType = CreationType.WATCH
                )
                currentPreview.copy(navToNameRegistrationEvent = Event(noAuthAccount))
            }
        }
    }

    fun initWatchAccountRegistrationPreviewFlow(copiedMessage: String, query: String) = flow {
        val nfDomainItemList = createNfDomainItemList(query)
        val accountAddressItem = createAccountAddressItem(copiedMessage)
        val pasteableAccounts = mutableListOf<BasePasteableWatchAccountItem>().apply {
            accountAddressItem?.let { add(it) }
            addAll(nfDomainItemList)
        }
        val preview = watchAccountRegistrationPreviewMapper.mapToWatchAccountRegistrationPreview(
            pasteableAccounts = pasteableAccounts,
            isActionButtonEnabled = query.isValidAddress(),
            errorMessageResId = R.string.account_not_found.takeIf {
                query.isValidNFTDomain() && nfDomainItemList.isEmpty()
            }
        )
        emit(preview)
    }

    private suspend fun createNfDomainItemList(query: String): List<BasePasteableWatchAccountItem.NfDomainItem> {
        return getNftDomainSearchResultUseCase.getNftDomainSearchResults(query).map {
            basePasteableWatchAccountItemMapper.mapToNfDomainItem(
                nfDomainName = it.name,
                nfDomainAccountAddress = it.accountAddress,
                formattedNfDomainAccountAddress = it.accountAddress.toShortenedAddress(),
                nfDomainLogoUrl = it.service?.logoUrl
            )
        }
    }

    private fun createAccountAddressItem(copiedMessage: String): BasePasteableWatchAccountItem.AccountAddressItem? {
        if (!copiedMessage.isValidAddress()) return null
        return basePasteableWatchAccountItemMapper.mapToAccountAddressItem(
            accountAddress = copiedMessage,
            formattedAccountAddress = copiedMessage.toShortenedAddress()
        )
    }
}
