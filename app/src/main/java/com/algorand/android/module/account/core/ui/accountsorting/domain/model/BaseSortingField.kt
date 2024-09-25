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

package com.algorand.android.module.account.core.ui.accountsorting.domain.model

import java.math.BigDecimal

sealed interface BaseSortingField {

    interface ManualSorting : BaseSortingField

    interface AlphabeticSortingField : BaseSortingField {
        val alphabeticSortingField: String?
    }

    interface NumericSortingField : BaseSortingField {
        // Number is not comparable type
        val numericSortingField: BigDecimal?
    }
}
