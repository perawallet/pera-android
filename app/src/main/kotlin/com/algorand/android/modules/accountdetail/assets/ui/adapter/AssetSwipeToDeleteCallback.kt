/*
 * Copyright 2022-2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 *  limitations under the License
 *
 */

package com.algorand.android.modules.accountdetail.assets.ui.adapter

import android.graphics.Canvas
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.LEFT
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.algorand.android.R
import com.algorand.android.modules.accountdetail.assets.ui.model.AccountDetailAssetsItem.ItemType.ASSET
import com.algorand.android.modules.accountdetail.assets.ui.model.AccountDetailAssetsItem.ItemType.NFT
import com.algorand.wallet.asset.domain.util.AssetConstants.ALGO_ID
import kotlin.math.absoluteValue

class AssetSwipeToDeleteCallback(private val onSwiped: (Int) -> Unit) : ItemTouchHelper.SimpleCallback(0, LEFT) {

    private var triggerListener: Boolean = false
    private var isSwipeCurrentlyActive: Boolean = false

    override fun onMove(recyclerView: RecyclerView, viewHolder: ViewHolder, target: ViewHolder): Boolean = false

    override fun getSwipeThreshold(viewHolder: ViewHolder): Float = SWIPE_THRESHOLD_PERCENT

    override fun getSwipeEscapeVelocity(defaultValue: Float): Float = Float.MAX_VALUE

    override fun onSwiped(viewHolder: ViewHolder, direction: Int) {
        // Nothing to do here since we handle the swipe action in onChildDraw
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE && viewHolder.isSwipeable()) {
            checkSwipeStatus(isCurrentlyActive, viewHolder, dX)
            val maxSwipeDistance = viewHolder.itemView.width * MAX_SWIPE_PERCENT
            val clampedDX = dX.coerceAtLeast(-maxSwipeDistance)
            if (clampedDX < 0) {
                c.drawBackground(viewHolder.itemView, clampedDX)
                c.drawIcon(viewHolder.itemView, clampedDX)
            }
            super.onChildDraw(c, recyclerView, viewHolder, clampedDX, dY, actionState, isCurrentlyActive)
        } else {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }
    }

    private fun checkSwipeStatus(isCurrentlyActive: Boolean, viewHolder: ViewHolder, dX: Float) {
        val swipeThreshold = viewHolder.itemView.width * SWIPE_ACCEPTANCE_PERCENT
        val position = viewHolder.bindingAdapterPosition
        if (dX == 0f) {
            if (triggerListener) {
                if (position != RecyclerView.NO_POSITION) onSwiped(position)
                triggerListener = false
            }
        }
        if (isSwipeCurrentlyActive && !isCurrentlyActive && dX.absoluteValue > swipeThreshold) {
            if (position != RecyclerView.NO_POSITION) triggerListener = true
        }
        isSwipeCurrentlyActive = isCurrentlyActive
    }

    override fun getSwipeDirs(recyclerView: RecyclerView, viewHolder: ViewHolder): Int {
        val position = viewHolder.bindingAdapterPosition
        if (position == RecyclerView.NO_POSITION) return 0
        val adapter = (recyclerView.adapter as? ConcatAdapter)?.adapters?.getOrNull(1)
        val itemViewType = adapter?.getItemViewType(position) ?: return 0
        val isAssetOrNFT = itemViewType == ASSET.viewType || itemViewType == NFT.viewType
        return if (isAssetOrNFT && viewHolder.isSwipeable()) {
            super.getSwipeDirs(recyclerView, viewHolder)
        } else {
            0
        }
    }

    private fun Canvas.drawBackground(itemView: View, clampedDX: Float) {
        val background = ContextCompat.getColor(itemView.context, R.color.negative)
        val backgroundDrawable = android.graphics.drawable.ColorDrawable(background)
        backgroundDrawable.setBounds(itemView.right + clampedDX.toInt(), itemView.top, itemView.right, itemView.bottom)
        backgroundDrawable.draw(this)
    }

    private fun Canvas.drawIcon(itemView: View, clampedDX: Float) {
        val icon = ContextCompat.getDrawable(itemView.context, R.drawable.ic_trash)
        icon?.let {
            val iconSize = itemView.context.resources.getDimensionPixelSize(R.dimen.account_icon_size_normal)
            val iconMargin = itemView.context.resources.getDimensionPixelSize(R.dimen.spacing_large)
            val visibleAreaLeft = itemView.right + clampedDX.toInt()
            val visibleAreaRight = itemView.right

            val iconRight = visibleAreaRight - iconMargin
            val iconLeft = iconRight - iconSize
            val iconLimitedLeft = maxOf(iconLeft, visibleAreaLeft)
            if (iconLimitedLeft < iconRight) {
                val iconTop = itemView.top + (itemView.height - iconSize) / 2
                val iconBottom = iconTop + iconSize

                it.setBounds(iconLimitedLeft, iconTop, iconRight, iconBottom)
                it.setTint(ContextCompat.getColor(itemView.context, android.R.color.white))
                it.draw(this)
            }
        }
    }

    private fun ViewHolder.isSwipeable(): Boolean {
        return when (this) {
            is OwnedAssetViewHolder -> assetId != null && assetId != ALGO_ID
            is OwnedNFTViewHolder -> true
            else -> false
        }
    }

    companion object Companion {
        private const val MAX_SWIPE_PERCENT = 0.4f
        private const val SWIPE_ACCEPTANCE_PERCENT = 0.35f
        private const val SWIPE_THRESHOLD_PERCENT = 1.0f
    }
}
