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

package com.algorand.android.transactionhistoryui.mapper

import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.LoadState.Loading
import com.algorand.android.designsystem.R
import com.algorand.android.designsystem.ScreenState
import com.algorand.android.transactionhistoryui.model.TransactionLoadStatePreview
import java.io.IOException
import javax.inject.Inject

internal class TransactionLoadStatePreviewMapperImpl @Inject constructor() : TransactionLoadStatePreviewMapper {

    override fun invoke(
        combinedLoadStates: CombinedLoadStates,
        itemCount: Int,
        isLastStateError: Boolean
    ): TransactionLoadStatePreview {
        return TransactionLoadStatePreview(
            isTransactionListVisible = isTransactionListVisible(combinedLoadStates, itemCount, isLastStateError),
            isScreenStateViewVisible = isScreenStateViewVisible(combinedLoadStates, itemCount, isLastStateError),
            screenStateViewType = getScreenStateViewType(combinedLoadStates, itemCount),
            isLoading = (combinedLoadStates.refresh is Loading) || (combinedLoadStates.append is Loading),
        )
    }

    private fun isTransactionListVisible(
        combinedLoadStates: CombinedLoadStates,
        itemCount: Int,
        isLastStateError: Boolean
    ): Boolean {
        return (combinedLoadStates.refresh is LoadState.Error).not() &&
            (isLastStateError && combinedLoadStates.refresh is Loading).not() &&
            itemCount != 0
    }

    private fun isScreenStateViewVisible(
        combinedLoadStates: CombinedLoadStates,
        itemCount: Int,
        isLastStateError: Boolean
    ): Boolean {
        val isLoading = combinedLoadStates.refresh is Loading
        val isCurrentStateError = combinedLoadStates.refresh is LoadState.Error
        val isEmptyAfterLoading = isLoading.not() && itemCount == 0 && isCurrentStateError.not()
        return isEmptyAfterLoading || isCurrentStateError || (isLoading && isLastStateError)
    }

    private fun getScreenStateViewType(combinedLoadStates: CombinedLoadStates, itemCount: Int): ScreenState? {
        val isCurrentStateError = combinedLoadStates.refresh is LoadState.Error
        val isLoading = combinedLoadStates.refresh is Loading
        val isEmpty = isLoading.not() && itemCount == 0 && isCurrentStateError.not()

        return when {
            isCurrentStateError -> {
                val throwable = (combinedLoadStates.refresh as LoadState.Error).error
                if (throwable is IOException) {
                    ScreenState.ConnectionError()
                } else {
                    // TODO: 7.01.2022 Specify error state action and message
                    ScreenState.DefaultError()
                }
            }
            isEmpty -> {
                ScreenState.CustomState(
                    title = R.string.no_transactions,
                    description = R.string.there_are_no
                )
            }
            else -> null
        }
    }
}
