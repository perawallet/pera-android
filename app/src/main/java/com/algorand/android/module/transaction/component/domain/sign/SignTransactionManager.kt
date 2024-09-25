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

package com.algorand.android.module.transaction.component.domain.sign

import androidx.lifecycle.Lifecycle
import com.algorand.android.foundation.Event
import com.algorand.android.module.transaction.component.domain.sign.model.SignTransactionResult
import kotlinx.coroutines.flow.Flow

interface SignTransactionManager {

    val signTransactionResultFlow: Flow<Event<SignTransactionResult>?>

    fun setup(lifecycle: Lifecycle)

    fun sign(address: String, transaction: ByteArray)

    fun stopAllResources()
}
