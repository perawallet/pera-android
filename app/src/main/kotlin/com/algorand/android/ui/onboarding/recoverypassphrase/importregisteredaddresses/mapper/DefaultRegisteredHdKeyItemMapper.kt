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

package com.algorand.android.ui.onboarding.recoverypassphrase.importregisteredaddresses.mapper

import com.algorand.android.ui.onboarding.recoverypassphrase.importregisteredaddresses.model.RegisteredHdKeyItem
import com.algorand.android.utils.formatAsTwoDecimals
import com.algorand.wallet.account.info.domain.model.RegisteredHdKey
import java.math.BigDecimal
import javax.inject.Inject

internal class DefaultRegisteredHdKeyItemMapper @Inject constructor() : RegisteredHdKeyItemMapper {

    override fun invoke(
        hdKey: RegisteredHdKey,
        usdToSelectedCurrencyMultiplier: BigDecimal,
        selectedCurrencySymbol: String
    ): RegisteredHdKeyItem {
        return with(hdKey) {
            RegisteredHdKeyItem(
                address = address,
                algoValue = algoValue,
                formattedSelectedCurrencyValue = usdValue.formatAsSelectedCurrency(
                    usdToSelectedCurrencyMultiplier,
                    selectedCurrencySymbol
                ),
                accountExists = accountExists,
                isImportedToDB = isImportedToDB,
                account = account,
                change = change,
                keyIndex = keyIndex
            )
        }
    }

    private fun BigDecimal.formatAsSelectedCurrency(
        usdToSelectedCurrencyMultiplier: BigDecimal,
        selectedCurrencySymbol: String
    ): String {
        val formattedSelectedCurrencyValue = multiply(usdToSelectedCurrencyMultiplier).formatAsTwoDecimals()
        return StringBuilder(selectedCurrencySymbol)
            .append(formattedSelectedCurrencyValue)
            .toString()
    }
}
