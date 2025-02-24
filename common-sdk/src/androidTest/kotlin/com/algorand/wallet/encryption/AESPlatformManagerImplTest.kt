package com.algorand.wallet.encryption

import androidx.test.ext.junit.runners.AndroidJUnit4
import cash.z.ecc.android.bip39.Mnemonics
import com.algorand.algosdk.sdk.Sdk
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class AESPlatformManagerImplTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var aesPlatformManager: AESPlatformManager

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun testEncryptDecryptAlgo25SecretKey_shouldMatchOriginal() {
        // Given
        val originalSecretKey = Sdk.generateSK()

        // When
        val encryptedSecretKey = aesPlatformManager.encryptByteArray(originalSecretKey)
        val decryptedSecretKey = aesPlatformManager.decryptByteArray(encryptedSecretKey)

        // Debugging: Log entropy values
        println("Original Secret Key: ${originalSecretKey.joinToString()}")
        println("Decrypted Secret Key: ${decryptedSecretKey.joinToString()}")

        // Debugging: Verify secret key equality
        assertTrue(originalSecretKey.contentEquals(decryptedSecretKey))
    }

    @Test
    fun testEncryptDecryptEntropy_shouldMatchOriginal() {
        // Given
        val wordCount = Mnemonics.WordCount.COUNT_24
        val originalEntropy = Mnemonics.MnemonicCode(wordCount).toEntropy()

        // When
        val encryptedEntropy = aesPlatformManager.encryptByteArray(originalEntropy)
        val decryptedEntropy = aesPlatformManager.decryptByteArray(encryptedEntropy)

        // Debugging: Log entropy values
        println("Original Entropy: ${originalEntropy.joinToString()}")
        println("Decrypted Entropy: ${decryptedEntropy.joinToString()}")

        // Debugging: Verify entropy equality
        assertTrue(originalEntropy.contentEquals(decryptedEntropy))

        // Generate words from both entropies
        val decryptedWords = Mnemonics.MnemonicCode(decryptedEntropy).words.joinToString(" ")
        val originalWords = Mnemonics.MnemonicCode(originalEntropy).words.joinToString(" ")

        // Verify words match
        assertEquals("Decrypted mnemonic words should match original words",
            originalWords,
            decryptedWords
        )
    }

    @Test
    fun testEncryptDecryptEntropy_withEmptyArray() {
        // Given
        val emptyEntropy = ByteArray(0)

        // When
        val encryptedEntropy = aesPlatformManager.encryptByteArray(emptyEntropy)
        val decryptedEntropy = aesPlatformManager.decryptByteArray(encryptedEntropy)

        // Then
        assertEquals("Decrypted empty entropy should match original empty entropy",
            true,
            emptyEntropy.contentEquals(decryptedEntropy)
        )
    }

    @Test
    fun testEncryptDecryptEntropy_withLargeArray() {
        // Given
        val largeEntropy = ByteArray(1024) { it.toByte() }

        // When
        val encryptedEntropy = aesPlatformManager.encryptByteArray(largeEntropy)
        val decryptedEntropy = aesPlatformManager.decryptByteArray(encryptedEntropy)

        // Then
        assertEquals("Decrypted large entropy should match original large entropy",
            true,
            largeEntropy.contentEquals(decryptedEntropy)
        )
    }
}