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

package com.algorand.android.modules.asb.importbackup.accountselection.ui.usecase

import com.algorand.android.R
import com.algorand.android.customviews.TriStatesCheckBox
import com.algorand.android.models.AccountCreation
import com.algorand.android.models.AccountIconResource
import com.algorand.android.models.ScreenState
import com.algorand.android.models.ui.AccountAssetItemButtonState.CHECKED
import com.algorand.android.modules.accountcore.ui.usecase.GetAccountDisplayName
import com.algorand.android.modules.accounticon.ui.model.AccountIconDrawablePreview
import com.algorand.android.modules.asb.importbackup.accountrestoreresult.ui.model.AsbImportRestoreResultNavArg
import com.algorand.android.modules.asb.importbackup.accountrestoreresult.ui.usecase.CreateAsbImportedAddresses
import com.algorand.android.modules.asb.importbackup.accountselection.ui.mapper.AsbImportAccountSelectionPreviewMapper
import com.algorand.android.modules.asb.importbackup.accountselection.ui.model.AsbImportAccountSelectionPreview
import com.algorand.android.modules.asb.importbackup.accountselection.utils.AsbAccountImportParser
import com.algorand.android.modules.backupprotocol.model.BackupProtocolElement
import com.algorand.android.modules.basemultipleaccountselection.ui.mapper.MultipleAccountSelectionListItemMapper
import com.algorand.android.modules.basemultipleaccountselection.ui.model.MultipleAccountSelectionListItem
import com.algorand.android.modules.basemultipleaccountselection.ui.usecase.BaseMultipleAccountSelectionPreviewUseCase
import com.algorand.android.usecase.AccountAdditionUseCase
import com.algorand.android.utils.Event
import com.algorand.android.utils.analytics.CreationType
import com.algorand.android.utils.extensions.decodeBase64ToByteArray
import com.algorand.android.utils.toShortenedAddress
import com.algorand.wallet.account.detail.domain.model.AccountType
import com.algorand.wallet.asb.domain.utils.BackupProtocolConstants.ALGO_25_ACCOUNT_TYPE_NAME
import com.algorand.wallet.asb.domain.utils.BackupProtocolConstants.NO_AUTH_ACCOUNT_TYPE_NAME
import com.algorand.wallet.encryption.domain.manager.AESPlatformManager
import javax.inject.Inject
import kotlinx.coroutines.flow.flow

@SuppressWarnings("LongParameterList")
class AsbImportAccountSelectionPreviewUseCase @Inject constructor(
    private val asbImportAccountSelectionPreviewMapper: AsbImportAccountSelectionPreviewMapper,
    private val multipleAccountSelectionListItemMapper: MultipleAccountSelectionListItemMapper,
    private val asbAccountImportParser: AsbAccountImportParser,
    private val accountAdditionUseCase: AccountAdditionUseCase,
    private val getAccountDisplayName: GetAccountDisplayName,
    private val aesPlatformManager: AESPlatformManager,
    private val createAsbImportedAddresses: CreateAsbImportedAddresses,
) : BaseMultipleAccountSelectionPreviewUseCase(multipleAccountSelectionListItemMapper) {
    fun getInitialPreview(): AsbImportAccountSelectionPreview {
        val titleItem = createTitleItem(textResId = R.string.choose_accounts_n_to_restore)
        return asbImportAccountSelectionPreviewMapper.mapToAsbImportAccountSelectionPreview(
            multipleAccountSelectionList = listOf(titleItem),
            isActionButtonEnabled = false,
            actionButtonTextResId = R.string.restore_account,
            isLoadingVisible = true,
            checkedAccountCount = 0,
            unsupportedAccounts = null
        )
    }

    fun updatePreviewAfterHeaderCheckBoxClicked(
        preview: AsbImportAccountSelectionPreview
    ): AsbImportAccountSelectionPreview {
        val currentHeaderCheckBoxState = preview.multipleAccountSelectionList.firstOrNull {
            it is MultipleAccountSelectionListItem.AccountHeaderItem
        } as? MultipleAccountSelectionListItem.AccountHeaderItem

        val newMultipleAccountSelectionList = updateListItemAfterHeaderCheckBoxClicked(
            currentHeaderCheckBoxState = currentHeaderCheckBoxState?.checkboxState,
            multipleAccountSelectionList = preview.multipleAccountSelectionList
        )
        val checkedAccountCount = newMultipleAccountSelectionList.count {
            it is MultipleAccountSelectionListItem.AccountItem && it.accountViewButtonState == CHECKED
        }

        return preview.copy(
            multipleAccountSelectionList = newMultipleAccountSelectionList,
            isActionButtonEnabled = checkedAccountCount > 0,
            checkedAccountCount = checkedAccountCount,
        )
    }

    fun updatePreviewAfterAccountCheckBoxClicked(
        preview: AsbImportAccountSelectionPreview,
        accountAddress: String
    ): AsbImportAccountSelectionPreview {
        val multipleAccountSelectionList = preview.multipleAccountSelectionList
        val newMultipleAccountSelectionList = updateListItemAfterAccountCheckBoxClicked(
            multipleAccountSelectionList = multipleAccountSelectionList,
            accountAddress = accountAddress
        )
        val checkedAccountCount = newMultipleAccountSelectionList.count {
            it is MultipleAccountSelectionListItem.AccountItem && it.accountViewButtonState == CHECKED
        }
        return preview.copy(
            multipleAccountSelectionList = newMultipleAccountSelectionList,
            isActionButtonEnabled = checkedAccountCount > 0,
            checkedAccountCount = checkedAccountCount,
        )
    }

    fun updatePreviewWithRestoredAccounts(
        preview: AsbImportAccountSelectionPreview,
        backupProtocolElements: Array<BackupProtocolElement>
    ) = flow {
        emit(preview.copy(isLoadingVisible = true))
        val selectedAccounts = getSelectedAccountAddressList(preview.multipleAccountSelectionList)
        val accountImportMap = selectedAccounts.mapNotNull { address ->
            address to (backupProtocolElements.firstOrNull { it.address == address } ?: return@mapNotNull null)
        }
        val accountImportResult = asbAccountImportParser.parseAsbImportedAccounts(
            accountImportMap = accountImportMap,
            unsupportedAccounts = preview.unsupportedAccounts
        )

        val importedAddressesAccountCreations = getImportedAddressesAccountCreations(
            accountImportResult.importedAccountList,
            backupProtocolElements
        )
        val asbImportedAddresses = createAsbImportedAddresses(importedAddressesAccountCreations)

        importedAddressesAccountCreations.forEach { accountCreation ->
            accountAdditionUseCase.addNewAccount(accountCreation)
        }

        val restoreResultNavArg = AsbImportRestoreResultNavArg(
            importedAddresses = asbImportedAddresses,
            existingAccountList = accountImportResult.existingAccountList,
            unsupportedAccountList = accountImportResult.unsupportedAccountList
        )
        val successPreview = preview.copy(
            navToRestoreCompleteEvent = Event(restoreResultNavArg),
            isLoadingVisible = false
        )
        emit(successPreview)
    }

    suspend fun getAsbImportAccountSelectionPreview(
        preview: AsbImportAccountSelectionPreview,
        backupProtocolElements: Array<BackupProtocolElement>
    ): AsbImportAccountSelectionPreview {
        val titleItem = createTitleItem(textResId = R.string.choose_accounts_n_to_restore)

        val supportedAccounts = mutableListOf<BackupProtocolElement>()
        val unsupportedAccounts = mutableListOf<BackupProtocolElement>()
        backupProtocolElements.forEach { payload ->
            val isAccountSupported = asbAccountImportParser.isAccountSupported(payload)
            if (isAccountSupported) {
                supportedAccounts.add(payload)
            } else {
                unsupportedAccounts.add(payload)
            }
        }

        if (supportedAccounts.isEmpty()) {
            val emptyScreenState = ScreenState.CustomState(title = R.string.we_couldn_t_find_any_accounts)
            return preview.copy(isLoadingVisible = false, emptyScreenState = emptyScreenState)
        }

        val accountItemList = createAccountItemListByPayload(supportedAccounts)
        if (accountItemList.isEmpty()) {
            val emptyScreenState = ScreenState.CustomState(title = R.string.we_couldn_t_find_any_accounts)
            return preview.copy(isLoadingVisible = false, emptyScreenState = emptyScreenState)
        }

        val accountSize = accountItemList.size
        val accountHeaderItem = createAccountHeaderItem(
            titleRes = R.plurals.account_count,
            accountCount = accountSize,
            checkboxState = TriStatesCheckBox.CheckBoxState.CHECKED
        )
        val multipleAccountSelectionList = mutableListOf<MultipleAccountSelectionListItem>().apply {
            add(titleItem)
            add(accountHeaderItem)
            addAll(accountItemList)
        }
        return preview.copy(
            multipleAccountSelectionList = multipleAccountSelectionList,
            isActionButtonEnabled = true,
            isLoadingVisible = false,
            checkedAccountCount = accountSize,
            unsupportedAccounts = unsupportedAccounts
        )
    }

    private suspend fun createAccountItemListByPayload(
        backupProtocolElements: List<BackupProtocolElement>
    ): List<MultipleAccountSelectionListItem.AccountItem> {
        return backupProtocolElements.mapNotNull { payload ->
            val safeAccountType = getAccountType(payload.accountType) ?: return@mapNotNull null
            // Since these accounts are not saved in local, we have to create [AccountDisplayName] model by using
            // mapper instead of using `AccountDisplayNameUseCase`
            val accountDisplayName = getAccountDisplayName(
                address = payload.address ?: return@mapNotNull null,
                name = payload.name,
                type = safeAccountType
            )
            val accountIconDrawablePreview = getAccountIconDrawable(safeAccountType)
            multipleAccountSelectionListItemMapper.mapToAccountItem(
                accountDisplayName = accountDisplayName,
                accountIconDrawablePreview = accountIconDrawablePreview,
                accountViewButtonState = CHECKED
            )
        }
    }

    private fun getAccountIconDrawable(accountType: AccountType): AccountIconDrawablePreview {
        // Since these account are not in our local, we have to create them manually
        val iconResource = when (accountType) {
            AccountType.NoAuth -> AccountIconResource.WATCH
            AccountType.Algo25 -> AccountIconResource.STANDARD
            AccountType.HdKey -> AccountIconResource.HD
            else -> AccountIconResource.UNDEFINED
        }
        return AccountIconDrawablePreview(
            backgroundColorResId = iconResource.backgroundColorResId,
            iconTintResId = iconResource.iconTintResId,
            iconResId = iconResource.iconResId
        )
    }

    private fun getAccountType(type: String?): AccountType? {
        return when (type) {
            ALGO_25_ACCOUNT_TYPE_NAME -> AccountType.Algo25
            NO_AUTH_ACCOUNT_TYPE_NAME -> AccountType.NoAuth
            else -> null
        }
    }

    private fun getImportedAddressesAccountCreations(
        importedAddresses: List<String>,
        backupProtocolElements: Array<BackupProtocolElement>
    ): List<AccountCreation> {
        return importedAddresses.mapNotNull { address ->
            val importedAddress = backupProtocolElements.firstOrNull { it.address == address } ?: return@mapNotNull null
            val safeAccountAddress = importedAddress.address ?: return@mapNotNull null
            val safeAccountName = importedAddress.name.orEmpty().ifBlank { safeAccountAddress.toShortenedAddress() }
            val accountType = when (importedAddress.accountType) {
                ALGO_25_ACCOUNT_TYPE_NAME -> {
                    val safeAccountPrivateKey = importedAddress.privateKey?.decodeBase64ToByteArray()
                        ?: return@mapNotNull null
                    val encryptedPrivateKey = aesPlatformManager.encryptByteArray(safeAccountPrivateKey)
                    AccountCreation.Type.Algo25(encryptedPrivateKey)
                }
                NO_AUTH_ACCOUNT_TYPE_NAME -> AccountCreation.Type.NoAuth
                else -> return@mapNotNull null
            }
            AccountCreation(
                address = safeAccountAddress,
                customName = safeAccountName,
                isBackedUp = true,
                type = accountType,
                creationType = CreationType.RECOVER
            )
        }
    }
}
