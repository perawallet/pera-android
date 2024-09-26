package com.algorand.android.module.account.sorting.domain.model

enum class AccountSortingTypeIdentifier {
    MANUAL,
    ALPHABETICALLY_ASCENDING,
    ALPHABETICALLY_DESCENDING,
    NUMERIC_ASCENDING,
    NUMERIC_DESCENDING;

    companion object {
        val DEFAULT_SORTING_TYPE = ALPHABETICALLY_ASCENDING
    }
}
