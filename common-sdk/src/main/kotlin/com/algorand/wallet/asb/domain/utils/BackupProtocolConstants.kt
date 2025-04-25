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

package com.algorand.wallet.asb.domain.utils

object BackupProtocolConstants {

    const val ALGO_25_ACCOUNT_TYPE_NAME = "single"
    const val NO_AUTH_ACCOUNT_TYPE_NAME = "watch"

    fun isAccountTypeEligible(accountType: String): Boolean {
        return accountType == ALGO_25_ACCOUNT_TYPE_NAME || accountType == NO_AUTH_ACCOUNT_TYPE_NAME
    }
}
