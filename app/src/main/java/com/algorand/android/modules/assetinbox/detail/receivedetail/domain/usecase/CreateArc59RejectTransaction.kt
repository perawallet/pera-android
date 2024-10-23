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

package com.algorand.android.modules.assetinbox.detail.receivedetail.domain.usecase

import com.algorand.android.models.Result
import com.algorand.android.modules.assetinbox.detail.receivedetail.domain.model.Arc59RejectTransactionPayload
import com.algorand.android.modules.assetinbox.detail.receivedetail.domain.model.BaseArc59ClaimRejectTransaction

interface CreateArc59RejectTransaction {

    suspend operator fun invoke(payload: Arc59RejectTransactionPayload):
            Result<List<BaseArc59ClaimRejectTransaction.Arc59RejectTransaction>>
}
