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

package com.algorand.android.modules.asb.importbackup.enterkey.ui.usecase

import com.algorand.android.R
import com.algorand.android.customviews.passphraseinput.usecase.PassphraseInputGroupUseCase
import com.algorand.android.customviews.passphraseinput.util.PassphraseInputConfigurationUtil
import com.algorand.android.modules.algosdk.backuputils.domain.usecase.CreateBackupCipherKeyUseCase
import com.algorand.android.modules.asb.importbackup.enterkey.ui.mapper.AsbKeyEnterPreviewMapper
import com.algorand.android.modules.asb.importbackup.enterkey.ui.model.AsbKeyEnterPreview
import com.algorand.android.modules.asb.util.AlgorandSecureBackupUtils
import com.algorand.android.modules.backupprotocol.model.BackupProtocolElement
import com.algorand.android.utils.Event
import com.algorand.android.utils.PassphraseKeywordUtils
import com.algorand.android.utils.splitMnemonic
import com.algorand.wallet.asb.domain.model.AsbBackupAccount
import com.algorand.wallet.asb.domain.model.AsbBackupData
import com.algorand.wallet.asb.domain.utils.BackupProtocolConstants.ALGO_25_ACCOUNT_TYPE_NAME
import com.algorand.wallet.asb.domain.utils.BackupProtocolConstants.NO_AUTH_ACCOUNT_TYPE_NAME
import com.algorand.wallet.asb.domain.usecase.RestoreEncryptedBackupProtocolPayload
import javax.inject.Inject
import kotlinx.coroutines.flow.flow

class AsbKeyEnterPreviewUseCase @Inject constructor(
    private val recoverWithPassphrasePreviewMapper: AsbKeyEnterPreviewMapper,
    private val passphraseInputGroupUseCase: PassphraseInputGroupUseCase,
    private val passphraseInputConfigurationUtil: PassphraseInputConfigurationUtil,
    private val createBackupCipherKeyUseCase: CreateBackupCipherKeyUseCase,
    private val restoreEncryptedBackupProtocolPayload: RestoreEncryptedBackupProtocolPayload
) {

    fun getRecoverWithPassphraseInitialPreview(): AsbKeyEnterPreview {
        val passphraseInputGroupConfiguration = passphraseInputGroupUseCase.createPassphraseInputGroupConfiguration(
            itemCount = AlgorandSecureBackupUtils.BACKUP_PASSPHRASES_WORD_COUNT
        )
        return recoverWithPassphrasePreviewMapper.mapToAsbKeyEnterPreview(
            passphraseInputGroupConfiguration = passphraseInputGroupConfiguration,
            suggestedWords = emptyList(),
            isNextButtonEnabled = false
        )
    }

    fun updatePreviewAfterPastingClipboardData(preview: AsbKeyEnterPreview, clipboardData: String): AsbKeyEnterPreview {
        val splintedText = clipboardData.splitMnemonic()
        return if (splintedText.size != AlgorandSecureBackupUtils.BACKUP_PASSPHRASES_WORD_COUNT) {
            val globalErrorPair = R.string.wrong_12_word_key to R.string.please_try_again_by_entering
            preview.copy(onGlobalErrorEvent = Event(globalErrorPair))
        } else {
            val inputGroupConfiguration = passphraseInputGroupUseCase.recoverPassphraseInputGroupConfiguration(
                configuration = preview.passphraseInputGroupConfiguration,
                itemList = splintedText
            )
            preview.copy(
                suggestedWords = emptyList(),
                passphraseInputGroupConfiguration = inputGroupConfiguration,
                onRestorePassphraseInputGroupEvent = Event(inputGroupConfiguration),
                isNextButtonEnabled = passphraseInputConfigurationUtil.areAllFieldsValid(
                    passphrasesMap = inputGroupConfiguration.passphraseInputConfigurationList
                )
            )
        }
    }

    fun updatePreviewAfterFocusChanged(preview: AsbKeyEnterPreview, focusedItemOrder: Int): AsbKeyEnterPreview {
        val passphraseInputGroupConfiguration = passphraseInputGroupUseCase.updatePreviewAfterFocusChanged(
            configuration = preview.passphraseInputGroupConfiguration,
            focusedItemOrder = focusedItemOrder
        ) ?: return preview
        val suggestedWords = PassphraseKeywordUtils.getSuggestedWords(
            wordCount = PassphraseKeywordUtils.SUGGESTED_WORD_COUNT,
            prefix = passphraseInputGroupConfiguration.focusedPassphraseItem?.input.orEmpty()
        )
        return preview.copy(
            suggestedWords = suggestedWords,
            passphraseInputGroupConfiguration = passphraseInputGroupConfiguration,
            isNextButtonEnabled = passphraseInputConfigurationUtil.areAllFieldsValid(
                passphrasesMap = passphraseInputGroupConfiguration.passphraseInputConfigurationList
            )
        )
    }

    fun updatePreviewAfterFocusedInputChanged(preview: AsbKeyEnterPreview, word: String): AsbKeyEnterPreview {
        val suggestedWords = PassphraseKeywordUtils.getSuggestedWords(
            wordCount = PassphraseKeywordUtils.SUGGESTED_WORD_COUNT,
            prefix = word
        )
        val passphraseInputGroupConfiguration = passphraseInputGroupUseCase.updatePreviewAfterFocusedInputChanged(
            configuration = preview.passphraseInputGroupConfiguration,
            word = word
        ) ?: return preview
        return preview.copy(
            suggestedWords = suggestedWords,
            passphraseInputGroupConfiguration = passphraseInputGroupConfiguration,
            isNextButtonEnabled = passphraseInputConfigurationUtil.areAllFieldsValid(
                passphrasesMap = passphraseInputGroupConfiguration.passphraseInputConfigurationList
            )
        )
    }

    suspend fun updatePreviewWithKeyValidation(preview: AsbKeyEnterPreview, cipherText: String) = flow {
        val areAllFieldsValid = passphraseInputConfigurationUtil.areAllFieldsValid(
            passphrasesMap = preview.passphraseInputGroupConfiguration.passphraseInputConfigurationList
        )
        if (!areAllFieldsValid) {
            val globalErrorPair = R.string.wrong_12_word_key to R.string.please_try_again_by_entering
            emit(preview.copy(onGlobalErrorEvent = Event(globalErrorPair)))
            return@flow
        }
        val enteredKey = passphraseInputConfigurationUtil.getOrderedInput(
            configuration = preview.passphraseInputGroupConfiguration
        )
        createBackupCipherKeyUseCase.invoke(enteredKey).useSuspended(
            onSuccess = { cipherKey ->
                val decryptedContent = restoreEncryptedBackupProtocolPayload(
                    cipherText = cipherText,
                    cipherKey = cipherKey
                )
                emit(validateDecryptedPayload(preview, decryptedContent))
            },
            onFailed = {
                val globalErrorPair = R.string.wrong_12_word_key to R.string.please_try_again_by_entering
                emit(preview.copy(onGlobalErrorEvent = Event(globalErrorPair)))
            }
        )
    }

    private fun validateDecryptedPayload(
        preview: AsbKeyEnterPreview,
        asbBackupData: AsbBackupData?
    ): AsbKeyEnterPreview {
        if (asbBackupData?.accounts == null) {
            val globalErrorPair = R.string.wrong_12_word_key to R.string.please_try_again_by_entering
            return preview.copy(onGlobalErrorEvent = Event(globalErrorPair))
        }
        val backupProtocolElements = asbBackupData.mapToBackupProtocolElements()
        return preview.copy(navToAccountSelectionFragmentEvent = Event(backupProtocolElements))
    }

    // TODO Remove this method after the backup protocol is migrated to common-sdk
    private fun AsbBackupData.mapToBackupProtocolElements(): List<BackupProtocolElement> {
        return accounts.orEmpty().map {
            val accountType = when (it.accountType) {
                is AsbBackupAccount.AccountType.Algo25 -> ALGO_25_ACCOUNT_TYPE_NAME
                AsbBackupAccount.AccountType.Watch -> NO_AUTH_ACCOUNT_TYPE_NAME
                else -> null
            }
            BackupProtocolElement(
                address = it.address,
                name = it.name,
                accountType = accountType,
                privateKey = (it.accountType as? AsbBackupAccount.AccountType.Algo25)?.privateKey,
                metadata = it.metadata
            )
        }
    }
}
