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

package com.algorand.wallet.jointaccount.transaction.domain.model

enum class SignRequestStatus(val value: String) {
    // Waiting statuses
    PENDING("pending"),
    READY("ready"),
    SUBMITTING("submitting"),

    // Finalized statuses
    CONFIRMED("confirmed"),
    FAILED("failed"),
    EXPIRED("expired");

    companion object {
        private val map = entries.associateBy(SignRequestStatus::value)

        fun fromValue(value: String?): SignRequestStatus? = value?.let { map[it] }

        val WAITING_STATUSES = listOf(PENDING, READY, SUBMITTING)
        val FINALIZED_STATUSES = listOf(CONFIRMED, FAILED, EXPIRED)
    }

    fun isWaiting(): Boolean = this in WAITING_STATUSES
    fun isFinalized(): Boolean = this in FINALIZED_STATUSES
}
