package com.algorand.android.module.asb.domain.usecase

internal interface CreateAsbCipherKey {
    operator fun invoke(mnemonics: String): ByteArray?
}
