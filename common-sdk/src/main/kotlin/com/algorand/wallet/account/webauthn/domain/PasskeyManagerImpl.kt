package com.algorand.wallet.account.webauthn.domain

import foundation.algorand.deterministicP256.DeterministicP256
import java.security.KeyPair
import java.security.MessageDigest
import javax.inject.Inject

class PasskeyManagerImpl @Inject constructor() : PasskeyManager {
    private var xPasskey = DeterministicP256()
    // TODO: move to parameter
    private val key = xPasskey.genDerivedMainKeyWithBIP39("salon zoo engage submit smile frost later decide wing sight chaos renew lizard rely canal coral scene hobby scare step bus leaf tobacco slice")

    /**
     * Signs the provided payload using the given domain-specific key pair.
     *
     * This method utilizes the specified `KeyPair` to generate a signature
     * for the provided `payload`. The operation is tailored for domain-specific
     * contexts using the `origin` and `userHandle` parameters.
     *
     * @param keyPair The `KeyPair` object containing the public and private keys used for signing.
     * @param payload A `ByteArray` containing the data to be signed.
     * @return A `ByteArray` representing the cryptographic signature of the payload.
     */
    override fun signPasskey(seedId: String, origin: String, userHandle: String, payload: ByteArray): ByteArray {
        return xPasskey.signWithDomainSpecificKeyPair(derivePasskey(seedId, origin, userHandle), payload)
    }
    /**
     * Generates a credential identifier by hashing the public key associated with the given key pair.
     *
     * This method uses the SHA-256 hashing algorithm to compute a unique identifier based on the
     * public key of the provided KeyPair.
     *
     * @param keyPair The KeyPair object containing the public and private keys.
     * @return A ByteArray representing the hashed credential identifier.
     */
    override fun generateCredentialId(keyPair: KeyPair): ByteArray {
        // Get the public key bytes
        val publicKeyBytes = keyPair.public.encoded

        // Compute SHA-256 hash of the public key
        val messageDigest = MessageDigest.getInstance("SHA-256")
        val credentialId = messageDigest.digest(publicKeyBytes)

        return credentialId
    }
    /**
     * Generates a domain-specific key pair based on the provided origin and user handle.
     *
     * This method utilizes the root passkey and combines it with the specified origin
     * and user handle to create a unique KeyPair for cryptographic operations.
     * It ensures that the generated key pair is tailored for the given domain and user.
     *
     * @param seedId A bytearray representing the root seed
     * @param origin A string representing the domain or origin associated with the key pair.
     *               This could represent a website, app, or any other context.
     * @param userHandle A string representing the user's identifier or handle.
     *                   It is used in a case-insensitive manner by converting it to lowercase.
     * @return A KeyPair object containing the generated public and private keys.
     */
    override fun derivePasskey(seedId: String, origin: String, userHandle: String): KeyPair {
        return xPasskey.genDomainSpecificKeypair(key, origin, userHandle.lowercase())
    }
}