package com.algorand.wallet.account.webauthn.domain

import java.security.KeyPair


interface PasskeyManager {
    fun signPasskey(seedId: String, origin: String, userHandle: String, payload: ByteArray): ByteArray
    fun generateCredentialId(keyPair: KeyPair): ByteArray
    fun derivePasskey(seedId: String, origin: String, userHandle: String): KeyPair
}