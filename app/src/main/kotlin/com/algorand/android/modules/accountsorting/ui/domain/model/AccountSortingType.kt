/*
 * Copyright 2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.android.modules.accountsorting.ui.domain.model

import com.algorand.android.R

sealed interface AccountSortingType {

    val textResId: Int

    data object ManuallySort : AccountSortingType {
        override val textResId: Int = R.string.manually
    }

    data object AlphabeticallyAscending : AccountSortingType {
        override val textResId: Int = R.string.alphabetically_a_to_z
    }

    data object AlphabeticallyDescending : AccountSortingType {
        override val textResId: Int = R.string.alphabetically_z_to_a
    }

    data object NumericalAscendingSort : AccountSortingType {
        override val textResId: Int = R.string.lowest_value_to_highest
    }

    data object NumericalDescendingSort : AccountSortingType {
        override val textResId: Int = R.string.highest_value_to_lowest
    }
}
