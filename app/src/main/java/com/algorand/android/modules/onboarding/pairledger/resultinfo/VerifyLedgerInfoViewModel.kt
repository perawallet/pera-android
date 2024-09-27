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

package com.algorand.android.modules.onboarding.pairledger.resultinfo

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.algorand.android.usecase.LedgerAccountAdditionResultInfoUseCase
import com.algorand.android.usecase.LockPreferencesUseCase
import com.algorand.android.utils.getOrElse
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class VerifyLedgerInfoViewModel @Inject constructor(
    ledgerAccountAdditionResultInfoUseCase: LedgerAccountAdditionResultInfoUseCase,
    private val lockPreferencesUseCase: LockPreferencesUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val numberOfAccountsAdded = savedStateHandle.getOrElse(NUMBER_OF_ACCOUNTS, 0)

    private val ledgerAccountAdditionResultInfoPreview =
        ledgerAccountAdditionResultInfoUseCase.getLedgerAccountAdditionResultInfoPreview(numberOfAccountsAdded)

    fun shouldForceLockNavigation(): Boolean {
        return lockPreferencesUseCase.shouldNavigateLockNavigation()
    }

    fun getPreviewTitle(): Int {
        return ledgerAccountAdditionResultInfoPreview.titleTextRes
    }

    fun getPreviewDescription(): Int {
        return ledgerAccountAdditionResultInfoPreview.descriptionTextRes
    }

    fun getPreviewFirstButtonText(): Int {
        return ledgerAccountAdditionResultInfoPreview.firstButtonTextRes
    }

    fun getPreviewSecondButtonText(): Int {
        return ledgerAccountAdditionResultInfoPreview.secondButtonTextRes
    }

    companion object {
        private const val NUMBER_OF_ACCOUNTS = "numberOfAccounts"
    }
}
