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

package com.algorand.android.mapper

import com.algorand.android.R
import com.algorand.android.modules.accounts.ui.model.AccountPreview
import com.algorand.android.modules.accounts.domain.model.BasePortfolioValueItem
import com.algorand.android.modules.accounts.ui.model.BaseAccountListItem
import javax.inject.Inject

class AccountPreviewMapper @Inject constructor(
    private val bottomGlobalErrorMapper: BottomGlobalErrorMapper
) {

    fun getEmptyAccountListState(): AccountPreview {
        return AccountPreview(
            isEmptyStateVisible = true,
            isFullScreenAnimatedLoadingVisible = false,
            isBlockingLoadingVisible = false,
            isMotionLayoutTransitionEnabled = false,
            portfolioValuesBackgroundRes = R.color.transparent,
            isSuccessStateVisible = false,
            hasNewNotification = false
        )
    }

    fun getFullScreenLoadingState(): AccountPreview {
        return AccountPreview(
            isEmptyStateVisible = false,
            isFullScreenAnimatedLoadingVisible = true,
            isBlockingLoadingVisible = false,
            isMotionLayoutTransitionEnabled = false,
            portfolioValuesBackgroundRes = R.color.transparent,
            isSuccessStateVisible = false,
            hasNewNotification = false
        )
    }

    fun getAllAccountsErrorState(
        accountListItems: List<BaseAccountListItem>,
        errorCode: Int?,
        errorPortfolioValueItem: BasePortfolioValueItem.ErrorPortfolioValueItem
    ): AccountPreview {
        return AccountPreview(
            isEmptyStateVisible = false,
            isFullScreenAnimatedLoadingVisible = false,
            isBlockingLoadingVisible = false,
            accountListItems = accountListItems,
            bottomGlobalError = bottomGlobalErrorMapper.mapToBottomGlobalError(errorCode),
            portfolioValueItem = errorPortfolioValueItem,
            isMotionLayoutTransitionEnabled = true,
            portfolioValuesBackgroundRes = R.color.hero_bg,
            isSuccessStateVisible = true,
            hasNewNotification = false
        )
    }

    fun getSuccessAccountPreview(
        accountListItems: List<BaseAccountListItem>,
        portfolioValueItem: BasePortfolioValueItem?,
        hasNewNotification: Boolean,
        assetInboxCount: Int
    ): AccountPreview {
        return AccountPreview(
            isEmptyStateVisible = false,
            isFullScreenAnimatedLoadingVisible = false,
            isBlockingLoadingVisible = false,
            accountListItems = accountListItems,
            portfolioValueItem = portfolioValueItem,
            isMotionLayoutTransitionEnabled = true,
            portfolioValuesBackgroundRes = R.color.hero_bg,
            isSuccessStateVisible = true,
            hasNewNotification = hasNewNotification,
            assetInboxCount = assetInboxCount
        )
    }
}
