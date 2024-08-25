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

package com.algorand.android.core

import android.content.SharedPreferences
import com.algorand.android.models.Account
import com.algorand.android.utils.preference.removeAll
import com.google.crypto.tink.Aead
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow

// DAGGER
class AccountManager(
    private val aead: Aead,
    private val gson: Gson,
    private val sharedPref: SharedPreferences
) {

    val accounts = MutableStateFlow<List<Account>>(listOf())

    fun removeAllData() {
        accounts.value = listOf()
        sharedPref.removeAll()
    }

    fun isThereAnyRegisteredAccount(): Boolean {
        return getAccounts().isEmpty().not()
    }

    fun getAccounts(): List<Account> {
        return accounts.value
    }
}
