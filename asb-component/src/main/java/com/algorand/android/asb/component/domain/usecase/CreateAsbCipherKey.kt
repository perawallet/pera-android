package com.algorand.android.asb.component.domain.usecase

internal interface CreateAsbCipherKey {
    operator fun invoke(mnemonics: String): ByteArray?
}
