package com.algorand.android.asb.component.domain.usecase

internal interface CreateAsbCipherText {
    operator fun invoke(payload: String, mnemonics: List<String>): String?
}
