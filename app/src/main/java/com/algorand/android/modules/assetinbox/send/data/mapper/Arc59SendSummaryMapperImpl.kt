/*
 *  Copyright 2022 Pera Wallet, LDA
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License
 */

package com.algorand.android.modules.assetinbox.send.data.mapper

import com.algorand.android.modules.assetinbox.send.data.model.Arc59SendSummaryResponse
import com.algorand.android.modules.assetinbox.send.domain.model.Arc59SendSummary
import java.math.BigInteger
import javax.inject.Inject

class Arc59SendSummaryMapperImpl @Inject constructor() : Arc59SendSummaryMapper {

    override fun invoke(response: Arc59SendSummaryResponse?): Arc59SendSummary? {
        if (response == null || !response.isValid()) return null
        return Arc59SendSummary(
            isArc59OptedIn = response.isArc59OptedIn ?: false,
            minimumBalanceRequirement = response.minimumBalanceRequirement?.toBigInteger() ?: BigInteger.ZERO,
            innerTxCount = response.innerTxCount ?: 0,
            totalProtocolAndMbrFee = response.totalProtocolAndMbrFee ?: BigInteger.ZERO,
            inboxAddress = response.inboxAddress.orEmpty(),
            algoFundAmount = response.algoFundAmount ?: BigInteger.ZERO
        )
    }

    // TODO Check api response make nullable fields optional
    private fun Arc59SendSummaryResponse.isValid(): Boolean {
        return isArc59OptedIn != null && minimumBalanceRequirement != null &&
                innerTxCount != null && totalProtocolAndMbrFee != null
    }
}
