package com.algorand.android.modules.onboarding.recoverypassphrase.importaddresses.model

import com.algorand.android.customviews.passphraseinput.model.PassphraseInputGroupConfiguration
import com.algorand.android.models.AccountCreation
import com.algorand.android.models.AnnotatedString
import com.algorand.android.utils.Event

data class RecoverRegisteredAccountsPreview(
    val showLoadingDialogEvent: Event<Unit>?,
    val onGlobalErrorEvent: Event<Int>?,
    val onRestorePassphraseInputGroupEvent: Event<PassphraseInputGroupConfiguration>?,
    val onDisplayWrongMnemonicEvent: Event<AnnotatedString>?,
    val navToNameRegistrationEvent: Event<AccountCreation>?,
    val onAccountNotFoundEvent: Event<AnnotatedString>?,
    val navToImportRekeyedAccountEvent: Event<Pair<AccountCreation, List<String>>>?,
    val showErrorEvent: Event<AnnotatedString>?
)
