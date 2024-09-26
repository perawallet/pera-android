package com.algorand.android.module.asb.mnemonics.domain.usecase

fun interface GetAsbBackUpMnemonics {
    suspend operator fun invoke(): String?
}
