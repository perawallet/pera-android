package com.algorand.android.asb.component.mnemonics.domain.usecase

fun interface GetAsbBackUpMnemonics {
    suspend operator fun invoke(): String?
}
