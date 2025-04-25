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

package com.algorand.android.modules.firebase.token.usecase

import com.algorand.android.modules.firebase.token.FirebaseTokenManager
import com.algorand.android.modules.firebase.token.model.FirebaseTokenResult
import com.algorand.wallet.analytics.domain.model.FirebaseTokenStatus
import com.algorand.wallet.analytics.domain.usecase.GetFirebaseTokenStatusFlow
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetFirebaseTokenStatusFlowUseCase @Inject constructor(
    private val firebaseTokenManager: FirebaseTokenManager
) : GetFirebaseTokenStatusFlow {

    override fun invoke(): Flow<FirebaseTokenStatus> {
        return firebaseTokenManager.firebaseTokenResultFlow.map {
            mapToTokenStatus(it)
        }
    }

    private fun mapToTokenStatus(firebaseTokenResult: FirebaseTokenResult): FirebaseTokenStatus {
        return when (firebaseTokenResult) {
            FirebaseTokenResult.TokenFailed -> FirebaseTokenStatus.Error
            FirebaseTokenResult.TokenLoaded -> FirebaseTokenStatus.Success
            FirebaseTokenResult.TokenLoading -> FirebaseTokenStatus.Loading
        }
    }
}
