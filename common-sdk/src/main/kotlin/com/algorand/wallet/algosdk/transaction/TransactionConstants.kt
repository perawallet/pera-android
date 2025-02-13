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

package com.algorand.wallet.algosdk.transaction

import java.math.BigInteger

object TransactionConstants {
    val MIN_TXN_FEE: BigInteger = BigInteger.valueOf(1_000L)
    val MIN_REQUIRED_BALANCE_PER_ASSET: BigInteger = BigInteger.valueOf(100_000L)
}
