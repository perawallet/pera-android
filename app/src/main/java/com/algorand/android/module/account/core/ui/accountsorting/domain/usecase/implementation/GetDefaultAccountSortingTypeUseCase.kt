package com.algorand.android.module.account.core.ui.accountsorting.domain.usecase.implementation

import com.algorand.android.module.account.core.ui.accountsorting.domain.model.AccountSortingType
import com.algorand.android.module.account.core.ui.accountsorting.domain.usecase.GetDefaultAccountSortingType
import com.algorand.android.module.account.sorting.domain.model.AccountSortingTypeIdentifier
import javax.inject.Inject

internal class GetDefaultAccountSortingTypeUseCase @Inject constructor() : GetDefaultAccountSortingType {

    override fun invoke(): AccountSortingType {
        return when (AccountSortingTypeIdentifier.DEFAULT_SORTING_TYPE) {
            AccountSortingTypeIdentifier.MANUAL -> AccountSortingType.ManuallySort
            AccountSortingTypeIdentifier.ALPHABETICALLY_ASCENDING -> AccountSortingType.AlphabeticallyAscending
            AccountSortingTypeIdentifier.ALPHABETICALLY_DESCENDING -> AccountSortingType.AlphabeticallyDescending
            AccountSortingTypeIdentifier.NUMERIC_ASCENDING -> AccountSortingType.NumericalAscendingSort
            AccountSortingTypeIdentifier.NUMERIC_DESCENDING -> AccountSortingType.NumericalDescendingSort
        }
    }
}