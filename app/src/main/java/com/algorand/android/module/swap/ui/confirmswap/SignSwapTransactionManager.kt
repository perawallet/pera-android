/*
 *   ~ Copyright 2022 Pera Wallet, LDA
 *   ~ Licensed under the Apache License, Version 2.0 (the "License");
 *   ~ you may not use this file except in compliance with the License.
 *   ~ You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *   ~ Unless required by applicable law or agreed to in writing, software
 *   ~ distributed under the License is distributed on an "AS IS" BASIS,
 *   ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   ~ See the License for the specific language governing permissions and
 *   ~ limitations under the License
 *   -->
 */

package com.algorand.android.module.swap.ui.confirmswap

import androidx.lifecycle.Lifecycle
import com.algorand.android.foundation.Event
import com.algorand.android.module.swap.component.domain.model.swapquotetxns.SwapQuoteTransaction
import com.algorand.android.module.swap.ui.confirmswap.model.SignSwapTransactionResult
import kotlinx.coroutines.flow.Flow

interface SignSwapTransactionManager {

    val signSwapTransactionResultFlow: Flow<Event<SignSwapTransactionResult>?>

    fun setup(lifecycle: Lifecycle)

    suspend fun sign(transactions: List<SwapQuoteTransaction>)

    fun stopAllResources()

    fun clearCachedTransactions()

    fun signCachedTransaction()
}
