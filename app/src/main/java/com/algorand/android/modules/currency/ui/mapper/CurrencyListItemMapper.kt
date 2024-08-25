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

package com.algorand.android.modules.currency.ui.mapper

import com.algorand.android.currency.domain.model.Currency
import com.algorand.android.currency.domain.model.CurrencyOption
import com.algorand.android.ui.settings.selection.CurrencyListItem
import javax.inject.Inject

class CurrencyListItemMapper @Inject constructor() {

    fun mapToCurrencyListItem(currencyOption: CurrencyOption, isSelectedItem: Boolean): CurrencyListItem {
        return CurrencyListItem(
            currencyId = currencyOption.currencyId,
            currencyName = currencyOption.currencyName,
            isSelected = isSelectedItem
        )
    }

    fun createAlgoCurrencyListItem(isSelected: Boolean): CurrencyListItem {
        return CurrencyListItem(
            currencyId = Currency.ALGO.id,
            currencyName = Currency.ALGO.id,
            isSelected = isSelected
        )
    }
}
