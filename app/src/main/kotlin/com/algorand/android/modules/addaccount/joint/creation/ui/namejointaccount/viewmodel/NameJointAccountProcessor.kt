/*
 * Copyright 2022-2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.android.modules.addaccount.joint.creation.ui.namejointaccount.viewmodel

interface NameJointAccountProcessor {

    fun mapExceptionToErrorResId(exception: Throwable?): Int

    suspend fun createLocalAccount(
        jointAccountAddress: String,
        participantAddresses: List<String>,
        threshold: Int,
        version: Int,
        accountName: String
    ): CreateLocalAccountResult

    sealed interface CreateLocalAccountResult {
        data object Success : CreateLocalAccountResult
        data object AlreadyExists : CreateLocalAccountResult
        data class Error(val messageResId: Int) : CreateLocalAccountResult
    }
}
