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

package com.algorand.android.utils

import kotlin.math.max

const val MIN_BALANCE_TO_KEEP_PER_OPTED_IN_APPS = 100000

private const val REKEY_BYTE_ARRAY_SIZE = 30

fun calculateRekeyFee(fee: Long, minFee: Long?): Long {
    return max(REKEY_BYTE_ARRAY_SIZE * fee, minFee ?: MIN_FEE)
}
