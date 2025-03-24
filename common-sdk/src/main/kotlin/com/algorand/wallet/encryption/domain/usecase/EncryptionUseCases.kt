package com.algorand.wallet.encryption.domain.usecase

import javax.crypto.SecretKey

fun interface GetEncryptionSecretKey {
    operator fun invoke(): SecretKey
}
