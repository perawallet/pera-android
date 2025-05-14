package com.algorand.android.credentials

import cash.z.ecc.android.bip39.Mnemonics
import cash.z.ecc.android.bip39.toSeed
import foundation.algorand.deterministicP256.DeterministicP256
import foundation.algorand.xhdwalletapi.KeyContext
import foundation.algorand.xhdwalletapi.XHDWalletAPIAndroid
import foundation.algorand.xhdwalletapi.encodeAddress
import java.security.KeyPair
import java.security.MessageDigest
import kotlin.collections.joinToString
import kotlin.text.lowercase
import kotlin.toUInt

const val EXCEPTION_KEY_NOT_FOUND = "Root key was not found"
const val EXCEPTION_KEY_EXISTS = "Root keys already exist"

/**
 * This class is responsible for managing the Algorand keys used in the application.
 * It uses the XHDWallet library to generate deterministic keys and it uses the Algorand SDK
 * to sign transactions and messages.
 *
 * @property xHD The XHDWallet library instance used to generate deterministic keys.
 * @property spendKey The Algorand spend key used to sign transactions.
 * @property rootPasskey The Algorand root passkey used to derive child keys.
 * @property xPasskey The XDH wallet library instance used to derive child keys.
 *
 * @constructor Creates a new instance of the HDKeyManager class.
 */
object KeyManager {
    /**
     * Deterministic key generation and management.
     */
    private var xPasskey = DeterministicP256()

    /**
     * An instance of the `XHDWalletAPIAndroid` which manages HD wallet key generation,
     * signing, and other cryptographic operations for the application.
     */
    private var xHD: XHDWalletAPIAndroid? = null

    /**
     * A byte array that stores the spending key derived from the hierarchical
     * deterministic (HD) wallet.
     */
    private var spendKey: ByteArray? = null

    /**
     * A byte array that stores the root passkey derived from the deterministic p256 library.
     */
    private var rootPasskey: ByteArray? = null

    /**
     * Retrieves the address associated with the current spend key.
     *
     * If the spend key is not set, the method returns null.
     * Otherwise, the associated address is encoded and returned.
     *
     * @return The encoded address as a String if the spend key is set, or null otherwise.
     */
    fun getAddress(): String? {
        if (spendKey === null) return null
        return encodeAddress(spendKey!!)
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
    fun generateCredentialId(keyPair: KeyPair): ByteArray {
        // Get the public key bytes
        val publicKeyBytes = keyPair.public.encoded

        // Compute SHA-256 hash of the public key
        val messageDigest = MessageDigest.getInstance("SHA-256")
        val credentialId = messageDigest.digest(publicKeyBytes)

        return credentialId
    }

    /**
     * Sets the root key for the hierarchical deterministic (HD) wallet instance.
     *
     * This method initializes the wallet using the provided mnemonic seed,
     * generating the extended hierarchical deterministic key (xHD) and
     * associated spending key. If the root or spending key already exists,
     * an exception is thrown to prevent overriding.
     *
     * @param seed The mnemonic seed used to derive the root key and spending key.
     *             Represented as a Mnemonics.MnemonicCode object.
     * @throws Exception if a root or spending key already exists.
     */
    fun setRootKey(seed: Mnemonics.MnemonicCode) {
        if (xHD !== null || spendKey !== null) {
            throw Exception(EXCEPTION_KEY_EXISTS)
        }
        xHD = XHDWalletAPIAndroid(seed.toSeed())
        // Just assume this address path is always for spending
        spendKey = xHD?.keyGen(KeyContext.Address, 0u, 0u, 0u)
        rootPasskey = xPasskey.genDerivedMainKeyWithBIP39(seed.joinToString(" "))
    }

    /**
     * Generates a domain-specific key pair based on the provided origin and user handle.
     *
     * This method utilizes the root passkey and combines it with the specified origin
     * and user handle to create a unique KeyPair for cryptographic operations.
     * It ensures that the generated key pair is tailored for the given domain and user.
     *
     * @param origin A string representing the domain or origin associated with the key pair.
     *               This could represent a website, app, or any other context.
     * @param userHandle A string representing the user's identifier or handle.
     *                   It is used in a case-insensitive manner by converting it to lowercase.
     * @return A KeyPair object containing the generated public and private keys.
     */
    fun generatePasskey(origin: String, userHandle: String): KeyPair {
        return xPasskey.genDomainSpecificKeypair(rootPasskey!!, origin, userHandle.lowercase())
    }

    /**
     * Signs the provided payload using the given domain-specific key pair.
     *
     * This method utilizes the specified `KeyPair` to generate a signature
     * for the provided `payload`. The operation is tailored for domain-specific
     * contexts using the `origin` and `userHandle` parameters.
     *
     * @param keyPair The `KeyPair` object containing the public and private keys used for signing.
     * @param origin A string representing the domain or origin associated with the key pair.
     * @param userHandle A string representing the user's identifier or handle.
     * @param payload A `ByteArray` containing the data to be signed.
     * @return A `ByteArray` representing the cryptographic signature of the payload.
     */
    fun signPasskey(keyPair: KeyPair, origin: String, userHandle: String, payload: ByteArray): ByteArray {
        return xPasskey.signWithDomainSpecificKeyPair(keyPair, payload)
    }

    /**
     * Signs the provided transaction data using the available cryptographic context.
     *
     * This method uses hierarchical deterministic key-based signing to create
     * a digital signature for the given transaction payload. It ensures the transaction
     * payload is authenticated and verifiable within the system's security context.
     *
     * @param txn The transaction payload to be signed, represented as a `ByteArray`.
     * @return A `ByteArray` containing the generated digital signature,
     *         or `null` if signing could not be performed.
     * @throws Exception if the required cryptographic keys are not set.
     */
    fun signTxn(txn: ByteArray): ByteArray? {
        if (spendKey === null || xHD === null) {
            throw Exception(EXCEPTION_KEY_NOT_FOUND)
        }
        return xHD?.signAlgoTransaction(
            KeyContext.Address,
            0u,
            0u,
            0u,
            txn
        )
    }
    // FROM XHD: TODO: resolve upstream signatures
    /**
     * Harden a number (set the highest bit to 1) Note that the input is UInt and the output is also
     * UInt
     *
     * @param num
     * @returns
     * @deprecated
     */
    private fun harden(num: UInt): UInt = 0x80000000.toUInt() + num

    /**
     * Get the BIP44 path from the context, account and keyIndex
     *
     * @param context
     * @param account
     * @param keyIndex
     * @returns
     * @deprecated
     */
    private fun getBIP44PathFromContext(
        context: KeyContext,
        account: UInt,
        change: UInt,
        keyIndex: UInt
    ): List<UInt> {
        return when (context) {
            KeyContext.Address -> listOf(
                harden(44u),
                harden(283u),
                harden(account),
                change,
                keyIndex
            )
            KeyContext.Identity -> listOf(
                harden(44u),
                harden(0u),
                harden(account),
                change,
                keyIndex
            )
        }
    }

    /**
     * @deprecated
     */
    fun rawSign(bytes: ByteArray): ByteArray? {
        return xHD?.rawSign(
            getBIP44PathFromContext(KeyContext.Address, 0u, 0u, 0u),
            bytes,
        )
    }
}
