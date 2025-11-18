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

package com.algorand.android.migration.domain.usecase

import com.algorand.wallet.foundation.PeraResult

fun interface MigrateTo6x {
    suspend operator fun invoke(): PeraResult<Int>
}

fun interface GetMigratedTo6xCheck {
    suspend operator fun invoke(): Boolean
}

fun interface SaveMigratedTo6xCheck {
    suspend operator fun invoke(check: Boolean)
}

fun interface IsSecretKeyValidatedForMigratedAccounts {
    suspend operator fun invoke(): Boolean
}

fun interface SetSecretKeyValidatedForMigratedAccounts {
    suspend operator fun invoke()
}
