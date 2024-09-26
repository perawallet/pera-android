package com.algorand.android.module.asb.domain.usecase

internal interface CreateAsbCipherText {
    operator fun invoke(payload: String, mnemonics: List<String>): String?
}
