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

package com.algorand.android.modules.baseresult.ui.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.algorand.android.models.BaseDiffUtil
import com.algorand.android.models.BaseViewHolder
import com.algorand.android.modules.baseresult.ui.adapter.viewholder.ResultAccountViewHolder
import com.algorand.android.modules.baseresult.ui.adapter.viewholder.ResultDescriptionViewHolder
import com.algorand.android.modules.baseresult.ui.adapter.viewholder.ResultIconViewHolder
import com.algorand.android.modules.baseresult.ui.adapter.viewholder.ResultInfoBoxViewHolder
import com.algorand.android.modules.baseresult.ui.adapter.viewholder.ResultTitleViewHolder
import com.algorand.android.modules.baseresult.ui.model.ResultListItem

class BaseResultAdapter(
    private val accountItemListener: AccountItemListener,
    private val descriptionItemListener: DescriptionItemListener? = null
) : ListAdapter<ResultListItem, BaseViewHolder<ResultListItem>>(BaseDiffUtil()) {

    private val resultAccountListener = ResultAccountViewHolder.Listener { accountAddress ->
        accountItemListener.onAccountLongPressed(accountAddress)
    }

    private val resultDescriptionListener = ResultDescriptionViewHolder.Listener {
        descriptionItemListener?.onDescriptionUrlClicked()
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position).itemType.ordinal
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<ResultListItem> {
        return when (viewType) {
            ResultListItem.ItemType.RESULT_ICON_ITEM.ordinal -> createResultIconViewHolder(parent)
            ResultListItem.ItemType.RESULT_TITLE_ITEM.ordinal -> createResultTitleViewHolder(parent)
            ResultListItem.ItemType.RESULT_DESCRIPTION_ITEM.ordinal -> createResultDescriptionViewHolder(parent)
            ResultListItem.ItemType.RESULT_INFO_BOX_ITEM.ordinal -> createResultInfoBoxViewHolder(parent)
            ResultListItem.ItemType.RESULT_ACCOUNT_ITEM.ordinal -> createResultAccountViewHolder(parent)
            else -> throw IllegalArgumentException("$logTag : Unknown viewType = $viewType")
        }
    }

    private fun createResultAccountViewHolder(parent: ViewGroup): ResultAccountViewHolder {
        return ResultAccountViewHolder.create(parent, resultAccountListener)
    }

    private fun createResultDescriptionViewHolder(parent: ViewGroup): ResultDescriptionViewHolder {
        return ResultDescriptionViewHolder.create(parent, resultDescriptionListener)
    }

    private fun createResultIconViewHolder(parent: ViewGroup): ResultIconViewHolder {
        return ResultIconViewHolder.create(parent)
    }

    private fun createResultInfoBoxViewHolder(parent: ViewGroup): ResultInfoBoxViewHolder {
        return ResultInfoBoxViewHolder.create(parent)
    }

    private fun createResultTitleViewHolder(parent: ViewGroup): ResultTitleViewHolder {
        return ResultTitleViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: BaseViewHolder<ResultListItem>, position: Int) {
        holder.bind(getItem(position))
    }

    fun interface AccountItemListener {
        fun onAccountLongPressed(accountAddress: String)
    }

    fun interface DescriptionItemListener {
        fun onDescriptionUrlClicked()
    }

    companion object {
        private val logTag = BaseResultAdapter::class.simpleName
    }
}
