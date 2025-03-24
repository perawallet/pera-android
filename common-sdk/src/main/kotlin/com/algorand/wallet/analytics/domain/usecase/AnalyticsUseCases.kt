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

package com.algorand.wallet.analytics.domain.usecase

import com.algorand.wallet.analytics.domain.model.FirebaseTokenStatus
import com.algorand.wallet.analytics.domain.model.ReferrerData
import kotlinx.coroutines.flow.Flow

fun interface GetReferrerData {
    suspend operator fun invoke(): ReferrerData
}

fun interface SaveReferrerData {
    suspend operator fun invoke(referrerData: ReferrerData)
}

fun interface GetFirebaseTokenStatusFlow {
    operator fun invoke(): Flow<FirebaseTokenStatus>
}

fun interface IsStrongBoxUsedForEncryption {
    suspend operator fun invoke(): Boolean
}
