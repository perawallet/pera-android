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

package com.algorand.android.models.ui

import com.algorand.android.models.CreateAccount
import com.algorand.android.utils.Event

data class NameRegistrationPreview(
    val handleNextNavigationEvent: Event<Unit?>?,
    private val accountAlreadyExistsEvent: Event<Unit?>?,
    private val createAccountEvent: Event<CreateAccount>?,
    private val updateWatchAccountEvent: Event<CreateAccount>?
) {
    fun getAccountAlreadyExistsEvent(): Event<Unit?>? {
        if (accountAlreadyExistsEvent?.consumed == false) {
            createAccountEvent?.consume()
            updateWatchAccountEvent?.consume()
        }
        return accountAlreadyExistsEvent
    }

    fun getCreateAccountEvent(): Event<CreateAccount>? {
        if (createAccountEvent?.consumed == false) {
            accountAlreadyExistsEvent?.consume()
            updateWatchAccountEvent?.consume()
        }
        return createAccountEvent
    }

    fun getUpdateWatchAccountEvent(): Event<CreateAccount>? {
        if (updateWatchAccountEvent?.consumed == false) {
            accountAlreadyExistsEvent?.consume()
            createAccountEvent?.consume()
        }
        return updateWatchAccountEvent
    }
}
