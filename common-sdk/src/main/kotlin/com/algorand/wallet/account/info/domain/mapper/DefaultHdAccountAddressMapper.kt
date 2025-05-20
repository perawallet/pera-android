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

package com.algorand.wallet.account.info.domain.mapper

import com.algorand.wallet.account.info.domain.model.AccountFastLookup
import com.algorand.wallet.account.info.domain.model.ActiveHdAccount.HdAccountAddress
import com.algorand.wallet.account.info.domain.model.HdKeyDetail
import javax.inject.Inject

internal class DefaultHdAccountAddressMapper @Inject constructor() : HdAccountAddressMapper {

    override fun invoke(
        hdKeyDetails: List<HdKeyDetail>,
        accountFastLookupBatch: Map<String, AccountFastLookup?>
    ): List<HdAccountAddress> {
        return hdKeyDetails.map { hdKeyDetail ->
            HdAccountAddress(
                address = hdKeyDetail.algoAddress,
                accountIndex = hdKeyDetail.accountIndex,
                changeIndex = hdKeyDetail.changeIndex,
                keyIndex = hdKeyDetail.keyIndex,
                fastLookup = accountFastLookupBatch[hdKeyDetail.algoAddress]
            )
        }
    }
}
