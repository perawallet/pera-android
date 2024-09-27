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

package com.algorand.android.modules.asb.createbackup.storekey.ui.usecase

import com.algorand.android.R
import com.algorand.android.models.AnnotatedString
import com.algorand.android.module.account.local.domain.usecase.GetSecretKey
import com.algorand.android.module.asb.backupprotocol.model.BackUpAccount
import com.algorand.android.module.asb.backupprotocol.model.BackUpPayload
import com.algorand.android.module.asb.backupprotocol.usecase.CreateAsbBackUpFilePayload
import com.algorand.android.module.asb.domain.usecase.CreateAsbMnemonic
import com.algorand.android.module.asb.domain.usecase.SetAccountsAsbBackedUp
import com.algorand.android.module.asb.mnemonics.domain.usecase.GetAsbBackUpMnemonics
import com.algorand.android.module.asb.mnemonics.domain.usecase.SetAsbBackUpMnemonics
import com.algorand.android.module.custominfo.domain.usecase.GetCustomInfoOrNull
import com.algorand.android.module.deviceid.domain.usecase.GetSelectedNodeDeviceId
import com.algorand.android.modules.asb.createbackup.storekey.ui.mapper.AsbStoreKeyPreviewMapper
import com.algorand.android.modules.asb.createbackup.storekey.ui.model.AsbStoreKeyPreview
import com.algorand.android.utils.Event
import com.algorand.android.utils.browser.ASB_SUPPORT_URL
import com.algorand.android.utils.joinMnemonics
import com.algorand.android.utils.splitMnemonic
import javax.inject.Inject

class AsbStoreKeyPreviewUseCase @Inject constructor(
    private val getAsbBackUpMnemonics: GetAsbBackUpMnemonics,
    private val createAsbMnemonic: CreateAsbMnemonic,
    private val asbStoreKeyPreviewMapper: AsbStoreKeyPreviewMapper,
    private val setAccountsAsbBackedUp: SetAccountsAsbBackedUp,
    private val setAsbBackUpMnemonics: SetAsbBackUpMnemonics,
    private val createAsbBackUpFilePayload: CreateAsbBackUpFilePayload,
    private val getCustomInfoOrNull: GetCustomInfoOrNull,
    private val getSecretKey: GetSecretKey,
    private val getSelectedNodeDeviceId: GetSelectedNodeDeviceId
) {

    suspend fun saveBackedUpAccountToLocalStorage(accountList: Array<String>) {
        setAccountsAsbBackedUp(accountList.toSet())
    }

    suspend fun updatePreviewAfterCreatingBackupFile(
        preview: AsbStoreKeyPreview?,
        accountList: List<String>
    ): AsbStoreKeyPreview? {
        val accounts = accountList.mapNotNull { address ->
            BackUpAccount(
                address = address,
                name = getCustomInfoOrNull(address)?.customName.orEmpty(),
                secretKey = getSecretKey(address) ?: return@mapNotNull null
            )
        }

        val backUpPayload = BackUpPayload(
            nodeDeviceId = getSelectedNodeDeviceId() ?: return null,
            accounts = accounts,
            mnemonics = preview?.mnemonics.orEmpty()
        )
        val encodedContent = createAsbBackUpFilePayload(backUpPayload) ?: return updatePreviewAfterFailure(preview)
        return preview?.copy(navToBackupReadyEvent = Event(encodedContent))
    }

    suspend fun updatePreviewWithNewCreatedKey(): AsbStoreKeyPreview {
        return createPreviewForFirstBackup()
    }

    fun updatePreviewAfterClickingCreateNewKey(
        preview: AsbStoreKeyPreview?
    ): AsbStoreKeyPreview? {
        return preview?.copy(navToCreateNewKeyConfirmationEvent = Event(Unit))
    }

    fun updatePreviewAfterClickingCreateBackupFile(
        preview: AsbStoreKeyPreview?
    ): AsbStoreKeyPreview? {
        return preview?.copy(navToCreateBackupConfirmationEvent = Event(Unit))
    }

    fun updatePreviewAfterClickingDescriptionUrl(
        preview: AsbStoreKeyPreview?
    ): AsbStoreKeyPreview? {
        return preview?.copy(openUrlEvent = Event(ASB_SUPPORT_URL))
    }

    fun updatePreviewAfterClickingCopyToKeyboard(
        preview: AsbStoreKeyPreview?
    ): AsbStoreKeyPreview? {
        val mnemonics = preview?.mnemonics?.joinMnemonics().orEmpty()
        return preview?.copy(onKeyCopiedEvent = Event(mnemonics))
    }

    suspend fun getAsbStoreKeyPreview(): AsbStoreKeyPreview {
        val backupMnemonics = getAsbBackUpMnemonics()
        return if (backupMnemonics == null) {
            createPreviewForFirstBackup()
        } else {
            createPreviewForSecondBackup(backupMnemonics)
        }
    }

    private suspend fun createPreviewForFirstBackup(): AsbStoreKeyPreview {
        val asbMnemonic = createAsbMnemonic()
        return if (!asbMnemonic.isNullOrBlank()) {
            setAsbBackUpMnemonics(asbMnemonic)
            asbStoreKeyPreviewMapper.mapToAsbStoreKeyPreview(
                titleTextResId = R.string.store_your_n_12_word_key,
                isCreateNewKeyButtonVisible = false,
                descriptionAnnotatedString = AnnotatedString(stringResId = R.string.your_12_word_key_is),
                mnemonics = asbMnemonic.splitMnemonic()
            )
        } else {
            asbStoreKeyPreviewMapper.mapToAsbStoreKeyPreview(
                titleTextResId = R.string.store_your_n_12_word_key,
                isCreateNewKeyButtonVisible = false,
                descriptionAnnotatedString = AnnotatedString(stringResId = R.string.your_12_word_key_is),
                mnemonics = emptyList(),
                navToFailureScreenEvent = Event(Unit)
            )
        }
    }

    private fun createPreviewForSecondBackup(backupMnemonics: String): AsbStoreKeyPreview {
        return asbStoreKeyPreviewMapper.mapToAsbStoreKeyPreview(
            titleTextResId = R.string.review_your_n_12_word_key,
            isCreateNewKeyButtonVisible = true,
            descriptionAnnotatedString = AnnotatedString(stringResId = R.string.you_stored_your_key_during),
            mnemonics = backupMnemonics.splitMnemonic()
        )
    }

    private fun updatePreviewAfterFailure(preview: AsbStoreKeyPreview?): AsbStoreKeyPreview? {
        return preview?.copy(navToFailureScreenEvent = Event(Unit))
    }
}
