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

package com.algorand.android.modules.onboarding.recoverypassphrase.enterpassphrase.ui.usecase

import com.algorand.android.R
import com.algorand.android.customviews.passphraseinput.usecase.PassphraseInputGroupUseCase
import com.algorand.android.customviews.passphraseinput.util.PassphraseInputConfigurationUtil
import com.algorand.android.models.AccountCreation
import com.algorand.android.models.AnnotatedString
import com.algorand.android.models.OnboardingAccountType
import com.algorand.android.modules.accountcore.ui.usecase.GetAccountIconDrawablePreviewByType
import com.algorand.android.modules.accounticon.ui.model.AccountIconDrawablePreview
import com.algorand.android.modules.onboarding.recoverypassphrase.enterpassphrase.ui.mapper.RecoverWithPassphrasePreviewMapper
import com.algorand.android.modules.onboarding.recoverypassphrase.enterpassphrase.ui.model.RecoverWithPassphrasePreview
import com.algorand.android.ui.rekeyedaccounts.model.RekeyedAccountSelectionNavArg
import com.algorand.android.utils.Event
import com.algorand.android.utils.PassphraseKeywordUtils
import com.algorand.android.utils.analytics.CreationType.RECOVER
import com.algorand.android.utils.splitMnemonic
import com.algorand.android.utils.toShortenedAddress
import com.algorand.wallet.account.detail.domain.model.AccountRegistrationType
import com.algorand.wallet.account.detail.domain.model.AccountType
import com.algorand.wallet.account.detail.domain.usecase.GetAccountRegistrationType
import com.algorand.wallet.account.info.domain.usecase.FetchRekeyedAccounts
import com.algorand.wallet.algosdk.transaction.sdk.AlgoAccountSdk
import com.algorand.wallet.algosdk.transaction.sdk.PeraBip39Sdk
import com.algorand.wallet.encryption.domain.manager.AESPlatformManager
import java.util.Locale
import javax.inject.Inject
import kotlinx.coroutines.flow.flow

@Suppress("LongParameterList")
class RecoverWithPassphrasePreviewUseCase @Inject constructor(
    private val recoverWithPassphrasePreviewMapper: RecoverWithPassphrasePreviewMapper,
    private val passphraseInputGroupUseCase: PassphraseInputGroupUseCase,
    private val passphraseInputConfigurationUtil: PassphraseInputConfigurationUtil,
    private val fetchRekeyedAccounts: FetchRekeyedAccounts,
    private val peraBip39Sdk: PeraBip39Sdk,
    private val algoAccountSdk: AlgoAccountSdk,
    private val getAccountRegistrationType: GetAccountRegistrationType,
    private val aesPlatformManager: AESPlatformManager,
    private val getAccountIconDrawablePreviewByType: GetAccountIconDrawablePreviewByType
) {

    fun getRecoverWithPassphraseInitialPreview(
        wordCount: Int
    ): RecoverWithPassphrasePreview {
        val passphraseInputGroupConfiguration = passphraseInputGroupUseCase.createPassphraseInputGroupConfiguration(
            itemCount = wordCount
        )
        return recoverWithPassphrasePreviewMapper.mapToRecoverWithPassphrasePreview(
            passphraseInputGroupConfiguration = passphraseInputGroupConfiguration,
            suggestedWords = emptyList(),
            isRecoveryEnabled = false
        )
    }

    fun updatePreviewAfterPastingClipboardData(
        preview: RecoverWithPassphrasePreview,
        clipboardData: String
    ): RecoverWithPassphrasePreview {
        val splittedText = clipboardData.splitMnemonic()
        return if (
            splittedText.size != OnboardingAccountType.Algo25.wordCount &&
            splittedText.size != OnboardingAccountType.HdKey.wordCount
        ) {
            preview.copy(onGlobalErrorEvent = Event(R.string.the_last_copied_text))
        } else {
            val inputGroupConfiguration = passphraseInputGroupUseCase.recoverPassphraseInputGroupConfiguration(
                configuration = preview.passphraseInputGroupConfiguration,
                itemList = splittedText
            )
            preview.copy(
                suggestedWords = emptyList(),
                passphraseInputGroupConfiguration = inputGroupConfiguration,
                onRestorePassphraseInputGroupEvent = Event(inputGroupConfiguration),
                isRecoveryEnabled = passphraseInputConfigurationUtil.areAllFieldsValid(
                    passphrasesMap = inputGroupConfiguration.passphraseInputConfigurationList
                )
            )
        }
    }

    fun updatePreviewAfterFocusChanged(
        preview: RecoverWithPassphrasePreview,
        focusedItemOrder: Int
    ): RecoverWithPassphrasePreview {
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
            isRecoveryEnabled = passphraseInputConfigurationUtil.areAllFieldsValid(
                passphrasesMap = passphraseInputGroupConfiguration.passphraseInputConfigurationList
            )
        )
    }

    fun updatePreviewAfterFocusedInputChanged(
        preview: RecoverWithPassphrasePreview,
        word: String
    ): RecoverWithPassphrasePreview {
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
            isRecoveryEnabled = passphraseInputConfigurationUtil.areAllFieldsValid(
                passphrasesMap = passphraseInputGroupConfiguration.passphraseInputConfigurationList
            )
        )
    }

    @SuppressWarnings("LongMethod")
    fun validateEnteredMnemonics(
        preview: RecoverWithPassphrasePreview,
        onboardingAccountType: OnboardingAccountType
    ) = flow {
        try {
            emit(preview.copy(showLoadingDialogEvent = Event(Unit)))
            var accountAddress = ""
            var mnemonics = passphraseInputConfigurationUtil.getOrderedInput(preview.passphraseInputGroupConfiguration)
            val recoveredAccount = getAccount(onboardingAccountType, mnemonics, accountAddress) ?: run {
                // Handle the case where account creation fails (e.g., invalid mnemonic)
                val copiedPreview = preview.copy(
                    onAccountNotFoundEvent = Event(AnnotatedString(R.string.account_not_found_please_try))
                )
                emit(copiedPreview)
                return@flow
            }

            if (onboardingAccountType == OnboardingAccountType.Algo25) {
                accountAddress = recoveredAccount.address

                val accountRegistrationType = getAccountRegistrationType(accountAddress)
                if (accountRegistrationType != null) {
                    val isLocalAccountReplaceable = when (accountRegistrationType) {
                        AccountRegistrationType.NoAuth -> true
                        else -> false
                    }
                    if (!isLocalAccountReplaceable) {
                        emit(preview.copy(onGlobalErrorEvent = Event(R.string.this_account_already_exists)))
                        return@flow
                    }
                }

                fetchRekeyedAccounts(accountAddress).use(
                    onSuccess = {
                        val updatedPreview = if (it.isEmpty()) {
                            preview.copy(navToNameRegistrationEvent = Event(recoveredAccount))
                        } else {
                            val rekeyedAccountSelectionNavArg = RekeyedAccountSelectionNavArg(
                                authAddress = accountAddress,
                                authAddressIconDrawablePreview = getAccountIconDrawablePreview(recoveredAccount.type),
                                rekeyedAccountAddresses = it.map { it.address }
                            )
                            val event = Event(recoveredAccount to rekeyedAccountSelectionNavArg)
                            preview.copy(navToImportRekeyedAccountEvent = event)
                        }
                        emit(updatedPreview)
                    },
                    onFailed = { _, _ ->
                        val updatedPreview = preview.copy(
                            navToNameRegistrationEvent = Event(recoveredAccount),
                            showErrorEvent = Event(AnnotatedString(R.string.failed_to_fetch_rekeyed))
                        )
                        emit(updatedPreview)
                    }
                )
            } else {
                val updatedPreview = preview.copy(navToNameRegistrationEvent = Event(recoveredAccount))
                emit(updatedPreview)
            }
        } catch (exception: Exception) {
            emit(preview.copy(onAccountNotFoundEvent = Event(AnnotatedString(R.string.account_not_found_please_try))))
        }
    }

    private fun getAccountIconDrawablePreview(accountType: AccountCreation.Type): AccountIconDrawablePreview {
        return when (accountType) {
            is AccountCreation.Type.Algo25 -> getAccountIconDrawablePreviewByType(AccountType.Algo25)
            is AccountCreation.Type.HdKey -> getAccountIconDrawablePreviewByType(AccountType.HdKey)
            is AccountCreation.Type.LedgerBle -> getAccountIconDrawablePreviewByType(AccountType.LedgerBle)
            AccountCreation.Type.NoAuth -> getAccountIconDrawablePreviewByType(AccountType.NoAuth)
        }
    }

    private fun getAccount(
        accountType: OnboardingAccountType,
        mnemonics: String,
        accountAddress: String
    ): AccountCreation? {
        return when (accountType) {
            OnboardingAccountType.Algo25 -> {
                val algo25account = algoAccountSdk.recoverAlgo25Account(
                    mnemonics.lowercase(Locale.ENGLISH)
                ) ?: return null
                AccountCreation(
                    address = algo25account.address,
                    customName = algo25account.address.toShortenedAddress(),
                    isBackedUp = true,
                    type = AccountCreation.Type.Algo25(aesPlatformManager.encryptByteArray(algo25account.secretKey)),
                    creationType = RECOVER
                )
            }
            OnboardingAccountType.HdKey -> {
                // only entropy is needed for next screen (importing registered addresses)
                val entropy = peraBip39Sdk.getEntropyFromMnemonic(mnemonics) ?: return null
                AccountCreation(
                    address = accountAddress,
                    customName = accountAddress.toShortenedAddress(),
                    isBackedUp = true,
                    type = AccountCreation.Type.HdKey(
                        publicKey = ByteArray(0),
                        encryptedPrivateKey = ByteArray(0),
                        encryptedEntropy = aesPlatformManager.encryptByteArray(entropy),
                        account = 0,
                        change = 0,
                        keyIndex = 0,
                        derivationType = 0
                    ),
                    creationType = RECOVER
                )
            }
        }
    }
}
