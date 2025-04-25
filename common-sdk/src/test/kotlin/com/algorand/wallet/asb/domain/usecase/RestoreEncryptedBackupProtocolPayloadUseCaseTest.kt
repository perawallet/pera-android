/*
 * Copyright 2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.wallet.asb.domain.usecase

import com.algorand.test.peraFixture
import com.algorand.wallet.asb.algosdk.AlgorandSdkEncryptionUtils
import com.algorand.wallet.asb.domain.mapper.AsbBackupDataMapper
import com.algorand.wallet.asb.domain.model.AsbBackupData
import com.algorand.wallet.asb.domain.model.BackupProtocolElement
import com.algorand.wallet.asb.domain.model.BackupProtocolPayload
import com.algorand.wallet.foundation.json.JsonSerializer
import com.algorand.wallet.foundation.json.JsonSerializerImpl
import com.google.gson.Gson
import io.mockk.every
import io.mockk.mockk
import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class RestoreEncryptedBackupProtocolPayloadUseCaseTest {

    private val jsonSerializer: JsonSerializer = JsonSerializerImpl(Gson())
    private val algorandSdkEncryptionUtils: AlgorandSdkEncryptionUtils = mockk()
    private val asbBackupDataMapper: AsbBackupDataMapper = mockk()

    private val sut = RestoreEncryptedBackupProtocolPayloadUseCase(
        jsonSerializer,
        algorandSdkEncryptionUtils,
        asbBackupDataMapper
    )

    @Test
    fun `EXPECT null WHEN decrypted content is null`() {
        every { algorandSdkEncryptionUtils.decryptContent(CIPHER_TEXT, CIPHER_KEY) } returns null

        val result = sut(CIPHER_TEXT, CIPHER_KEY)

        assertNull(result)
    }

    @Test
    fun `EXPECT null WHEN decrypted payload is null`() {
        every { algorandSdkEncryptionUtils.decryptContent(CIPHER_TEXT, CIPHER_KEY) } returns null

        val result = sut(CIPHER_TEXT, CIPHER_KEY)

        assertNull(result)
    }

    @Test
    fun `EXPECT AsbBackupData WHEN content and payload is valid`() {
        every { algorandSdkEncryptionUtils.decryptContent(CIPHER_TEXT, CIPHER_KEY) } returns DECRYPTED_CONTENT
        every { asbBackupDataMapper(BACKUP_PAYLOAD) } returns ASB_BACKUP_DATA

        val result = sut(CIPHER_TEXT, CIPHER_KEY)

        assertEquals(ASB_BACKUP_DATA, result)
    }

    private companion object {
        const val CIPHER_TEXT = "cipher_text"
        val CIPHER_KEY = byteArrayOf(1, 2, 3)
        val ASB_BACKUP_DATA = peraFixture<AsbBackupData?>()
        val BACKUP_PAYLOAD = BackupProtocolPayload(
            deviceId = "123456",
            providerName = "Pera Wallet",
            accounts = listOf(
                BackupProtocolElement(
                    address = "EGRJQ7DXMIJ577UUN6AFOIUZY6CNSFKLMGFHQNTC5US5TRC23LK6DGQRDM",
                    accountType = "single",
                    name = "EGRJ...QRDM",
                    privateKey = "abcde",
                    metadata = null
                ),
                BackupProtocolElement(
                    address = "7TTLR5VQAY5YVQ5QV4IBOVIKUULGVNPURNWM5NG7M7ELEOQPVROA4CS3FM",
                    accountType = "watch",
                    name = "7TTL...S3FM",
                    privateKey = "",
                    metadata = null
                ),
                BackupProtocolElement(
                    address = "PKJCHYGFJ4OLOW32THABNCYRO3IT67NS7GRCMNHHBJMZ3VQHYCAYA525LI",
                    accountType = "single",
                    name = "PKJC...25LI",
                    privateKey = "abcde",
                    metadata = null
                )
            )
        )

        val FILE_PATH = "./src/test/resources/wallet/asb/domain/usecase"
        val DECRYPTED_CONTENT = File("${FILE_PATH}/decrypted_content.json").readText()
    }
}
