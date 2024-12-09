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
import com.algorand.common.algosdk.model.Bip39Account

expect interface AlgoAccountSdk {

    fun createBip39Account(): Bip39Account

    fun recoverBip39Account(mnemonic: String): Bip39Account

    fun createAlgo25Account(): Algo25Account

    fun recoverAlgo25Account(mnemonic: String): Algo25Account
}
