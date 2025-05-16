package com.algorand.wallet.account.webauthn.domain

import java.security.KeyPair


interface PasskeyManager {
    fun convertKeyPair(keyPair: KeyPair): KeyPair
    fun signPasskey(keyPair: KeyPair, payload: ByteArray): ByteArray
    fun generateCredentialId(keyPair: KeyPair): ByteArray
    fun derivePasskey(seedId: String, origin: String, userHandle: String): KeyPair
}