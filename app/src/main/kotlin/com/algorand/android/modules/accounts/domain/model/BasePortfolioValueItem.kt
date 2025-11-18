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

package com.algorand.android.modules.accounts.domain.model

import android.content.Context
import androidx.annotation.StringRes
import com.algorand.android.ui.common.amount.AmountRenderer

sealed class BasePortfolioValueItem {

    abstract val titleColorResId: Int
    abstract val errorStringResId: Int?
    abstract fun getPrimaryAccountValue(context: Context): String
    abstract fun getSecondaryAccountValue(context: Context): String

    data class SuccessPortfolioValueItem(
        override val titleColorResId: Int,
        @param:StringRes override val errorStringResId: Int? = null,
        val primaryAmountRenderer: AmountRenderer,
        val secondaryAmountRenderer: AmountRenderer
    ) : BasePortfolioValueItem() {
        override fun getPrimaryAccountValue(context: Context): String = primaryAmountRenderer.getDisplayValue()
        override fun getSecondaryAccountValue(context: Context): String = secondaryAmountRenderer.getDisplayValue()
    }

    data class ErrorPortfolioValueItem(
        override val titleColorResId: Int,
        @param:StringRes override val errorStringResId: Int?,
        @param:StringRes val primaryAccountValueErrorResId: Int,
        @param:StringRes val secondaryAccountValueErrorResId: Int
    ) : BasePortfolioValueItem() {
        override fun getPrimaryAccountValue(context: Context): String = context.getString(primaryAccountValueErrorResId)

        override fun getSecondaryAccountValue(context: Context): String =
            context.getString(secondaryAccountValueErrorResId)
    }

    data class PartialErrorPortfolioValueItem(
        override val titleColorResId: Int,
        @param:StringRes override val errorStringResId: Int,
        val primaryAmountRenderer: AmountRenderer,
        val secondaryAmountRenderer: AmountRenderer
    ) : BasePortfolioValueItem() {
        override fun getPrimaryAccountValue(context: Context): String = primaryAmountRenderer.getDisplayValue()
        override fun getSecondaryAccountValue(context: Context): String = secondaryAmountRenderer.getDisplayValue()
    }
}
