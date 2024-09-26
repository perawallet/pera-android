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
import com.algorand.android.module.asb.backupprotocol.model.BackUpAccount
import com.algorand.android.module.asb.restorebackup.domain.model.RestoreBackUpPayloadResult.Success
import com.algorand.android.module.asb.restorebackup.domain.model.RestoreBackUpPayloadResult.UnableToCreateCipherKey
import com.algorand.android.module.asb.restorebackup.domain.model.RestoreBackUpPayloadResult.UnableToRestoreAccounts
import com.algorand.android.module.asb.restorebackup.domain.usecase.RestoreBackUpPayload
import com.algorand.android.module.asb.utils.AsbBackUpConstants
import com.algorand.android.customviews.passphraseinput.usecase.PassphraseInputGroupUseCase
import com.algorand.android.customviews.passphraseinput.util.PassphraseInputConfigurationUtil
import com.algorand.android.modules.asb.importbackup.enterkey.ui.mapper.AsbKeyEnterPreviewMapper
import com.algorand.android.modules.asb.importbackup.enterkey.ui.model.AsbKeyEnterPreview
import com.algorand.android.modules.asb.importbackup.enterkey.ui.model.RestoredAccount
import com.algorand.android.utils.Event
import com.algorand.android.utils.PassphraseKeywordUtils
import com.algorand.android.utils.splitMnemonic
import javax.inject.Inject
import kotlinx.coroutines.flow.flow

class AsbKeyEnterPreviewUseCase @Inject constructor(
    private val recoverWithPassphrasePreviewMapper: AsbKeyEnterPreviewMapper,
    private val passphraseInputGroupUseCase: PassphraseInputGroupUseCase,
    private val passphraseInputConfigurationUtil: PassphraseInputConfigurationUtil,
    private val restoreBackUpPayload: RestoreBackUpPayload
) {

    fun getRecoverWithPassphraseInitialPreview(): AsbKeyEnterPreview {
        val passphraseInputGroupConfiguration = passphraseInputGroupUseCase.createPassphraseInputGroupConfiguration(
            itemCount = AsbBackUpConstants.BACKUP_PASSPHRASES_WORD_COUNT
        )
        return recoverWithPassphrasePreviewMapper.mapToAsbKeyEnterPreview(
            passphraseInputGroupConfiguration = passphraseInputGroupConfiguration,
            suggestedWords = emptyList(),
            isNextButtonEnabled = false
        )
    }

    fun updatePreviewAfterPastingClipboardData(preview: AsbKeyEnterPreview, clipboardData: String): AsbKeyEnterPreview {
        val splintedText = clipboardData.splitMnemonic()
        return if (splintedText.size != AsbBackUpConstants.BACKUP_PASSPHRASES_WORD_COUNT) {
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

        val updatedPreview = when (val result = restoreBackUpPayload(enteredKey, cipherText)) {
            is Success -> preview.copy(navToAccountSelectionFragmentEvent = Event(getRestoredAccounts(result.accounts)))
            UnableToCreateCipherKey, UnableToRestoreAccounts -> {
                val globalErrorPair = R.string.wrong_12_word_key to R.string.please_try_again_by_entering
                preview.copy(onGlobalErrorEvent = Event(globalErrorPair))
            }
        }
        emit(updatedPreview)
    }

    private fun getRestoredAccounts(accounts: List<BackUpAccount>): List<RestoredAccount> {
        return accounts.map {
            RestoredAccount(
                address = it.address,
                name = it.name,
                secretKey = it.secretKey
            )
        }
    }
}
