package com.algorand.android.module.asb.mnemonics.domain.usecase

fun interface SetAsbBackUpMnemonics {
    suspend operator fun invoke(mnemonics: String)
}
