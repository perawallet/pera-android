package com.algorand.android.accountcore.ui.model

import android.os.Parcelable
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import kotlinx.parcelize.Parcelize

@Parcelize
data class AccountIconDrawablePreview(
    @ColorRes
    val backgroundColorResId: Int,
    @ColorRes
    val iconTintResId: Int,
    @DrawableRes
    val iconResId: Int
) : Parcelable
