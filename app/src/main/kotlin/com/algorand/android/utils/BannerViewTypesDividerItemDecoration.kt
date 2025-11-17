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

package com.algorand.android.utils

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.algorand.android.modules.accounts.ui.view.AccountsAdapter

class BannerViewTypesDividerItemDecoration(
    private val bannerItemTypes: List<Int>,
    private val marginSize: Int
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        val adapter = parent.adapter as? AccountsAdapter
        if (adapter != null && position != RecyclerView.NO_POSITION && position < adapter.itemCount - 1) {
            val currentType = adapter.getItemViewType(position)
            val nextType = adapter.getItemViewType(position + 1)
            if (currentType in bannerItemTypes && nextType in bannerItemTypes) {
                outRect.bottom = marginSize
            } else {
                outRect.bottom = 0
            }
        } else {
            outRect.bottom = 0
        }
    }
}
