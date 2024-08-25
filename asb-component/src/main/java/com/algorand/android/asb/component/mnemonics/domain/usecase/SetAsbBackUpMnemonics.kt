package com.algorand.android.asb.component.mnemonics.domain.usecase

fun interface SetAsbBackUpMnemonics {
    suspend operator fun invoke(mnemonics: String)
}
