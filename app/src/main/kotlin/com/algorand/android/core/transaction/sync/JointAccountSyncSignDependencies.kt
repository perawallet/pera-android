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

package com.algorand.android.core.transaction.sync

import android.content.Context
import com.algorand.android.core.transaction.JointAccountTransactionSignHelper
import com.algorand.wallet.deviceregistration.domain.usecase.GetSelectedNodeDeviceId
import com.algorand.wallet.jointaccount.transaction.domain.MultisigTransactionAssembler
import com.algorand.wallet.jointaccount.transaction.domain.usecase.MarkSignRequestsConfirmed

data class JointAccountSyncSignDependencies(
    val jointAccountTransactionSignHelper: JointAccountTransactionSignHelper,
    val signArbitraryDataForSyncRequest: SignArbitraryDataForSyncRequest,
    val syncSignRequestPollingManager: SyncSignRequestPollingManager,
    val multisigTransactionAssembler: MultisigTransactionAssembler,
    val getSelectedNodeDeviceId: GetSelectedNodeDeviceId,
    val applicationContext: Context,
    val syncSignResultHolder: SyncSignResultHolder,
    val markSignRequestsConfirmed: MarkSignRequestsConfirmed
)
