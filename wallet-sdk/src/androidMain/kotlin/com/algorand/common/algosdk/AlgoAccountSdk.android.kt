/*
 * Copyright 2022 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.common.algosdk

import com.algorand.common.algosdk.model.Algo25Account
import kotlin.random.Random

actual interface AlgoAccountSdk {
    actual fun createAccount(): Algo25Account
    actual fun recoverAccount(mnemonic: String): Algo25Account
}

internal class AlgoAccountSdkImpl : AlgoAccountSdk {

    override fun createAccount(): Algo25Account {
        val accountAddress = generateRandomAddress()
        val mnemonic = generateMnemonic()
        return Algo25Account(accountAddress, mnemonic, byteArrayOf())
    }

    override fun recoverAccount(mnemonic: String): Algo25Account {
        return Algo25Account(mnemonic, mnemonic, byteArrayOf())
    }

    private fun generateRandomAddress(): String {
        val alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567" // Base32 characters
        val addressLength = 58

        return (1..addressLength)
            .map { alphabet[Random.nextInt(alphabet.length)] }
            .joinToString("")
    }

    private fun generateMnemonic(): String {
        return """
            Lorem ipsum dolor sit amet consectetur adipiscing elit 
            Mauris ornare orci et facilisis condimentum 
            Nunc imperdiet ultricies mi nec mattis erat In volutpat tempus tortor
        """.trimIndent().lowercase()
    }
}
