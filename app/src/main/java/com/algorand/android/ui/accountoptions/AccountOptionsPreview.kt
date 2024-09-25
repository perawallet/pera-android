package com.algorand.android.ui.accountoptions

import com.algorand.android.module.account.core.ui.model.AccountDisplayName

data class AccountOptionsPreview(
    val accountAddress: String,
    val accountDisplayName: AccountDisplayName,
    val authAddress: String?,
    val authAccountDisplayName: AccountDisplayName?,
    val isAuthAddressButtonVisible: Boolean,
    val isPassphraseButtonVisible: Boolean,
    val isUndoRekeyButtonVisible: Boolean,
    val canSignTransaction: Boolean
)
