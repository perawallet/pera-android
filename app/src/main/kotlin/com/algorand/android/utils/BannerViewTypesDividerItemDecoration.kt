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
