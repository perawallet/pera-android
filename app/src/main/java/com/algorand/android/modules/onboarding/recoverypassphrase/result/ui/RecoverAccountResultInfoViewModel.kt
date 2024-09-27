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

package com.algorand.android.modules.onboarding.recoverypassphrase.result.ui

import androidx.lifecycle.ViewModel
import com.algorand.android.usecase.LockPreferencesUseCase
import com.algorand.android.usecase.RecoverAccountResultInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RecoverAccountResultInfoViewModel @Inject constructor(
    recoverAccountResultInfoUseCase: RecoverAccountResultInfoUseCase,
    private val lockPreferencesUseCase: LockPreferencesUseCase
) : ViewModel() {

    private val recoverAccountResultInfoPreview = recoverAccountResultInfoUseCase.getRecoverAccountResultInfoPreview()

    fun shouldForceLockNavigation(): Boolean {
        return lockPreferencesUseCase.shouldNavigateLockNavigation()
    }

    fun getPreviewTitle(): Int {
        return recoverAccountResultInfoPreview.titleTextRes
    }

    fun getPreviewDescription(): Int {
        return recoverAccountResultInfoPreview.descriptionTextRes
    }

    fun getPreviewFirstButtonText(): Int {
        return recoverAccountResultInfoPreview.firstButtonTextRes
    }

    fun getPreviewSecondButtonText(): Int {
        return recoverAccountResultInfoPreview.secondButtonTextRes
    }
}
