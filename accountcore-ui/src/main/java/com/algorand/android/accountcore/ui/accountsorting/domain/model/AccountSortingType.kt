package com.algorand.android.accountcore.ui.accountsorting.domain.model

import com.algorand.android.designsystem.R

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
